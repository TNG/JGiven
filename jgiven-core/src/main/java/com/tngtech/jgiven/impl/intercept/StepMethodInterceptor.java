package com.tngtech.jgiven.impl.intercept;

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

    public StepMethodInterceptor( StepMethodHandler scenarioMethodHandler, AtomicInteger stackDepth ) {
        this.scenarioMethodHandler = scenarioMethodHandler;
        this.stackDepth = stackDepth;
    }

    @Override
    public Object intercept( Object receiver, Method method, Object[] parameters, MethodProxy methodProxy )
            throws Throwable {
        if( stackDepth.get() == 0 && !method.getDeclaringClass().equals( Object.class ) ) {
            scenarioMethodHandler.handleMethod( receiver, method, parameters );
        }

        if( method.isAnnotationPresent( NotImplementedYet.class )
                || receiver.getClass().isAnnotationPresent( NotImplementedYet.class ) ) {
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

        try {
            stackDepth.incrementAndGet();
            return methodProxy.invokeSuper( receiver, parameters );
        } catch( Throwable t ) {
            scenarioMethodHandler.handleThrowable( t );
            throw t;
        } finally {
            stackDepth.decrementAndGet();
        }
    }

}
