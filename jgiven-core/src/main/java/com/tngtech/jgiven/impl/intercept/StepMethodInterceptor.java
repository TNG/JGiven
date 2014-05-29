package com.tngtech.jgiven.impl.intercept;

import static com.tngtech.jgiven.impl.intercept.InvocationMode.NORMAL;
import static com.tngtech.jgiven.impl.intercept.InvocationMode.NOT_IMPLEMENTED_YET;
import static com.tngtech.jgiven.impl.intercept.InvocationMode.SKIPPED;

import java.lang.reflect.Method;
import java.util.concurrent.atomic.AtomicInteger;

import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tngtech.jgiven.annotation.NotImplementedYet;

public class StepMethodInterceptor implements MethodInterceptor {
    private static final Logger log = LoggerFactory.getLogger( StepMethodInterceptor.class );

    private final StepMethodHandler scenarioMethodHandler;
    private final AtomicInteger stackDepth;
    private boolean methodHandlingEnabled;
    private boolean methodExecutionEnabled = true;

    public StepMethodInterceptor( StepMethodHandler scenarioMethodHandler, AtomicInteger stackDepth ) {
        this.scenarioMethodHandler = scenarioMethodHandler;
        this.stackDepth = stackDepth;
    }

    @Override
    public Object intercept( Object receiver, Method method, Object[] parameters, MethodProxy methodProxy )
            throws Throwable {

        InvocationMode mode = getInvocationMode( receiver, method );

        if( methodHandlingEnabled && stackDepth.get() == 0 && !method.getDeclaringClass().equals( Object.class ) ) {
            scenarioMethodHandler.handleMethod( receiver, method, parameters, mode );
        }

        if( mode == SKIPPED || mode == NOT_IMPLEMENTED_YET ) {
            return returnReceiverOrNull( receiver, method );
        }

        try {
            stackDepth.incrementAndGet();
            return methodProxy.invokeSuper( receiver, parameters );
        } catch( Exception t ) {
            return handleThrowable( receiver, method, t );
        } catch( AssertionError e ) {
            return handleThrowable( receiver, method, e );
        } finally {
            stackDepth.decrementAndGet();
        }
    }

    private Object handleThrowable( Object receiver, Method method, Throwable t ) throws Throwable, Exception {
        if( methodHandlingEnabled ) {
            scenarioMethodHandler.handleThrowable( t );
            return returnReceiverOrNull( receiver, method );
        }
        throw t;
    }

    private Object returnReceiverOrNull( Object receiver, Method method ) {
        // we assume here that the implementation follows the fluent interface
        // convention and returns the receiver object. If not, we fall back to null
        // and hope for the best.
        if( !method.getReturnType().isAssignableFrom( receiver.getClass() ) ) {
            log.warn( "The step method " + method.getName()
                    + " of class " + method.getDeclaringClass().getSimpleName()
                    + " does not follow the fluent interface convention of returning "
                    + "the receiver object. Please change the return type to the SELF type parameter." );
            return null;
        }

        return receiver;
    }

    private InvocationMode getInvocationMode( Object receiver, Method method ) {
        if( !methodExecutionEnabled ) {
            return SKIPPED;
        } else if( method.isAnnotationPresent( NotImplementedYet.class )
                || receiver.getClass().isAnnotationPresent( NotImplementedYet.class ) ) {
            return NOT_IMPLEMENTED_YET;
        }
        return NORMAL;
    }

    public void enableMethodHandling( boolean b ) {
        this.methodHandlingEnabled = b;
    }

    public void disableMethodExecution() {
        this.methodExecutionEnabled = false;
    }

}
