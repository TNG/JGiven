package com.tngtech.jgiven.impl.intercept;

import static com.tngtech.jgiven.report.model.InvocationMode.DO_NOT_INTERCEPT;
import static com.tngtech.jgiven.report.model.InvocationMode.NORMAL;
import static com.tngtech.jgiven.report.model.InvocationMode.PENDING;
import static com.tngtech.jgiven.report.model.InvocationMode.SKIPPED;

import java.lang.reflect.Method;
import java.util.Stack;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tngtech.jgiven.annotation.DoNotIntercept;
import com.tngtech.jgiven.annotation.Hidden;
import com.tngtech.jgiven.annotation.NestedSteps;
import com.tngtech.jgiven.annotation.NotImplementedYet;
import com.tngtech.jgiven.annotation.Pending;
import com.tngtech.jgiven.impl.ScenarioExecutor;
import com.tngtech.jgiven.report.model.InvocationMode;

public class StepMethodInterceptor {
    private static final Logger log = LoggerFactory.getLogger( StepMethodInterceptor.class );

    private StepMethodHandler scenarioMethodHandler;

    private StageTransitionHandler stageTransitionHandler;

    /**
     * Contains the stack of call receivers. This is used to update
     * the state of a parent stage after a call to a child stage has returned
     */
    protected final Stack<Object> stageStack = new Stack<Object>();

    private int maxStepDepth = ScenarioExecutor.INITIAL_MAX_STEP_DEPTH;

    /**
     * Whether methods should be intercepted or not
     */
    private boolean interceptingEnabled;

    /**
     * Whether step methods are actually executed or just skipped
     */
    private boolean methodExecutionEnabled = true;

    /**
     * abstraction to continue intercepted method
     */
    public interface Invoker {
        Object proceed() throws Throwable;
    };

    public StepMethodInterceptor( StepMethodHandler scenarioMethodHandler, StageTransitionHandler stageTransitionHandler ) {
        this.scenarioMethodHandler = scenarioMethodHandler;
        this.stageTransitionHandler = stageTransitionHandler;
    }

    public final Object doIntercept( final Object receiver, Method method, final Object[] parameters, Invoker invoker ) throws Throwable {
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

            return intercept( receiver, method, parameters, invoker, currentStackDepth );
        } finally {
            stageStack.pop();
            stageTransitionHandler.leaveStage( parentStage, receiver );
        }
    }

    private Object intercept( Object receiver, Method method, Object[] parameters, Invoker invoker, int currentStackDepth )
            throws Throwable {
        long started = System.nanoTime();

        InvocationMode mode = getInvocationMode( receiver, method );

        if( mode == DO_NOT_INTERCEPT ) {
            return invoker.proceed();
        }

        boolean hasNestedSteps = method.isAnnotationPresent( NestedSteps.class );

        boolean handleMethod = shouldHandleMethod( method );
        if( handleMethod ) {
            scenarioMethodHandler.handleMethod( receiver, method, parameters, mode, hasNestedSteps );
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
                scenarioMethodHandler.handleMethodFinished( System.nanoTime() - started, hasNestedSteps );
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
        if( !interceptingEnabled ) {
            return false;
        }

        if( method.getDeclaringClass().equals( Object.class ) ) {
            return false;
        }
        return true;
    }

    protected Object handleThrowable( Object receiver, Method method, Throwable t, long durationInNanos, boolean handleMethod )
            throws Throwable {
        if( handleMethod ) {
            scenarioMethodHandler.handleThrowable( t );
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
        if( method.getDeclaringClass() == Object.class || method.isAnnotationPresent( DoNotIntercept.class ) ) {
            return DO_NOT_INTERCEPT;
        }

        if( !methodExecutionEnabled ) {
            return SKIPPED;
        }

        if( method.isAnnotationPresent( NotImplementedYet.class )
                || receiver.getClass().isAnnotationPresent( NotImplementedYet.class )
                || method.isAnnotationPresent( Pending.class )
                || receiver.getClass().isAnnotationPresent( Pending.class ) ) {
            return PENDING;
        }

        return NORMAL;
    }

    public void enableMethodHandling( boolean b ) {
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

    public StepMethodHandler getScenarioMethodHandler() {
        return scenarioMethodHandler;
    }

    public void setScenarioMethodHandler( StepMethodHandler scenarioMethodHandler ) {
        this.scenarioMethodHandler = scenarioMethodHandler;
    }

    public void setStageTransitionHandler( StageTransitionHandler stageTransitionHandler ) {
        this.stageTransitionHandler = stageTransitionHandler;
    }
}
