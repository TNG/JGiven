package com.tngtech.jgiven.impl.params;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.List;

import com.tngtech.jgiven.tags.FeatureCaseDescriptions;
import org.assertj.core.util.Lists;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;
import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.annotation.As;
import com.tngtech.jgiven.annotation.CaseDescription;
import com.tngtech.jgiven.junit.SimpleScenarioTest;

@FeatureCaseDescriptions
@RunWith( DataProviderRunner.class )
public class PatternBasedCaseDescriptionProviderTest extends SimpleScenarioTest<PatternBasedCaseDescriptionProviderTest.TestSteps> {

    @DataProvider
    public static Object[][] testData() {
        return new Object[][] {
            { "Empty value", "", Lists.<String>emptyList(), Lists.emptyList(), "" },
            { "No value", CaseDescription.NO_VALUE, Arrays.asList( "a", "b" ), Arrays.asList( 1, 2 ), "a = 1, b = 2" },
            { "Placeholder with index", "$0", Arrays.asList( "a", "b" ), Arrays.asList( 1, 2 ), "1" },
            { "Placeholder without index", "$", Arrays.asList( "a", "b" ), Arrays.asList( 1, 2 ), "1" },
            { "Escaped placeholder", "$$", Arrays.asList( "a", "b" ), Arrays.asList( 1, 2 ), "$" },
            { "Multiple placeholders with switch order", "$1 + $0", Arrays.asList( "a", "b" ), Arrays.asList( 1, 2 ), "2 + 1" },
            { "Placeholders with additional text", "a = $0 and b = $1", Arrays.asList( "a", "b" ), Arrays.asList( 1, 2 ), "a = 1 and b = 2" },
        };
    }

    @UseDataProvider( "testData" )
    @Test
    @CaseDescription( value = "$0" )
    public void the_description_pattern_is_evaluated_correctly( String description, String value, List<String> parameterNames,
            List<Object> parameterValues, String expectedValue ) {

        given().a_CaseDescription_annotation_with_value( value )
            .and().the_parameter_names_are( parameterNames )
            .and().the_parameter_values_are( parameterValues );

        then().the_case_description_will_be( expectedValue );

    }

    public static class TestSteps extends Stage<TestSteps> {

        private String value;
        private List<Object> values;
        private List<String> parameterNames;

        @As( "A @CaseDescription annotation with value" )
        public TestSteps a_CaseDescription_annotation_with_value( String pattern ) {
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
            DefaultCaseDescriptionProvider provider = new DefaultCaseDescriptionProvider();
            assertThat( provider.description( value, parameterNames, values ) ).isEqualTo( expectedValue );
        }

    }

}
