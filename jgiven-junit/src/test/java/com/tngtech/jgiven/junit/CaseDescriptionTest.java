package com.tngtech.jgiven.junit;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.Test;

import com.tngtech.jgiven.annotation.CaseDescription;
import com.tngtech.jgiven.annotation.CaseDescriptionProvider;

@CaseDescription( provider = CaseDescriptionTest.TestCaseDescriptionProvider.class )
public class CaseDescriptionTest extends SimpleScenarioTest<StepsAreReportedTest.TestSteps> {

    @Test
    public void the_case_description_annotation_is_also_taken_from_the_class() {
        given().some_test_step();

        String description = getScenario().getScenarioCaseModel().getDescription();
        assertThat( description ).isEqualTo( "Case Description 0" );
    }

    public static class TestCaseDescriptionProvider implements CaseDescriptionProvider {

        static int count = 0;

        @Override
        public String description( String value, List<String> parameterNames, List<?> parameterValues ) {
            return "Case Description " + ( count++ );
        }
    }
}
