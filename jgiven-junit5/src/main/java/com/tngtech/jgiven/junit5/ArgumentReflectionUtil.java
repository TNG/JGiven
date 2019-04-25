package com.tngtech.jgiven.junit5;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tngtech.jgiven.impl.util.ParameterNameUtil;
import com.tngtech.jgiven.impl.util.ReflectionUtil;
import com.tngtech.jgiven.report.model.NamedArgument;

class ArgumentReflectionUtil {
    private static final Logger log = LoggerFactory.getLogger( ArgumentReflectionUtil.class );

    static final String METHOD_EXTENSION_CONTEXT = "org.junit.jupiter.engine.descriptor.MethodExtensionContext";
    static final String TEST_TEMPLATE_INVOCATION_TEST_DESCRIPTOR = "org.junit.jupiter.engine.descriptor.TestTemplateInvocationTestDescriptor";
    static final String PARAMETERIZED_TEST_INVOCATION_CONTEXT = "org.junit.jupiter.params.ParameterizedTestInvocationContext";

    static final String ERROR = "Not able to access field containing test method arguments. " +
            "Probably the internal representation has changed. Consider writing a bug report.";

    /**
     * This is a very ugly workaround to get the method arguments from the JUnit 5 context via reflection.
     */
    static List<NamedArgument> getNamedArgs( ExtensionContext context ) {
        List<NamedArgument> namedArgs = new ArrayList<>();

        if( context.getTestMethod().get().getParameterCount() > 0 ) {
            try {
                if( context.getClass().getCanonicalName().equals( METHOD_EXTENSION_CONTEXT ) ) {
                    Field field = context.getClass().getSuperclass().getDeclaredField( "testDescriptor" );
                    Object testDescriptor = ReflectionUtil.getFieldValueOrNull( field, context, ERROR );
                    if( testDescriptor != null
                            && testDescriptor.getClass().getCanonicalName().equals( TEST_TEMPLATE_INVOCATION_TEST_DESCRIPTOR ) ) {
                        Object invocationContext = ReflectionUtil.getFieldValueOrNull( "invocationContext", testDescriptor, ERROR );
                        if( invocationContext != null
                                && invocationContext.getClass().getCanonicalName().equals( PARAMETERIZED_TEST_INVOCATION_CONTEXT ) ) {
                            Object arguments = ReflectionUtil.getFieldValueOrNull( "arguments", invocationContext, ERROR );
                            List<Object> args = Arrays.asList( (Object[]) arguments );
                            namedArgs = ParameterNameUtil.mapArgumentsWithParameterNames( context.getTestMethod().get(), args );
                        }
                    }
                }
            } catch( Exception e ) {
                log.warn( ERROR, e );
            }
        }

        return namedArgs;
    }
}
