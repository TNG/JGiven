package com.tngtech.jgiven.impl.intercept;

import static com.tngtech.jgiven.report.model.InvocationMode.*;

import java.lang.reflect.Method;
import java.util.Stack;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tngtech.jgiven.annotation.DoNotIntercept;
import com.tngtech.jgiven.annotation.NestedSteps;
import com.tngtech.jgiven.annotation.NotImplementedYet;
import com.tngtech.jgiven.annotation.Pending;
import com.tngtech.jgiven.report.model.InvocationMode;

public class StepMethodInterceptor {
    private static final Logger log = LoggerFactory.getLogger( StepMethodInterceptor.class );

    private StepMethodHandler scenarioMethodHandler;

    private AtomicInteger stackDepth;

    private int maxStepDepth = 1;

    /**
     * stack that represent method call context. @see NestedSteps.java
     */
    private final Stack<Method> methodCallStack = new Stack<Method>();

    /**
     * Whether the method handler is called when a step method is invoked
     */
    private boolean methodHandlingEnabled;

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

    public StepMethodInterceptor( StepMethodHandler scenarioMethodHandler, AtomicInteger stackDepth ) {
        this.scenarioMethodHandler = scenarioMethodHandler;
        this.stackDepth = stackDepth;
    }

    public final Object doIntercept( final Object receiver, Method method,
            final Object[] parameters, Invoker invoker ) throws Throwable {
        int currentStackDepth = stackDepth.incrementAndGet();
        try {
            if( !shouldInterceptMethod( method ) ) {
                return invoker.proceed();
            }
            return intercept( receiver, method, parameters, invoker, currentStackDepth );
        } finally {
            stackDepth.decrementAndGet();
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

        if( methodHandlingEnabled ) {
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
            return handleThrowable( receiver, method, e, System.nanoTime() - started );
        } catch( AssertionError e ) {
            return handleThrowable( receiver, method, e, System.nanoTime() - started );
        } finally {
            if( hasNestedSteps ) {
                maxStepDepth--;
            }
            if( methodHandlingEnabled ) {
                scenarioMethodHandler.handleMethodFinished( System.nanoTime() - started, hasNestedSteps );
            }
        }
    }

    private boolean shouldInterceptMethod( Method method ) {
        if( method.getDeclaringClass().equals( Object.class ) ) {
            return false;
        }
        return stackDepth.get() <= maxStepDepth;
    }

    protected Object handleThrowable( Object receiver, Method method, Throwable t, long durationInNanos ) throws Throwable {
        if( methodHandlingEnabled ) {
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
        this.methodHandlingEnabled = b;
    }

    public void disableMethodExecution() {
        this.methodExecutionEnabled = false;
    }

    public boolean enableMethodExecution( boolean b ) {
        boolean previousMethodExecution = methodExecutionEnabled;
        this.methodExecutionEnabled = b;
        return previousMethodExecution;
    }

    public StepMethodHandler getScenarioMethodHandler() {
        return scenarioMethodHandler;
    }

    public AtomicInteger getStackDepth() {
        return stackDepth;
    }

    public void setScenarioMethodHandler( StepMethodHandler scenarioMethodHandler ) {
        this.scenarioMethodHandler = scenarioMethodHandler;
    }

    public void setStackDepth( AtomicInteger stackDepth ) {
        this.stackDepth = stackDepth;
    }

}
