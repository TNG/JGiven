package com.tngtech.jgiven.report.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.List;

import com.tngtech.jgiven.annotation.CaseAs;
import com.tngtech.jgiven.impl.params.DefaultCaseAsProvider;
import org.assertj.core.util.Lists;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;

@RunWith( DataProviderRunner.class )
public class CaseAsTest {

    @DataProvider
    public static Object[][] testData() {
        return new Object[][] {
                { "Empty value", "", Lists.<String>emptyList(), Lists.emptyList(), "" },
                { "No value", CaseAs.NO_VALUE, Arrays.asList( "a", "b" ), Arrays.asList( 1, 2 ), "a = 1, b = 2" },
                { "Placeholder with index", "$1", Arrays.asList( "a", "b" ), Arrays.asList( 1, 2 ), "1" },
                { "Placeholder without index", "$", Arrays.asList( "a", "b" ), Arrays.asList( 1, 2 ), "1" },
                { "Escaped placeholder", "$$", Arrays.asList( "a", "b" ), Arrays.asList( 1, 2 ), "$" },
                { "Multiple placeholders with switch order", "$2 + $1", Arrays.asList( "a", "b" ), Arrays.asList( 1, 2 ), "2 + 1" },
                { "Placeholders with additional text", "a = $1 and b = $2", Arrays.asList( "a", "b" ), Arrays.asList( 1, 2 ),
                        "a = 1 and b = 2" },
                { "Placeholders references by argument names in order", "int = $int and str = $str and bool = $bool",
                        Arrays.asList( "int", "str", "bool" ), Arrays.asList( 1, "some string", true ),
                        "int = 1 and str = some string and bool = true" },
                { "Placeholders references by argument names in mixed order", "str = $str and int = $int and bool = $bool",
                        Arrays.asList( "int", "str", "bool" ), Arrays.asList( 1, "some string", true ),
                        "str = some string and int = 1 and bool = true" },
                { "Placeholders references by argument names and enumeration", "str = $str and int = $1 and bool = $bool",
                        Arrays.asList( "int", "str", "bool" ), Arrays.asList( 1, "some string", true ),
                        "str = some string and int = 1 and bool = true" },
                { "Placeholders references by argument names and enumerations ", "bool = $3 and str = $2 and int = $int",
                        Arrays.asList( "int", "str", "bool" ), Arrays.asList( 1, "some string", true ),
                        "bool = true and str = some string and int = 1" },
                { "Placeholder without index mixed with names", "bool = $bool and int = $ and str = $",
                        Arrays.asList( "int", "str", "bool" ), Arrays.asList( 1, "some string", true ),
                        "bool = true and int = 1 and str = some string" },
                { "Placeholder without index mixed with names and index", "bool = $bool and str = $2 and int = $ and str = $ and bool = $3",
                        Arrays.asList( "int", "str", "bool" ), Arrays.asList( 1, "some string", true ),
                        "bool = true and str = some string and int = 1 and str = some string and bool = true" },
                { "Placeholder with unknown argument names get erased", "bool = $bool and not known = $unknown and unknown = $10",
                        Arrays.asList( "int", "str", "bool" ), Arrays.asList( 1, "some string", true ),
                        "bool = true and not known = 1 and unknown = some string" },
                { "Non-Java-Identifier char does trigger a space after a placeholder", "$]",
                        Arrays.asList( "int" ), Arrays.asList( 1 ), "1 ]" },
        };
    }

    @Test
    @UseDataProvider( "testData" )
    public void case_description_should_handle_everything_correctly( String description, String value, List<String> parameterNames,
            List<Object> parameterValues,
            String expectedValue ) {
        DefaultCaseAsProvider provider = new DefaultCaseAsProvider();
        assertThat( provider.as( value, parameterNames, parameterValues ) ).isEqualTo( expectedValue );

    }
}
