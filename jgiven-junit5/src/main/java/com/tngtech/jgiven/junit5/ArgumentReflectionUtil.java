package com.tngtech.jgiven.junit5;

import com.tngtech.jgiven.impl.util.ParameterNameUtil;
import com.tngtech.jgiven.impl.util.ReflectionUtil;
import com.tngtech.jgiven.report.model.NamedArgument;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.support.ParameterInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

class ArgumentReflectionUtil {
    private static final Logger log = LoggerFactory.getLogger(ArgumentReflectionUtil.class);

    static final String METHOD_EXTENSION_CONTEXT = "org.junit.jupiter.engine.descriptor.MethodExtensionContext";
    static final String TEST_TEMPLATE_INVOCATION_TEST_DESCRIPTOR = "org.junit.jupiter.engine.descriptor.TestTemplateInvocationTestDescriptor";
    static final String PARAMETERIZED_TEST_INVOCATION_CONTEXT = "org.junit.jupiter.params.ParameterizedTestInvocationContext";

    static final String ERROR = "Not able to access field containing test method arguments. " +
            "Probably the internal representation has changed. Consider writing a bug report.";

    static final List<Function<ExtensionContext, List<NamedArgument>>> argumentRetrievers = Arrays.asList(
            ArgumentReflectionUtil::findByPublicApi, ArgumentReflectionUtil::findByReflectingJUnit512Context);

    static List<NamedArgument> getNamedArgs(ExtensionContext context) {
        List<NamedArgument> namedArgs = new ArrayList<>();
        var exceptionStorage = new RuntimeException("Failed to retrieve parameter arguments");

        if (context.getTestMethod().isPresent() && context.getTestMethod().get().getParameterCount() > 0) {
            for (var retriever : argumentRetrievers) {
                try {
                    namedArgs = retriever.apply(context);
                    if (!namedArgs.isEmpty()) {
                        break;
                    }
                } catch (Exception | NoClassDefFoundError e) {
                    exceptionStorage.addSuppressed(e);
                }
            }
        }
        if (exceptionStorage.getSuppressed().length > 0) {
            log.atWarn().setCause(exceptionStorage).log(ERROR);
        }
        return namedArgs;
    }

    //ExperimentalAPI that only exists since JUnit 5.13
    @SuppressWarnings("OptionalGetWithoutIsPresent") //Presence checked in calling method
    private static List<NamedArgument> findByPublicApi(ExtensionContext context) {
        var args = Arrays.asList(ParameterInfo.get(context).getArguments().toArray());
        return ParameterNameUtil.mapArgumentsWithParameterNames(context.getTestMethod().get(), args);
    }

    /**
     * This is a very ugly workaround to get the method arguments from the JUnit 5 context via reflection.
     */
    @SuppressWarnings("OptionalGetWithoutIsPresent") //Presence checked in calling method
    private static List<NamedArgument> findByReflectingJUnit512Context(ExtensionContext context) {
        List<NamedArgument> namedArgs = new ArrayList<>();
        try {
            if (context.getClass().getCanonicalName().equals(METHOD_EXTENSION_CONTEXT)) {
                Field field = context.getClass().getSuperclass().getDeclaredField("testDescriptor");
                Object testDescriptor = ReflectionUtil.getFieldValueOrNull(field, context, ERROR);
                if (testDescriptor != null
                        && testDescriptor.getClass().getCanonicalName().equals(TEST_TEMPLATE_INVOCATION_TEST_DESCRIPTOR)) {
                    Object invocationContext = ReflectionUtil.getFieldValueOrNull("invocationContext", testDescriptor, ERROR);
                    if (invocationContext != null
                            && invocationContext.getClass().getCanonicalName().equals(PARAMETERIZED_TEST_INVOCATION_CONTEXT)) {
                        Object arguments = ReflectionUtil.getFieldValueOrNull("arguments", invocationContext, ERROR);
                        if (arguments instanceof Arguments) {
                            List<Object> args = Arrays.asList(((Arguments) arguments).get());
                            namedArgs = ParameterNameUtil.mapArgumentsWithParameterNames(context.getTestMethod().get(), args);
                        } else {
                            log.warn(ERROR + " The type of arguments in the invocation context has changed. Please write a bug report.");
                        }
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return namedArgs;
    }
}
