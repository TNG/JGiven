package com.tngtech.jgiven.impl;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Lists.reverse;
import static com.tngtech.jgiven.impl.ScenarioExecutor.State.FINISHED;
import static com.tngtech.jgiven.impl.ScenarioExecutor.State.STARTED;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.tngtech.jgiven.CurrentScenario;
import com.tngtech.jgiven.CurrentStep;
import com.tngtech.jgiven.annotation.*;
import com.tngtech.jgiven.attachment.Attachment;
import com.tngtech.jgiven.exception.FailIfPassedException;
import com.tngtech.jgiven.exception.JGivenMissingRequiredScenarioStateException;
import com.tngtech.jgiven.exception.JGivenUserException;
import com.tngtech.jgiven.impl.inject.ValueInjector;
import com.tngtech.jgiven.impl.intercept.NoOpScenarioListener;
import com.tngtech.jgiven.impl.intercept.ScenarioListener;
import com.tngtech.jgiven.impl.intercept.StageTransitionHandler;
import com.tngtech.jgiven.impl.intercept.StepInterceptorImpl;
import com.tngtech.jgiven.impl.util.FieldCache;
import com.tngtech.jgiven.impl.util.ReflectionUtil;
import com.tngtech.jgiven.impl.util.ReflectionUtil.MethodAction;
import com.tngtech.jgiven.integration.CanWire;
import com.tngtech.jgiven.report.model.NamedArgument;

/**
 * Main class of JGiven for executing scenarios.
 */
public class ScenarioExecutor {
    private static final Logger log = LoggerFactory.getLogger( ScenarioExecutor.class );

    public enum State {
        INIT,
        STARTED,
        FINISHED
    }

    private Object currentTopLevelStage;
    private State state = State.INIT;
    private boolean beforeScenarioMethodsExecuted;

    /**
     * Whether life cycle methods should be executed.
     * This is only false for scenarios that are annotated with @NotImplementedYet
     */
    private boolean executeLifeCycleMethods = true;

    protected final Map<Class<?>, StageState> stages = Maps.newLinkedHashMap();

    private final List<Object> scenarioRules = Lists.newArrayList();

    private final ValueInjector injector = new ValueInjector();
    private StageCreator stageCreator = createStageCreator( new ByteBuddyStageClassCreator() );
    private ScenarioListener listener = new NoOpScenarioListener();
    protected final StageTransitionHandler stageTransitionHandler = new StageTransitionHandlerImpl();
    protected final StepInterceptorImpl methodInterceptor = new StepInterceptorImpl( this, listener, stageTransitionHandler );

    private Throwable failedException;
    private boolean failIfPass;
    private boolean suppressExceptions;

    public ScenarioExecutor() {
        injector.injectValueByType( ScenarioExecutor.class, this );
        injector.injectValueByType( CurrentStep.class, new StepAccessImpl() );
        injector.injectValueByType( CurrentScenario.class, new ScenarioAccessImpl() );
    }

    protected static class StageState {
        final Object instance;
        boolean afterStageCalled;
        boolean beforeStageCalled;
        public Object currentChildStage;

        StageState( Object instance ) {
            this.instance = instance;
        }
    }

    class StepAccessImpl implements CurrentStep {

        @Override
        public void addAttachment( Attachment attachment ) {
            listener.attachmentAdded( attachment );
        }

        @Override
        public void setExtendedDescription( String extendedDescription ) {
            listener.extendedDescriptionUpdated( extendedDescription );
        }

        @Override
        public void setName( String name ) {
            listener.stepNameUpdated( name );
        }
    }

    class ScenarioAccessImpl implements CurrentScenario {

        @Override
        public void addTag( Class<? extends Annotation> annotationClass, String... values ) {
            listener.tagAdded( annotationClass, values );
        }

    }

    class StageTransitionHandlerImpl implements StageTransitionHandler {

        @Override
        public void enterStage( Object parentStage, Object childStage ) throws Throwable {
            if( parentStage == childStage || currentTopLevelStage == childStage ) { // NOSONAR: reference comparison OK
                return;
            }

            // if currentStage == null, this means that no stage at
            // all has been executed, thus we call all beforeScenarioMethods
            if( currentTopLevelStage == null ) {
                ensureBeforeScenarioMethodsAreExecuted();
            } else {
                // in case parentStage == null, this is the first top-level
                // call on this stage, thus we have to call the afterStage methods
                // from the current top level stage
                if( parentStage == null ) {
                    executeAfterStageMethods( currentTopLevelStage );
                    readScenarioState( currentTopLevelStage );
                } else {
                    // as the parent stage is not null, we have a true child call
                    // thus we have to read the state from the parent stage
                    readScenarioState( parentStage );

                    // if there has been a child stage that was executed before
                    // and the new child stage is different, we have to execute
                    // the after stage methods of the previous child stage
                    StageState stageState = getStageState( parentStage );
                    if( stageState.currentChildStage != null && stageState.currentChildStage != childStage
                            && !afterStageMethodsCalled( stageState.currentChildStage ) ) {
                        updateScenarioState( stageState.currentChildStage );
                        executeAfterStageMethods( stageState.currentChildStage );
                        readScenarioState( stageState.currentChildStage );
                    }

                    stageState.currentChildStage = childStage;
                }
            }

            updateScenarioState( childStage );

            StageState stageState = getStageState( childStage );
            if( !stageState.beforeStageCalled ) {
                stageState.beforeStageCalled = true;
                executeBeforeStageSteps( childStage );
            }

            if( parentStage == null ) {
                currentTopLevelStage = childStage;
            }
        }

        @Override
        public void leaveStage( Object parentStage, Object childStage ) throws Throwable {
            if( parentStage == childStage || parentStage == null ) {
                return;
            }

            readScenarioState( childStage );

            // in case we leave a child stage that itself had a child stage
            // we have to execute the after stage method of that transitive child
            StageState childState = getStageState( childStage );
            if( childState.currentChildStage != null ) {
                updateScenarioState( childState.currentChildStage );
                if( executeAfterStageMethods( childState.currentChildStage ) ) {
                    readScenarioState( childState.currentChildStage );
                    updateScenarioState( childStage );
                }
                childState.currentChildStage = null;
            }

            updateScenarioState( parentStage );
        }

    }

    @SuppressWarnings( "unchecked" )
    public <T> T addStage( Class<T> stageClass ) {
        if( stages.containsKey( stageClass ) ) {
            return (T) stages.get( stageClass ).instance;
        }

        T result = stageCreator.createStage( stageClass, methodInterceptor );
        methodInterceptor.enableMethodInterception( true );

        stages.put( stageClass, new StageState( result ) );
        gatherRules( result );
        injectStages( result );
        return result;
    }

    public void addIntroWord( String word ) {
        listener.introWordAdded( word );
    }

    @SuppressWarnings( "unchecked" )
    private void gatherRules( Object stage ) {
        for( Field field : FieldCache.get( stage.getClass() ).getFieldsWithAnnotation( ScenarioRule.class ) ) {
            log.debug( "Found rule in field {} ", field );
            try {
                scenarioRules.add( field.get( stage ) );
            } catch( IllegalAccessException e ) {
                throw new RuntimeException( "Error while reading field " + field, e );
            }
        }

    }

    private <T> void updateScenarioState( T t ) {
        try {
            injector.updateValues( t );
        } catch( JGivenMissingRequiredScenarioStateException e ) {
            if( !suppressExceptions ) {
                throw e;
            }
        }
    }

    private boolean afterStageMethodsCalled( Object stage ) {
        return getStageState( stage ).afterStageCalled;
    }

    private boolean executeAfterStageMethods( Object stage ) throws Throwable {
        StageState stageState = getStageState( stage );
        if( stageState.afterStageCalled ) {
            return false;
        }
        stageState.afterStageCalled = true;
        executeAnnotatedMethods( stage, AfterStage.class );
        return true;
    }

    public StageState getStageState( Object stage ) {
        StageState stageState = stages.get( stage.getClass() );
        return stageState != null ? stageState : stages.get( stage.getClass().getSuperclass() );
    }

    private void ensureBeforeScenarioMethodsAreExecuted() throws Throwable {
        if( state != State.INIT ) {
            return;
        }
        state = STARTED;
        methodInterceptor.enableMethodInterception( false );

        try {
            for( Object rule : scenarioRules ) {
                invokeRuleMethod( rule, "before" );
            }

            beforeScenarioMethodsExecuted = true;

            for( StageState stage : stages.values() ) {
                executeBeforeScenarioMethods( stage.instance );
            }
        } catch( Throwable e ) {
            failed( e );
            finished();
            throw e;
        }

        methodInterceptor.enableMethodInterception( true );
    }

    private void executeAnnotatedMethods( Object stage, final Class<? extends Annotation> annotationClass ) throws Throwable {
        if( !executeLifeCycleMethods ) {
            return;
        }

        log.debug( "Executing methods annotated with @{}", annotationClass.getName() );
        boolean previousMethodExecution = methodInterceptor.enableMethodExecution( true );
        try {
            methodInterceptor.enableMethodInterception( false );
            ReflectionUtil.forEachMethod( stage, stage.getClass(), annotationClass, new MethodAction() {
                @Override
                public void act( Object object, Method method ) throws Exception {
                    ReflectionUtil.invokeMethod( object, method, " with annotation @" + annotationClass.getName() );
                }
            } );
            methodInterceptor.enableMethodInterception( true );
        } catch( JGivenUserException e ) {
            throw e.getCause();
        } finally {
            methodInterceptor.enableMethodExecution( previousMethodExecution );
        }
    }

    private void invokeRuleMethod( Object rule, String methodName ) throws Throwable {
        if( !executeLifeCycleMethods ) {
            return;
        }

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

    private void executeBeforeScenarioMethods( Object stage ) throws Throwable {
        executeAnnotatedMethods( stage, BeforeScenario.class );
    }

    public void readScenarioState( Object object ) {
        injector.readValues( object );
    }

    /**
     * Used for DI frameworks to inject values into stages.
     */
    public void wireSteps( CanWire canWire ) {
        for( StageState steps : stages.values() ) {
            canWire.wire( steps.instance );
        }
    }

    /**
     * Has to be called when the scenario is finished in order to execute after methods.
     */
    public void finished() throws Throwable {
        if( state == FINISHED ) {
            return;
        }

        State previousState = state;

        state = FINISHED;
        methodInterceptor.enableMethodInterception( false );

        try {
            if( previousState == STARTED ) {
                callFinishLifeCycleMethods();
            }
        } finally {
            listener.scenarioFinished();
        }
    }

    private void callFinishLifeCycleMethods() throws Throwable {
        Throwable firstThrownException = failedException;
        if( beforeScenarioMethodsExecuted ) {
            try {
                if( currentTopLevelStage != null ) {
                    executeAfterStageMethods( currentTopLevelStage );
                }
            } catch( AssertionError e ) {
                firstThrownException = logAndGetFirstException( firstThrownException, e );
            } catch( Exception e ) {
                firstThrownException = logAndGetFirstException( firstThrownException, e );
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
    public void injectStages( Object stage ) {
        for( Field field : FieldCache.get( stage.getClass() ).getFieldsWithAnnotation( ScenarioStage.class ) ) {
            Object steps = addStage( field.getType() );
            ReflectionUtil.setField( field, stage, steps, ", annotated with @ScenarioStage" );
        }
    }

    public boolean hasFailed() {
        return failedException != null;
    }

    public Throwable getFailedException() {
        return failedException;
    }

    public void setFailedException( Exception e ) {
        failedException = e;
    }

    public void failed( Throwable e ) {
        if( hasFailed() ) {
            log.error( e.getMessage(), e );
        } else {
            listener.scenarioFailed( e );
            methodInterceptor.disableMethodExecution();
            failedException = e;
        }
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
    public void startScenario( Class<?> testClass, Method method, List<NamedArgument> arguments ) {
        listener.scenarioStarted( testClass, method, arguments );

        if( method.isAnnotationPresent( Pending.class ) ) {
            Pending annotation = method.getAnnotation( Pending.class );

            if( annotation.failIfPass() ) {
                failIfPass();
            } else if( !annotation.executeSteps() ) {
                methodInterceptor.disableMethodExecution();
                executeLifeCycleMethods = false;
            }
            suppressExceptions = true;
        } else if( method.isAnnotationPresent( NotImplementedYet.class ) ) {
            NotImplementedYet annotation = method.getAnnotation( NotImplementedYet.class );

            if( annotation.failIfPass() ) {
                failIfPass();
            } else if( !annotation.executeSteps() ) {
                methodInterceptor.disableMethodExecution();
                executeLifeCycleMethods = false;
            }
            suppressExceptions = true;
        }

    }

    public void setListener( ScenarioListener listener ) {
        this.listener = listener;
        methodInterceptor.setScenarioListener( listener );
    }

    public void failIfPass() {
        failIfPass = true;
    }

    public void addSection( String sectionTitle ) {
        listener.sectionAdded( sectionTitle );
    }

    public void setStageCreator( StageCreator stageCreator ) {
        this.stageCreator = stageCreator;
    }

    public void setStageClassCreator( StageClassCreator stageClassCreator ) {
        this.stageCreator = createStageCreator( stageClassCreator );
    }

    private StageCreator createStageCreator( StageClassCreator stageClassCreator ) {
        return new DefaultStageCreator( new CachingStageClassCreator( stageClassCreator ) );
    }

}
