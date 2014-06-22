package com.tngtech.jgiven.junit;

import static com.tngtech.jgiven.report.model.ExecutionStatus.FAILED;
import static com.tngtech.jgiven.report.model.ExecutionStatus.SUCCESS;
import static java.lang.String.format;
import static org.junit.Assume.assumeTrue;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;

import org.junit.internal.AssumptionViolatedException;
import org.junit.rules.MethodRule;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.primitives.Primitives;
import com.tngtech.jgiven.impl.NamedArgument;
import com.tngtech.jgiven.impl.ScenarioBase;
import com.tngtech.jgiven.impl.util.ScenarioUtil;
import com.tngtech.jgiven.report.model.ReportModel;
import com.tngtech.jgiven.report.model.ReportModelBuilder;

public class ScenarioExecutionRule implements MethodRule {
    private static final String DATAPROVIDER_FRAMEWORK_METHOD = "com.tngtech.java.junit.dataprovider.DataProviderFrameworkMethod";
    private static final String JUNITPARAMS_STATEMENT = "junitparams.internal.InvokeParameterisedMethod";

    private static final Logger log = LoggerFactory.getLogger( ScenarioExecutionRule.class );

    private final Object testInstance;
    private final ScenarioBase scenario;
    private final ReportModel reportModel;

    public ScenarioExecutionRule( ReportModel model, Object testInstance, ScenarioBase scenario ) {
        this.testInstance = testInstance;
        this.scenario = scenario;
        this.reportModel = model;
    }

    @Override
    public Statement apply( final Statement base, final FrameworkMethod method, final Object target ) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                starting( base, method, target );
                try {
                    base.evaluate();
                    succeeded();
                } catch( AssumptionViolatedException e ) {
                    throw e;
                } catch( Throwable t ) {
                    failed( t );
                }
            }
        };
    }

    protected void succeeded() {
        scenario.finished();

        // ignore test when scenario is not implemented
        assumeTrue( EnumSet.of( SUCCESS, FAILED ).contains( scenario.getModel().getLastScenarioModel().getExecutionStatus() ) );
    }

    protected void failed( Throwable e ) {
        scenario.getExecutor().failed( e );
        scenario.finished();
    }

    protected void starting( Statement base, FrameworkMethod testMethod, Object target ) {
        scenario.setModel( reportModel );
        scenario.getExecutor().injectSteps( testInstance );

        Class<?> testClass = target.getClass();
        ReportModelBuilder modelBuilder = scenario.getModelBuilder();
        modelBuilder.setClassName( testClass.getName() );

        scenario.getExecutor().startScenario( testMethod.getMethod(), getNamedArguments( base, testMethod, target ) );

        // inject state from the test itself
        scenario.getExecutor().readScenarioState( testInstance );
    }

    @VisibleForTesting
    static List<NamedArgument> getNamedArguments( Statement base, FrameworkMethod method, Object target ) {
        AccessibleObject constructorOrMethod = method.getMethod();

        List<Object> arguments = Collections.emptyList();

        if( DATAPROVIDER_FRAMEWORK_METHOD.equals( method.getClass().getCanonicalName() ) ) {
            arguments = getArgumentsFrom( method, "parameters" );
        }

        if( JUNITPARAMS_STATEMENT.equals( base.getClass().getCanonicalName() ) ) {
            arguments = getArgumentsFrom( base, "params" );
        }

        if( isParameterizedTest( target ) ) {
            Constructor<?> constructor = getOnlyConstructor( target.getClass() );
            constructorOrMethod = constructor;
            arguments = getArgumentsFrom( constructor, target );
        }

        return ScenarioUtil.mapArgumentsWithParameterNamesOf( constructorOrMethod, arguments );
    }

    private static List<Object> getArgumentsFrom( Object object, String fieldName ) {
        Class<?> methodClass = object.getClass();
        try {
            Field field = methodClass.getDeclaredField( fieldName );
            field.setAccessible( true );
            return Arrays.asList( (Object[]) field.get( object ) );

        } catch( NoSuchFieldException e ) {
            log.warn( format( "Could not find field containing test method arguments in '%s'. "
                + "Probably the internal representation has changed. Consider writing a bug report.",
                methodClass.getSimpleName() ), e );
        } catch( IllegalAccessException e ) {
            log.warn( format( "Not able to access field containing test method arguments in '%s'. "
                + "Probably the internal representation has changed. Consider writing a bug report.",
                methodClass.getSimpleName() ), e );
        }
        return Collections.emptyList();
    }

    private static boolean isParameterizedTest( Object target ) {
        RunWith runWith = target.getClass().getAnnotation( RunWith.class );
        return runWith != null && Parameterized.class.equals( runWith.value() );
    }

    private static Constructor<?> getOnlyConstructor( Class<?> testClass ) {
        Constructor<?>[] constructors = testClass.getConstructors();
        if( constructors.length != 1 ) {
            log.warn( "Test class can only have one public constructor, see org.junit.runners.Parameterized.TestClassRunnerForParameters.validateConstructor(List<Throwable>)" );
        }
        return constructors[0];
    }

    /**
     * Searches for all arguments of the given {@link Parameterized} test class by retrieving the values of all
     * non-static instance fields and comparing their types with the constructor arguments. The order of resulting
     * parameters corresponds to the order of the constructor argument types (which is equal to order of the provided
     * data of the method annotated with {@link Parameters}). If the constructor contains multiple arguments of the same
     * type, the order of the {@link Class#getDeclaredFields()} is used.
     *
     * @param constructor {@link Constructor} from which argument types should be retrieved
     * @param target {@link Parameterized} test instance from which arguments tried to be retrieved
     * @return the determined arguments, never {@code null}
     */
    private static List<Object> getArgumentsFrom( Constructor<?> constructor, Object target ) {
        Class<?> testClass = target.getClass();

        Class<?>[] constructorParamClasses = constructor.getParameterTypes();
        List<Object> fieldValues = getAllNonStaticFieldValuesFrom( testClass, target );

        return getTypeMatchingValuesInOrderOf( constructorParamClasses, fieldValues );
    }

    private static List<Object> getAllNonStaticFieldValuesFrom( Class<?> testClass, Object target ) {
        List<Object> fieldValues = new ArrayList<Object>();
        for( Field field : testClass.getDeclaredFields() ) {
            if( !Modifier.isStatic( field.getModifiers() ) ) {
                field.setAccessible( true );
                try {
                    fieldValues.add( field.get( target ) );

                } catch( IllegalAccessException e ) {
                    log.warn( format( "Not able to access field '%s' containing parameterized test argument in '%s'. "
                        + "Consider writing a bug report.", field.getName(), testClass.getSimpleName() ), e );
                }
            }
        }
        return fieldValues;
    }

    private static List<Object> getTypeMatchingValuesInOrderOf( Class<?>[] expectedClasses, List<Object> values ) {
        List<Object> result = new ArrayList<Object>();
        int idx = 0;
        int jdx = 0;
        while( idx < expectedClasses.length && jdx < values.size() ) {
            Class<?> argumentClass = expectedClasses[idx];
            Object value = values.get( jdx );
            if( Primitives.wrap( argumentClass ).equals( value.getClass() ) ) {
                // or: if ( Primitives.wrap( paramClass ).isInstance( value ) ) {
                result.add( value );
                idx++;
                jdx++;
            } else {
                jdx++;
            }
        }
        if( idx < expectedClasses.length ) {
            log.warn( format( "Couldn't find matching values in '%s' for expected classes '%s',", values,
                Arrays.toString( expectedClasses ) ) );
        }
        return result;
    }

}
