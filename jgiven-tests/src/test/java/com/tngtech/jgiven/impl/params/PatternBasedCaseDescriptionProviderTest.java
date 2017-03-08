package com.tngtech.jgiven.impl.params;

import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;
import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.annotation.As;
import com.tngtech.jgiven.annotation.OrdinalCaseDescription;
import com.tngtech.jgiven.junit.SimpleScenarioTest;
import com.tngtech.jgiven.tags.FeatureCaseDescriptions;
import org.assertj.core.util.Lists;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@FeatureCaseDescriptions
@RunWith( DataProviderRunner.class )
public class PatternBasedCaseDescriptionProviderTest extends SimpleScenarioTest<PatternBasedCaseDescriptionProviderTest.TestSteps> {

    @DataProvider
    public static Object[][] testData() {
        return new Object[][] {
                { "Empty value", "", Lists.<String>emptyList(), Lists.emptyList(), "" },
                { "No value", OrdinalCaseDescription.NO_VALUE, Arrays.asList( "a", "b" ), Arrays.asList( 1, 2 ), "a = 1, b = 2" },
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
                        "bool = true and int = 1 and str = some string"},
                { "Placeholder without index mixed with names and index", "bool = $bool and str = $2 and int = $ and str = $ and bool = $3",
                        Arrays.asList( "int", "str", "bool" ), Arrays.asList( 1, "some string", true ),
                        "bool = true and str = some string and int = 1 and str = some string and bool = true"},

        };
    }

    @UseDataProvider( "testData" )
    @Test
    @OrdinalCaseDescription( value = "$1" )
    public void the_description_pattern_is_evaluated_correctly( String description, String value, List<String> parameterNames,
            List<Object> parameterValues, String expectedValue ) {

        given().an_OrdinalCaseDescription_annotation_with_value( value )
                .and().the_parameter_names_are( parameterNames )
                .and().the_parameter_values_are( parameterValues );

        then().the_case_description_will_be( expectedValue );

    }

    public static class TestSteps extends Stage<TestSteps> {

        private String value;
        private List<Object> values;
        private List<String> parameterNames;

        @As( "An @OrdinalCaseDescription annotation with value" )
        public TestSteps an_OrdinalCaseDescription_annotation_with_value( String pattern ) {
            this.value = pattern;
            return self();
        }

        public void the_parameter_values_are( List<Object> values ) {
            this.values = values;
        }

        public TestSteps the_parameter_names_are( List<String> parameterNames ) {
            this.parameterNames = parameterNames;
            return self();
        }

        public void the_case_description_will_be( String expectedValue ) {
            DefaultOrdinalCaseDescriptionProvider provider = new DefaultOrdinalCaseDescriptionProvider();
            assertThat( provider.description( value, parameterNames, values ) ).isEqualTo( expectedValue );
        }

    }

}
