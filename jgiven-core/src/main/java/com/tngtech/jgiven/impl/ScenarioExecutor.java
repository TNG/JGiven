package com.tngtech.jgiven.impl;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Lists.reverse;
import static com.tngtech.jgiven.impl.ScenarioExecutor.State.FINISHED;
import static com.tngtech.jgiven.impl.ScenarioExecutor.State.STARTED;
import static com.tngtech.jgiven.impl.util.ReflectionUtil.hasAtLeastOneAnnotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import net.sf.cglib.proxy.Enhancer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Optional;
import com.google.common.base.Throwables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.tngtech.jgiven.annotation.AfterScenario;
import com.tngtech.jgiven.annotation.AfterStage;
import com.tngtech.jgiven.annotation.BeforeScenario;
import com.tngtech.jgiven.annotation.BeforeStage;
import com.tngtech.jgiven.annotation.Hidden;
import com.tngtech.jgiven.annotation.IntroWord;
import com.tngtech.jgiven.annotation.ScenarioRule;
import com.tngtech.jgiven.annotation.ScenarioStage;
import com.tngtech.jgiven.impl.inject.ValueInjector;
import com.tngtech.jgiven.impl.intercept.NoOpScenarioListener;
import com.tngtech.jgiven.impl.intercept.ScenarioListener;
import com.tngtech.jgiven.impl.intercept.StepMethodHandler;
import com.tngtech.jgiven.impl.intercept.StepMethodInterceptor;
import com.tngtech.jgiven.impl.util.ReflectionUtil;
import com.tngtech.jgiven.impl.util.ReflectionUtil.FieldAction;
import com.tngtech.jgiven.impl.util.ReflectionUtil.MethodAction;
import com.tngtech.jgiven.integration.CanWire;

/**
 * Main class of JGiven for executing scenarios
 */
public class ScenarioExecutor {
    private static final Logger log = LoggerFactory.getLogger( ScenarioExecutor.class );

    public enum State {
        INIT,
        STARTED,
        FINISHED
    }

    private Object currentStage;
    private State state = State.INIT;
    private boolean beforeStepsWereExecuted;
    private Date startDate;

    /**
     * Measures the stack depth of methods called on the step definition object.
     * Only the top-level method calls are used for reporting. 
     */
    private final AtomicInteger stackDepth = new AtomicInteger();

    private final Map<Class<?>, StageState> stages = Maps.newLinkedHashMap();

    private final List<Object> scenarioRules = Lists.newArrayList();

    private final ValueInjector injector = new ValueInjector();
    private ScenarioListener listener = new NoOpScenarioListener();

    public ScenarioExecutor() {
        injector.injectValueByType( ScenarioExecutor.class, this );
    }

    static class StageState {
        final Object instance;
        boolean afterStageCalled;
        boolean beforeStageCalled;

        StageState( Object instance ) {
            this.instance = instance;
        }
    }

    class MethodHandler implements StepMethodHandler {
        @Override
        public void handleMethod( Object stageInstance, Method paramMethod, Object[] arguments ) {
            if( paramMethod.isSynthetic() )
                return;

            if( paramMethod.isAnnotationPresent( AfterStage.class ) ||
                    paramMethod.isAnnotationPresent( BeforeStage.class ) ||
                    paramMethod.isAnnotationPresent( BeforeScenario.class ) ||
                    paramMethod.isAnnotationPresent( AfterScenario.class ) ||
                    paramMethod.isAnnotationPresent( Hidden.class ) )
                return;

            update( stageInstance );

            if( paramMethod.isAnnotationPresent( IntroWord.class ) ) {
                listener.introWordAdded( paramMethod.getName() );
            } else {
                listener.stepMethodInvoked( paramMethod, Arrays.asList( arguments ) );
            }
        }

        @Override
        public void handleThrowable( Throwable t ) {
            failed( t );
            finished();
        }

    }

    @SuppressWarnings( "unchecked" )
    public <T> T addSteps( Class<T> stepsClass ) {
        if( stages.containsKey( stepsClass ) )
            return (T) stages.get( stepsClass ).instance;

        Enhancer e = new Enhancer();
        e.setSuperclass( stepsClass );
        e.setCallback( new StepMethodInterceptor( new MethodHandler(), stackDepth ) );
        T result = (T) e.create();
        stages.put( stepsClass, new StageState( result ) );
        gatherRules( result );
        injectSteps( result );
        return result;
    }

    public void addIntroWord( String word ) {
        listener.introWordAdded( word );
    }

    @SuppressWarnings( "unchecked" )
    private void gatherRules( Object object ) {
        ReflectionUtil.forEachField( object, object.getClass(), hasAtLeastOneAnnotation( ScenarioRule.class ), new FieldAction() {
            @Override
            public void act( Object object, Field field ) throws Exception {
                log.debug( "Found rule in field: " + field );
                field.setAccessible( true );
                scenarioRules.add( field.get( object ) );
            }
        } );
    }

    public <T> T when( T whenStage ) {
        return update( whenStage );
    }

    private <T> T update( T t ) {
        if( currentStage == t )
            return t;

        if( currentStage != null ) {
            executeAfterStageMethods( currentStage );
            readScenarioState( currentStage );
        }

        injector.updateValues( t );

        StageState stageState = getStageState( t );
        if( !stageState.beforeStageCalled ) {
            stageState.beforeStageCalled = true;
            executeBeforeStageSteps( t );
        }

        currentStage = t;
        return t;
    }

    private void executeAfterStageMethods( Object stage ) {
        StageState stageState = getStageState( stage );
        if( stageState.afterStageCalled )
            return;
        stageState.afterStageCalled = true;
        executeAnnotatedMethods( stage, AfterStage.class );
    }

    StageState getStageState( Object stage ) {
        return stages.get( stage.getClass().getSuperclass() );
    }

    private void ensureBeforeStepsAreExecuted() {
        if( state != State.INIT )
            return;
        state = State.STARTED;
        startDate = new Date();

        try {
            for( Object rule : scenarioRules ) {
                invokeRuleMethod( rule, "before" );
            }

            beforeStepsWereExecuted = true;

            for( StageState stage : stages.values() ) {
                executeBeforeScenarioSteps( stage.instance );
            }
        } catch( Exception e ) {
            finished();
            throw Throwables.propagate( e );
        }
    }

    private void executeAnnotatedMethods( Object stage, Class<? extends Annotation> annotationClass ) {
        log.debug( "Executing methods annotated with @" + annotationClass.getName() );
        ReflectionUtil.forEachMethod( stage, stage.getClass(), annotationClass, new MethodAction() {
            @Override
            public void act( Object object, Method method ) throws Exception {
                log.debug( "Executing method " + method );
                method.setAccessible( true );
                method.invoke( object );
            }
        } );
    }

    private void invokeRuleMethod( Object rule, String methodName ) {
        Optional<Method> optionalMethod = ReflectionUtil.findMethodTransitively( rule.getClass(), methodName );
        if( !optionalMethod.isPresent() ) {
            log.debug( "Class " + rule.getClass() + " has no " + methodName + " method, but was used as ScenarioRule!" );
        }
        log.debug( "Executing method " + methodName + " of rule class " + rule.getClass() );
        optionalMethod.get().setAccessible( true );
        try {
            optionalMethod.get().invoke( rule );
        } catch( Exception e ) {
            throw Throwables.propagate( e );
        }
    }

    void executeBeforeStageSteps( Object stage ) {
        executeAnnotatedMethods( stage, BeforeStage.class );
    }

    private void executeBeforeScenarioSteps( Object stage ) {
        executeAnnotatedMethods( stage, BeforeScenario.class );
    }

    public <T> void injectValueByType( Class<T> clazz, T idGenerator ) {
        injector.injectValueByType( clazz, idGenerator );
    }

    public void readScenarioState( Object object ) {
        injector.readValues( object );
    }

    public Date getStartDate() {
        return startDate;
    }

    /**
     * Used for DI frameworks to inject values into stages
     */
    public void wireSteps( CanWire canWire ) {
        for( StageState steps : stages.values() ) {
            canWire.wire( steps.instance );
        }
    }

    /**
     * Has to be called when the scenario is finished in order to execute after methods
     */
    public void finished() {
        if( state == FINISHED )
            return;
        if( state != STARTED )
            throw new IllegalStateException( "The Scenario must be in state STARTED in order to finish it, but it is in state " + state );

        Exception lastThrownException = null;
        if( beforeStepsWereExecuted ) {
            for( StageState stage : reverse( newArrayList( stages.values() ) ) ) {
                try {
                    executeAnnotatedMethods( stage.instance, AfterScenario.class );
                } catch( Exception e ) {
                    log.error( e.getMessage(), e );
                    lastThrownException = e;
                }
            }
        }

        for( Object rule : Lists.reverse( scenarioRules ) ) {
            try {
                invokeRuleMethod( rule, "after" );
            } catch( Exception e ) {
                log.error( e.getMessage(), e );
                lastThrownException = e;
            }
        }

        state = FINISHED;
        if( lastThrownException != null ) {
            new RuntimeException( "Exception occurred during the execution of after methods", lastThrownException );
        }
    }

    @SuppressWarnings( "unchecked" )
    public void injectSteps( Object object ) {
        ReflectionUtil.forEachField( object, object.getClass(),
            ReflectionUtil.hasAtLeastOneAnnotation( ScenarioStage.class ), new FieldAction() {
                @Override
                public void act( Object object, Field field ) throws Exception {
                    field.setAccessible( true );
                    Class<?> type = field.getType();
                    Object steps = addSteps( type );
                    field.set( object, steps );
                }
            } );
    }

    public void succeeded() {
        listener.scenarioSucceeded();
    }

    public void failed( Throwable e ) {
        listener.scenarioFailed( e );
    }

    /**
     * Starts a scenario with the given description.
     * 
     * @param description the description of the scenario
     */
    public void startScenario( String description ) {
        ensureBeforeStepsAreExecuted();
        listener.scenarioStarted( description );
    }

    /**
     * Starts the scenario with the given method and arguments.
     * Derives the description from the method name.
     * @param method the method that started the scenario
     * @param arguments the arguments of the method invocation
     */
    public void startScenario( Method method, List<?> arguments ) {
        ensureBeforeStepsAreExecuted();
        listener.scenarioStarted( method, arguments );
    }

    public void setListener( ScenarioListener listener ) {
        this.listener = listener;
    }

}
