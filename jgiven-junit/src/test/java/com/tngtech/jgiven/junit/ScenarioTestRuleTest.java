package com.tngtech.jgiven.junit;

import static java.util.Arrays.asList;
import static java.util.Collections.EMPTY_LIST;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import com.tngtech.java.junit.dataprovider.DataProviderFrameworkMethod;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;
import org.junit.runners.model.FrameworkMethod;

@RunWith( DataProviderRunner.class )
public class ScenarioTestRuleTest {

    // TODO ASc
    @DataProvider
    public static Object[][] methodTestData() throws Exception {
        return new Object[][] {
            { new FrameworkMethod( anyMethod() ) /*, "someName", "someName"*/, EMPTY_LIST },

            { new DataProviderFrameworkMethod( anyMethod(), 1, new Object[] { "arg1", 2 } ), asList( "arg1", 2 ) }, // DataProviderRunner example
//            { "someName[1: arg1[1: test], arg2]", "someName", asList( "arg1[1: test]", "arg2" ) },
//            { "someName[1]", "someName", EMPTY_LIST }, // ParameterizedRunner example
//
//            { "[1] arg1, arg2", "", asList( "arg1", "arg2" ) }, // JUnitParams example
        };
    }

    @Test
    @UseDataProvider( "methodTestData" )
    public void testParseMethodName( FrameworkMethod testMethod, List<String> expectedArgs ) {
        List<Object> result = ScenarioExecutionRule.getMethodArguments( testMethod );
        assertThat( result ).isEqualTo( expectedArgs );
    }

    protected static Method anyMethod() throws Exception {
        Class<?> clazz = ScenarioTestRuleTest.class;
        String methodName = "anyMethod";
        if ( clazz == null ) {
            fail( String.format( "No method with name '%s' found.", methodName ) );
            return null;
        }
        return clazz.getDeclaredMethod( methodName );
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
