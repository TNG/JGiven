package com.tngtech.jgiven.impl.intercept;

import static com.tngtech.jgiven.report.model.InvocationMode.NORMAL;
import static com.tngtech.jgiven.report.model.InvocationMode.PENDING;
import static com.tngtech.jgiven.report.model.InvocationMode.SKIPPED;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;

import com.tngtech.jgiven.impl.util.ThrowableUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tngtech.jgiven.annotation.DoNotIntercept;
import com.tngtech.jgiven.annotation.Hidden;
import com.tngtech.jgiven.annotation.NestedSteps;
import com.tngtech.jgiven.annotation.Pending;
import com.tngtech.jgiven.impl.ScenarioExecutor;
import com.tngtech.jgiven.impl.util.ParameterNameUtil;
import com.tngtech.jgiven.report.model.InvocationMode;
import com.tngtech.jgiven.report.model.NamedArgument;

public class StepInterceptorImpl implements StepInterceptor {
    private static final Logger log = LoggerFactory.getLogger( StepInterceptorImpl.class );

    private static final int INITIAL_MAX_STEP_DEPTH = 1;

    private ScenarioExecutor scenarioExecutor;

    private StageTransitionHandler stageTransitionHandler;

    private ScenarioListener listener;

    /**
     * Contains the stack of call receivers. This is used to update
     * the state of a parent stage after a call to a child stage has returned
     */
    protected final Stack<Object> stageStack = new Stack<Object>();

    private int maxStepDepth = INITIAL_MAX_STEP_DEPTH;

    private InvocationMode defaultInvocationMode = InvocationMode.NORMAL;

    /**
     * Whether methods should be intercepted or not
     */
    private boolean interceptingEnabled;

    /**
     * Whether step methods are actually executed or just skipped
     */
    private boolean methodExecutionEnabled = true;

    /**
     * Whether all exceptions should be suppressed and not be rethrown
     */
    private boolean suppressExceptions = true;

    public StepInterceptorImpl(ScenarioExecutor scenarioExecutor, ScenarioListener listener, StageTransitionHandler stageTransitionHandler) {
        this.scenarioExecutor = scenarioExecutor;
        this.listener = listener;
        this.stageTransitionHandler = stageTransitionHandler;
    }

    public final Object intercept( final Object receiver, Method method, final Object[] parameters, Invoker invoker ) throws Throwable {
        if( !shouldInterceptMethod( method ) ) {
            return invoker.proceed();
        }

        int currentStackDepth = stageStack.size();
        Object parentStage = null;
        if( !stageStack.isEmpty() ) {
            parentStage = stageStack.peek();
        }

        stageStack.push( receiver );
        try {
            stageTransitionHandler.enterStage( parentStage, receiver );

            return doIntercept( receiver, method, parameters, invoker, currentStackDepth );
        } finally {
            stageStack.pop();
            stageTransitionHandler.leaveStage( parentStage, receiver );
        }
    }

    private Object doIntercept(Object receiver, Method method, Object[] parameters, Invoker invoker, int currentStackDepth )
            throws Throwable {
        long started = System.nanoTime();

        InvocationMode mode = getInvocationMode( receiver, method );

        boolean hasNestedSteps = method.isAnnotationPresent( NestedSteps.class );

        boolean handleMethod = shouldHandleMethod( method );
        if( handleMethod ) {
            handleMethod( receiver, method, parameters, mode, hasNestedSteps );
        }

        if( mode == SKIPPED || mode == PENDING ) {
            return returnReceiverOrNull( receiver, method );
        }

        if( hasNestedSteps ) {
            maxStepDepth++;
        }

        try {
            return invoker.proceed();
        } catch( Exception e ) {
            return handleThrowable( receiver, method, e, System.nanoTime() - started, handleMethod );
        } catch( AssertionError e ) {
            return handleThrowable( receiver, method, e, System.nanoTime() - started, handleMethod );
        } finally {
            if( hasNestedSteps ) {
                maxStepDepth--;
            }
            if( handleMethod ) {
                handleMethodFinished( System.nanoTime() - started, hasNestedSteps );
            }
        }
    }

    private boolean shouldHandleMethod( Method method ) {
        if( method.isSynthetic() && !method.isBridge() ) {
            return false;
        }

        if( method.isAnnotationPresent( Hidden.class ) ) {
            return false;
        }

        if( stageStack.size() > maxStepDepth ) {
            return false;
        }

        return true;
    }

    private boolean shouldInterceptMethod( Method method ) {
        return interceptingEnabled
                && method.getDeclaringClass() != Object.class
                && !method.isAnnotationPresent(DoNotIntercept.class);
    }

    protected Object handleThrowable( Object receiver, Method method, Throwable t, long durationInNanos, boolean handleMethod )
            throws Throwable {
        if( handleMethod ) {
            handleThrowable( t );
            return returnReceiverOrNull( receiver, method );
        }
        throw t;
    }

    protected Object returnReceiverOrNull( Object receiver, Method method ) {
        // we assume here that the implementation follows the fluent interface
        // convention and returns the receiver object. If not, we fall back to null
        // and hope for the best.
        if( !method.getReturnType().isAssignableFrom( receiver.getClass() ) ) {
            if( method.getReturnType() != Void.class ) {
                log.warn( "The step method " + method.getName()
                        + " of class " + method.getDeclaringClass().getSimpleName()
                        + " does not follow the fluent interface convention of returning "
                        + "the receiver object. Please change the return type to the SELF type parameter." );
            }
            return null;
        }

        return receiver;
    }

    protected InvocationMode getInvocationMode( Object receiver, Method method ) {
        if( !methodExecutionEnabled ) {
            return SKIPPED;
        }

        if( method.isAnnotationPresent( Pending.class )
            || method.getDeclaringClass().isAnnotationPresent( Pending.class )
            || receiver.getClass().isAnnotationPresent( Pending.class ) ) {
            return PENDING;
        }

        return defaultInvocationMode;
    }

    public void enableMethodInterception(boolean b ) {
        interceptingEnabled = b;
    }

    public void disableMethodExecution() {
        methodExecutionEnabled = false;
    }

    public boolean enableMethodExecution( boolean b ) {
        boolean previousMethodExecution = methodExecutionEnabled;
        methodExecutionEnabled = b;
        return previousMethodExecution;
    }

    public void setSuppressExceptions(boolean b) {
        suppressExceptions = b;
    }

    public void setDefaultInvocationMode(InvocationMode defaultInvocationMode) {
        this.defaultInvocationMode = defaultInvocationMode;
    }

    private void handleMethod(Object stageInstance, Method paramMethod, Object[] arguments, InvocationMode mode,
                              boolean hasNestedSteps ) throws Throwable {

        List<NamedArgument> namedArguments = ParameterNameUtil.mapArgumentsWithParameterNames( paramMethod,
                Arrays.asList( arguments ) );
        listener.stepMethodInvoked( paramMethod, namedArguments, mode, hasNestedSteps );
    }

    private void handleThrowable( Throwable t ) throws Throwable {
        if( ThrowableUtil.isAssumptionException(t) ) {
            throw t;
        }

        listener.stepMethodFailed( t );

        scenarioExecutor.failed( t );

        if (!suppressExceptions) {
            throw t;
        }
    }

    private void handleMethodFinished( long durationInNanos, boolean hasNestedSteps ) {
        listener.stepMethodFinished( durationInNanos, hasNestedSteps );
    }

    public void setScenarioListener(ScenarioListener scenarioListener) {
        this.listener = scenarioListener;
    }
}
