package com.tngtech.jgiven.junit;

import static java.util.Arrays.asList;
import static java.util.Collections.EMPTY_LIST;
import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import junitparams.internal.InvokeParameterisedMethod;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;

import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderFrameworkMethod;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;

@RunWith( DataProviderRunner.class )
public class ScenarioTestRuleTest {

    @DataProvider
    public static Object[][] methodTestData() throws Exception {
        return new Object[][] {
            { emptyStatement(), new FrameworkMethod( twoParamsMethod() ), EMPTY_LIST },
            { emptyStatement(), dataProviderFrameworkMethod( twoParamsMethod(), "arg1", 2 ), asList( "arg1", 2 ) },
            { junitParamsStatement( twoParamsMethod(), "arg1, 2" ), new FrameworkMethod( twoParamsMethod() ), asList( "arg1", 2 ) },
        };
    }

    private static Object dataProviderFrameworkMethod( Method method, Object... args ) throws Exception {
        return new DataProviderFrameworkMethod( method, 1, args );
    }

    private static Object junitParamsStatement( Method method, String args ) throws Exception {
        return new InvokeParameterisedMethod( new FrameworkMethod( method ), ScenarioTestRuleTest.class, args, 1 );
    }

    private static Statement emptyStatement() {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {}
        };
    }

    @Test
    @UseDataProvider( "methodTestData" )
    public void testParseMethodName( Statement statement, FrameworkMethod testMethod, List<String> expectedArgs ) {
        List<Object> result = ScenarioExecutionRule.getMethodArguments( statement, testMethod );
        assertThat( result ).isEqualTo( expectedArgs );
    }

    public void testMethodWithTwoParams( String arg1, int arg2 ) {}

    protected static Method twoParamsMethod() throws Exception {
        return anyMethod( "testMethodWithTwoParams", String.class, int.class );
    }

    protected static Method anyMethod( String methodName, Class<?>... types ) throws Exception {
        return ScenarioTestRuleTest.class.getDeclaredMethod( methodName, types );
    }

    @DataProvider
    public static Object[][] argumentTestData() {
        return new Object[][] {
            { "foo", Arrays.asList( "foo" ) },
            { "foo, bar", Arrays.asList( "foo", "bar" ) },
            { "foo, [1, 2, 3]", Arrays.asList( "foo", "1, 2, 3" ) },
            { "[1, 2], foo", Arrays.asList( "1, 2", "foo" ) },
            { "foo, [1, 2], bar", Arrays.asList( "foo", "1, 2", "bar" ) },
            { "[1, [1, 2], 2]", Arrays.asList( "1, [1, 2], 2" ) },
            { "[foo", Arrays.asList( "[foo" ) },
        };
    }

    @Test
    @UseDataProvider( "argumentTestData" )
    public void testArgumentParsing( String argumentString, List<String> expectedResult ) {
        List<String> parseArguments = ScenarioExecutionRule.parseArguments( argumentString );
        assertThat( parseArguments ).isEqualTo( expectedResult );
    }

}
