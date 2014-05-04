package com.tngtech.jgiven.junit;

import static java.util.Arrays.asList;
import static java.util.Collections.EMPTY_LIST;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;
import com.tngtech.jgiven.junit.ScenarioExecutionRule.Case;

@RunWith( DataProviderRunner.class )
public class ScenarioTestRuleTest {

    @DataProvider
    public static Object[][] methodTestData() {
        return new Object[][] {
            { "someName", "someName", EMPTY_LIST },
            { "someName[1: arg1, arg2]", "someName", asList( "arg1", "arg2" ) }, // DataProviderRunner example
            { "someName[1: arg1[1: test], arg2]", "someName", asList( "arg1[1: test]", "arg2" ) },
            { "someName[1]", "someName", EMPTY_LIST }, // ParameterizedRunner example
            { "[1] arg1, arg2", "", asList( "arg1", "arg2" ) }, // JUnitParams example
        };
    }

    @Test
    @UseDataProvider( "methodTestData" )
    public void testParseMethodName( String inputName, String expectedName, List<String> expectedArgs ) {
        Case parsedCase = ScenarioExecutionRule.parseMethodName( inputName );
        assertThat( parsedCase.methodName ).isEqualTo( expectedName );
        assertThat( parsedCase.arguments ).isEqualTo( expectedArgs );
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
