package com.tngtech.jgiven.junit;

import static org.junit.Assume.assumeTrue;

import java.lang.reflect.Method;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.tngtech.jgiven.impl.ScenarioBase;
import com.tngtech.jgiven.impl.util.ReflectionUtil;
import com.tngtech.jgiven.report.model.ImplementationStatus;
import com.tngtech.jgiven.report.model.ReportModel;
import com.tngtech.jgiven.report.model.ReportModelBuilder;

public class ScenarioExecutionRule extends TestWatcher {
    private static final Logger log = LoggerFactory.getLogger( ScenarioExecutionRule.class );

    private final Object testInstance;
    private final ScenarioBase scenario;

    public ScenarioExecutionRule( ReportModel model, Object testInstance, ScenarioBase scenario ) {
        this.testInstance = testInstance;
        this.scenario = scenario;
        this.scenario.setModel( model );
        scenario.getExecutor().injectSteps( testInstance );
    }

    @Override
    protected void succeeded( Description description ) {
        scenario.getExecutor().succeeded();
        scenario.finished();

        // ignore test when scenario is not implemented
        assumeTrue( scenario.getModel().getLastScenarioModel().getImplementationStatus() != ImplementationStatus.NONE );
    }

    @Override
    protected void failed( Throwable e, Description description ) {
        scenario.getExecutor().failed( e );
        scenario.finished();
    }

    @Override
    protected void starting( Description description ) {
        Class<?> testClass = description.getTestClass();
        ReportModelBuilder modelBuilder = scenario.getModelBuilder();
        modelBuilder.setClassName( testClass.getName() );

        Case singleCase = parseMethodName( description.getMethodName() );

        Method method = ReflectionUtil.findMethod( testClass, singleCase.methodName );

        scenario.getExecutor().startScenario( method, singleCase.arguments );

        // inject state from the test itself
        scenario.getExecutor().readScenarioState( testInstance );
    }

    private static final Pattern DATA_PROVIDER_RUNNER_PATTERN = Pattern.compile( "\\[(\\d+):(.*)\\]" );
    private static final Pattern PARAMETER_RUNNER_PATTERN = Pattern.compile( "\\[(\\d+)\\]" );
    private static final Pattern JUNIT_PARAMS_RUNNER_PATTERN = Pattern.compile( "\\[(\\d+)\\] (.*)" );

    static Case parseMethodName( String name ) {
        Case singleCase = new Case();

        // test for parameters
        int endOfMethod = name.indexOf( '[' );
        if( endOfMethod != -1 ) {
            String methodName = name.substring( 0, endOfMethod );
            singleCase.methodName = methodName;

            String data = name.substring( endOfMethod, name.length() );

            Matcher matcher = DATA_PROVIDER_RUNNER_PATTERN.matcher( data );
            if( !matcher.matches() ) {
                matcher = JUNIT_PARAMS_RUNNER_PATTERN.matcher( data );
                if( !matcher.matches() ) {
                    matcher = PARAMETER_RUNNER_PATTERN.matcher( data );
                    if( !matcher.matches() ) {
                        log.warn(
                            "Arguments '{}' did not match any known JUnit parameter runner. Consider writing a feature request or bug report.",
                            data );
                        return singleCase;
                    }
                }
            }

            if( matcher.groupCount() > 1 ) {
                String arguments = matcher.group( 2 );
                singleCase.arguments = parseArguments( arguments );
            }
        } else {
            singleCase.methodName = name;
        }
        return singleCase;
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

    static class Case {
        String methodName;
        List<String> arguments = Lists.newArrayList();
    }

}
