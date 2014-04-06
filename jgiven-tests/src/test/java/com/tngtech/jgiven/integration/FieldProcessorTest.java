package com.tngtech.jgiven.integration;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Field;

import org.junit.Test;

import com.tngtech.jgiven.GivenScenarioStages;
import com.tngtech.jgiven.ThenScenario;
import com.tngtech.jgiven.WhenScenario;
import com.tngtech.jgiven.annotation.FieldProcessors;
import com.tngtech.jgiven.integration.FieldProcessorTest.GivenFieldProcessorTest;
import com.tngtech.jgiven.integration.FieldProcessorTest.ThenFieldProcessorTest;
import com.tngtech.jgiven.junit.ScenarioTest;

public class FieldProcessorTest extends ScenarioTest<GivenFieldProcessorTest, WhenScenario, ThenFieldProcessorTest> {

    @Test
    public void field_annotation_processors_can_process_annotated_fields() {
        given().a_stage_with_a_FieldProcessor_annotation();
        when().the_scenario_is_created();
        then().the_processor_has_processed_the_field();
    }

    @Retention( RetentionPolicy.RUNTIME )
    public @interface TestAnnotation {}

    public static class TestFieldProcessor implements StageFieldProcessor {
        @Override
        public void process( Object stage, Field field ) throws Exception {
            if( field.isAnnotationPresent( TestAnnotation.class ) ) {
                field.setAccessible( true );
                field.set( stage, "FooBar" );
            }
        }
    }

    @FieldProcessors( TestFieldProcessor.class )
    public static class AnnotatedStage {
        @TestAnnotation
        private String field;
    }

    public static class GivenFieldProcessorTest extends GivenScenarioStages<GivenFieldProcessorTest> {
        public GivenFieldProcessorTest a_stage_with_a_FieldProcessor_annotation() {
            a_given_stage_of_type( AnnotatedStage.class );
            return self();
        }
    }

    public static class ThenFieldProcessorTest extends ThenScenario<ThenFieldProcessorTest> {
        public void the_processor_has_processed_the_field() {
            AnnotatedStage givenStage = (AnnotatedStage) scenario.getGivenStage();
            assertThat( givenStage.field ).isEqualTo( "FooBar" );
        }
    }
}
