package com.tngtech.jgiven.junit;

import static com.tngtech.jgiven.report.model.ExecutionStatus.FAILED;
import static com.tngtech.jgiven.report.model.ExecutionStatus.SUCCESS;
import static org.junit.Assume.assumeTrue;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.EnumSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.internal.AssumptionViolatedException;
import org.junit.rules.MethodRule;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.tngtech.jgiven.impl.ScenarioBase;
import com.tngtech.jgiven.impl.util.ReflectionUtil;
import com.tngtech.jgiven.report.model.ReportModel;
import com.tngtech.jgiven.report.model.ReportModelBuilder;

// TODO MethodRule has been deprecated in 4.10 but no longer in 4.11
public class ScenarioExecutionRule implements MethodRule {
    private static final Logger log = LoggerFactory.getLogger( ScenarioExecutionRule.class );

    private final Object testInstance;
    private final ScenarioBase scenario;
    private final ReportModel reportModel;

    public ScenarioExecutionRule( ReportModel model, Object testInstance, ScenarioBase scenario ) {
        this.testInstance = testInstance;
        this.scenario = scenario;
        this.reportModel = model;
    }


    public Statement apply(final Statement base, final FrameworkMethod method,
                           final Object target) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                starting(method, target);
                try {
                    base.evaluate();
                    succeeded(method);
                } catch (AssumptionViolatedException e) {
                    throw e;
                } catch (Throwable t) {
                    failed(t, method);
                    throw t;
                } finally {
                    finished(method);
                }
            }
        };
    }

    protected void succeeded( FrameworkMethod testMethod ) {
        scenario.finished();

        // ignore test when scenario is not implemented
        assumeTrue( EnumSet.of( SUCCESS, FAILED ).contains( scenario.getModel().getLastScenarioModel().getExecutionStatus() ) );
    }

    protected void failed( Throwable e, FrameworkMethod testMethod ) {
        scenario.getExecutor().failed( e );
        scenario.finished();
    }

    protected void starting( FrameworkMethod testMethod, Object target ) {
        scenario.setModel( reportModel );
        scenario.getExecutor().injectSteps( testInstance );

        Class<?> testClass = target.getClass();
        ReportModelBuilder modelBuilder = scenario.getModelBuilder();
        modelBuilder.setClassName( testClass.getName() );

        scenario.getExecutor().startScenario( testMethod.getMethod(), getMethodArguments(testMethod) );

        // inject state from the test itself
        scenario.getExecutor().readScenarioState( testInstance );
    }

    protected void finished( FrameworkMethod testMethod ) {
        // not used currently
    }

    private static final Pattern PARAMETER_RUNNER_PATTERN = Pattern.compile( "\\[(\\d+)\\]" );
    private static final Pattern JUNIT_PARAMS_RUNNER_PATTERN = Pattern.compile( "\\[(\\d+)\\] (.*)" );

    static List<Object> getMethodArguments( FrameworkMethod method ) {

        Class<? extends FrameworkMethod> methodClass = method.getClass();
        if ( "com.tngtech.java.junit.dataprovider.DataProviderFrameworkMethod".equals( methodClass.getCanonicalName() ) ) {
            try {
                Field parametersField = methodClass.getDeclaredField( "parameters" );
                parametersField.setAccessible( true );
                Object[] parameters = (Object[]) parametersField.get( method );
                return Arrays.asList( parameters );

            } catch ( NoSuchFieldException e ) {
                log.warn( String.format(
                        "Could not find field containing test method arguments in '%s'. Probably the internal representation has changed. Consider writing bug report.",
                        methodClass.getSimpleName() ), e );
            } catch ( IllegalAccessException e ) {
                log.warn( String.format(
                        "Not able to access field containing test method arguments in '%s'. Probably the internal representation has changed. Consider writing bug report.",
                        methodClass.getSimpleName() ), e );
            }
            return Collections.emptyList();
        }

        String name = method.getName(); // TODO this is not same as with Description?

        // test for parameters
        int endOfMethod = name.indexOf( '[' );
        if( endOfMethod != -1 ) {
            String data = name.substring( endOfMethod, name.length() );

            Matcher matcher = JUNIT_PARAMS_RUNNER_PATTERN.matcher( data );
            if( !matcher.matches() ) {
                matcher = PARAMETER_RUNNER_PATTERN.matcher( data );
                if( !matcher.matches() ) {
                    log.warn(
                        "Arguments '{}' did not match any known JUnit parameter runner. Consider writing a feature request or bug report.",
                        data );
                    return Collections.emptyList();
                }
            }

            if( matcher.groupCount() > 1 ) {
                String arguments = matcher.group( 2 );
                return new ArrayList<Object>( parseArguments( arguments ) );
            }
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
