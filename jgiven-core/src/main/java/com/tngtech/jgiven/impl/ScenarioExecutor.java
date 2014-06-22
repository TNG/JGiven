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
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import net.sf.cglib.proxy.Enhancer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.tngtech.jgiven.annotation.AfterScenario;
import com.tngtech.jgiven.annotation.AfterStage;
import com.tngtech.jgiven.annotation.BeforeScenario;
import com.tngtech.jgiven.annotation.BeforeStage;
import com.tngtech.jgiven.annotation.Hidden;
import com.tngtech.jgiven.annotation.IntroWord;
import com.tngtech.jgiven.annotation.NotImplementedYet;
import com.tngtech.jgiven.annotation.ScenarioRule;
import com.tngtech.jgiven.annotation.ScenarioStage;
import com.tngtech.jgiven.exception.FailIfPassedException;
import com.tngtech.jgiven.exception.JGivenUserException;
import com.tngtech.jgiven.impl.inject.ValueInjector;
import com.tngtech.jgiven.impl.intercept.InvocationMode;
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

    /**
     * Measures the stack depth of methods called on the step definition object.
     * Only the top-level method calls are used for reporting. 
     */
    private final AtomicInteger stackDepth = new AtomicInteger();

    private final Map<Class<?>, StageState> stages = Maps.newLinkedHashMap();

    private final List<Object> scenarioRules = Lists.newArrayList();

    private final ValueInjector injector = new ValueInjector();
    private ScenarioListener listener = new NoOpScenarioListener();
    private final StepMethodHandler methodHandler = new MethodHandler();
    private final StepMethodInterceptor methodInterceptor = new StepMethodInterceptor( methodHandler, stackDepth );
    private Throwable failedException;
    private boolean failIfPass;
    private boolean suppressExceptions;

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
        public void handleMethod( Object stageInstance, Method paramMethod, Object[] arguments, InvocationMode mode ) throws Throwable {
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
                listener.stepMethodInvoked( paramMethod, Arrays.asList( arguments ), mode );
            }
        }

        @Override
        public void handleThrowable( Throwable t ) throws Throwable {
            failed( t );
        }

    }

    @SuppressWarnings( "unchecked" )
    public <T> T addStage( Class<T> stepsClass ) {
        if( stages.containsKey( stepsClass ) )
            return (T) stages.get( stepsClass ).instance;

        T result = setupCglibProxy( stepsClass );

        stages.put( stepsClass, new StageState( result ) );
        gatherRules( result );
        injectSteps( result );
        return result;
    }

    @SuppressWarnings( "unchecked" )
    private <T> T setupCglibProxy( Class<T> stepsClass ) {
        Enhancer e = new Enhancer();
        e.setSuperclass( stepsClass );
        e.setCallback( methodInterceptor );
        T result = (T) e.create();
        methodInterceptor.enableMethodHandling( true );
        return result;
    }

    public void addIntroWord( String word ) {
        listener.introWordAdded( word );
    }

    @SuppressWarnings( "unchecked" )
    private void gatherRules( Object stage ) {
        ReflectionUtil.forEachField( stage, stage.getClass(), hasAtLeastOneAnnotation( ScenarioRule.class ), new FieldAction() {
            @Override
            public void act( Object object, Field field ) throws Exception {
                log.debug( "Found rule in field: " + field );
                field.setAccessible( true );
                scenarioRules.add( field.get( object ) );
            }
        } );
    }

    private <T> T update( T t ) throws Throwable {
        if( currentStage == t ) { // NOSONAR: reference comparison OK here
            return t;
        }

        if( currentStage == null ) {
            ensureBeforeStepsAreExecuted();
        } else {
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

    private void executeAfterStageMethods( Object stage ) throws Throwable {
        StageState stageState = getStageState( stage );
        if( stageState.afterStageCalled )
            return;
        stageState.afterStageCalled = true;
        executeAnnotatedMethods( stage, AfterStage.class );
    }

    StageState getStageState( Object stage ) {
        return stages.get( stage.getClass().getSuperclass() );
    }

    private void ensureBeforeStepsAreExecuted() throws Throwable {
        if( state != State.INIT )
            return;
        state = State.STARTED;
        methodInterceptor.enableMethodHandling( false );

        try {
            for( Object rule : scenarioRules ) {
                invokeRuleMethod( rule, "before" );
            }

            beforeStepsWereExecuted = true;

            for( StageState stage : stages.values() ) {
                executeBeforeScenarioSteps( stage.instance );
            }
        } catch( Throwable e ) {
            failed( e );
            finished();
            throw e;
        }

        methodInterceptor.enableMethodHandling( true );
    }

    private void executeAnnotatedMethods( Object stage, final Class<? extends Annotation> annotationClass ) throws Throwable {
        log.debug( "Executing methods annotated with @{}", annotationClass.getName() );
        try {
            ReflectionUtil.forEachMethod( stage, stage.getClass(), annotationClass, new MethodAction() {
                @Override
                public void act( Object object, Method method ) throws Exception {
                    ReflectionUtil.invokeMethod( object, method, " with annotation @" + annotationClass.getName() );
                }
            } );
        } catch( JGivenUserException e ) {
            throw e.getCause();
        }
    }

    private void invokeRuleMethod( Object rule, String methodName ) throws Throwable {
        Optional<Method> optionalMethod = ReflectionUtil.findMethodTransitively( rule.getClass(), methodName );
        if( !optionalMethod.isPresent() ) {
            log.debug( "Class {} has no {} method, but was used as ScenarioRule!", rule.getClass(), methodName );
            return;
        }

        try {
            ReflectionUtil.invokeMethod( rule, optionalMethod.get(), " of rule class " + rule.getClass().getName() );
        } catch( JGivenUserException e ) {
            throw e.getCause();
        }
    }

    void executeBeforeStageSteps( Object stage ) throws Throwable {
        executeAnnotatedMethods( stage, BeforeStage.class );
    }

    private void executeBeforeScenarioSteps( Object stage ) throws Throwable {
        executeAnnotatedMethods( stage, BeforeScenario.class );
    }

    public void readScenarioState( Object object ) {
        injector.readValues( object );
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
    public void finished() throws Throwable {
        if( state == FINISHED )
            return;
        if( state != STARTED )
            throw new IllegalStateException( "The Scenario must be in state STARTED in order to finish it, but it is in state " + state );
        state = FINISHED;
        methodInterceptor.enableMethodHandling( false );

        Throwable firstThrownException = failedException;
        if( beforeStepsWereExecuted ) {
            if( currentStage != null ) {
                try {
                    executeAfterStageMethods( currentStage );
                } catch( AssertionError e ) {
                    firstThrownException = logAndGetFirstException( firstThrownException, e );
                } catch( Exception e ) {
                    firstThrownException = logAndGetFirstException( firstThrownException, e );
                }
            }

            for( StageState stage : reverse( newArrayList( stages.values() ) ) ) {
                try {
                    executeAnnotatedMethods( stage.instance, AfterScenario.class );
                } catch( AssertionError e ) {
                    firstThrownException = logAndGetFirstException( firstThrownException, e );
                } catch( Exception e ) {
                    firstThrownException = logAndGetFirstException( firstThrownException, e );
                }
            }
        }

        for( Object rule : Lists.reverse( scenarioRules ) ) {
            try {
                invokeRuleMethod( rule, "after" );
            } catch( AssertionError e ) {
                firstThrownException = logAndGetFirstException( firstThrownException, e );
            } catch( Exception e ) {
                firstThrownException = logAndGetFirstException( firstThrownException, e );
            }
        }

        failedException = firstThrownException;

        if( !suppressExceptions && failedException != null ) {
            throw failedException;
        }

        if( failIfPass && failedException == null ) {
            throw new FailIfPassedException();
        }
    }

    private Throwable logAndGetFirstException( Throwable firstThrownException, Throwable newException ) {
        log.error( newException.getMessage(), newException );
        return firstThrownException == null ? newException : firstThrownException;
    }

    @SuppressWarnings( "unchecked" )
    public void injectSteps( Object stage ) {
        ReflectionUtil.forEachField( stage, stage.getClass(),
            ReflectionUtil.hasAtLeastOneAnnotation( ScenarioStage.class ), new FieldAction() {
                @Override
                public void act( Object object, Field field ) throws Exception {
                    Object steps = addStage( field.getType() );
                    ReflectionUtil.setField( field, object, steps, ", annoted with @ScenarioStage" );
                }
            } );
    }

    public void failed( Throwable e ) {
        listener.scenarioFailed( e );
        methodInterceptor.disableMethodExecution();
        failedException = e;
    }

    /**
     * Starts a scenario with the given description.
     * 
     * @param description the description of the scenario
     */
    public void startScenario( String description ) {
        listener.scenarioStarted( description );
    }

    /**
     * Starts the scenario with the given method and arguments.
     * Derives the description from the method name.
     * @param method the method that started the scenario
     * @param arguments the test arguments with their parameter names
     */
    public void startScenario( Method method, List<NamedArgument> arguments ) {
        listener.scenarioStarted( method, arguments );

        if( method.isAnnotationPresent( NotImplementedYet.class ) ) {
            NotImplementedYet annotation = method.getAnnotation( NotImplementedYet.class );

            if( annotation.failIfPass() ) {
                failIfPass();
            } else if( !annotation.executeSteps() ) {
                methodInterceptor.disableMethodExecution();
            }
            suppressExceptions = true;
        }
    }

    public void setListener( ScenarioListener listener ) {
        this.listener = listener;
    }

    public void failIfPass() {
        failIfPass = true;
    }

}
