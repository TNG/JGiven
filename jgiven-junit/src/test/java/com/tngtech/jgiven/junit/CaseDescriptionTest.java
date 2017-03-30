package com.tngtech.jgiven.junit;

import com.tngtech.jgiven.annotation.CaseAsProvider;
import com.tngtech.jgiven.annotation.CaseAs;
import org.junit.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@CaseAs( provider = CaseDescriptionTest.TestCaseDescriptionProvider.class )
public class CaseDescriptionTest extends SimpleScenarioTest<StepsAreReportedTest.TestSteps> {

    @Test
    public void the_case_description_annotation_is_also_taken_from_the_class() {
        given().some_test_step();

        String description = getScenario().getScenarioCaseModel().getDescription();
        assertThat( description ).isEqualTo( "Case Description 0" );
    }

    public static class TestCaseDescriptionProvider implements CaseAsProvider {

        static int count = 0;

        @Override
        public String as( String value, List<String> parameterNames, List<?> parameterValues ) {
            return "Case Description " + ( count++ );
        }
    }
}
