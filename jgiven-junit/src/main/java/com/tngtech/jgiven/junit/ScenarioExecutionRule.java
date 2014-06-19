package com.tngtech.jgiven.junit;

import static com.tngtech.jgiven.report.model.ExecutionStatus.FAILED;
import static com.tngtech.jgiven.report.model.ExecutionStatus.SUCCESS;
import static java.lang.String.format;
import static org.junit.Assume.assumeTrue;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;

import org.junit.internal.AssumptionViolatedException;
import org.junit.rules.MethodRule;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.tngtech.jgiven.impl.ScenarioBase;
import com.tngtech.jgiven.report.model.ReportModel;
import com.tngtech.jgiven.report.model.ReportModelBuilder;

public class ScenarioExecutionRule implements MethodRule {
    private static final String JUNITPARAMS_STATEMENT = "junitparams.internal.InvokeParameterisedMethod";
    private static final String DATAPROVIDER_FRAMEWORK_METHOD = "com.tngtech.java.junit.dataprovider.DataProviderFrameworkMethod";

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
    public Statement apply( final Statement base, final FrameworkMethod method,
            final Object target ) {
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

        scenario.getExecutor().startScenario( testMethod.getMethod(), getMethodArguments( base, testMethod ) );

        // inject state from the test itself
        scenario.getExecutor().readScenarioState( testInstance );
    }

    static List<Object> getMethodArguments( Statement base, FrameworkMethod method ) {
        if( DATAPROVIDER_FRAMEWORK_METHOD.equals( method.getClass().getCanonicalName() ) ) {
            return getParametersFromObject( method, "parameters" );
        }

        if( JUNITPARAMS_STATEMENT.equals( base.getClass().getCanonicalName() ) ) {
            return getParametersFromObject( base, "params" );
        }

        return Collections.emptyList();
    }

    private static List<Object> getParametersFromObject( Object object, String fieldName ) {
        Class<?> methodClass = object.getClass();
        try {
            Field parametersField = methodClass.getDeclaredField( fieldName );
            parametersField.setAccessible( true );
            Object[] parameters = (Object[]) parametersField.get( object );
            return Arrays.asList( parameters );
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

    /**
     * Simple splitting by ',' is not possible, because of arguments
     * that contain ',' like arrays for example.
     * Handles arrays and lists that are contained in [ ]. 
     * Can also deal with nested arrays.
     * <p>
     * Removes the surrounding [ ] from lists in the result
     */
    public static List<String> parseArguments( String arguments ) {
        List<String> result = Lists.newArrayList();
        StringBuilder currentArg = new StringBuilder();
        int listDepth = 0;
        for( int i = 0; i < arguments.length(); i++ ) {
            char c = arguments.charAt( i );
            if( c == '[' ) {
                if( listDepth > 0 || currentArg.toString().trim().length() == 0 ) {
                    listDepth++;
                    if( listDepth == 1 ) {
                        continue;
                    }
                }
            } else if( listDepth > 0 ) {
                if( c == ']' ) {
                    listDepth--;
                    if( listDepth == 0 ) {
                        continue;
                    }
                }
            } else {
                if( c == ',' ) {
                    result.add( currentArg.toString().trim() );
                    currentArg = new StringBuilder();
                    continue;
                }
            }
            currentArg.append( c );
        }
        // fix string if list was not ended correctly
        if( listDepth > 0 ) {
            currentArg.insert( 0, '[' );
        }
        if( currentArg.length() > 0 ) {
            result.add( currentArg.toString().trim() );
        }
        return result;
    }
}
