package com.tngtech.jgiven.examples.tags;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import org.junit.Test;

import com.tngtech.jgiven.CurrentScenario;
import com.tngtech.jgiven.annotation.IsTag;
import com.tngtech.jgiven.annotation.ScenarioState;
import com.tngtech.jgiven.junit.SimpleScenarioTest;

/**
 * This example shows how tags can be added dynamically at runtime.
 */
public class DynamicTags extends SimpleScenarioTest<DynamicTags.Steps> {

    @Test
    public void tags_can_be_added_dynamically() {
        given().an_order_for_a_$_car( CarType.BMW );
        when().the_order_is_processed();
        then().the_car_is_sent_to_manufacturing();
    }

    public static class Steps {
        @ScenarioState
        CurrentScenario currentScenario;

        Steps an_order_for_a_$_car( CarType type ) {
            /*
             * Dynamically add a tag with a value depending on the passed
             * argument.
             */
            currentScenario.addTag( CarOrder.class, type.name );
            return this;
        }

        Steps the_order_is_processed() {
            return this;
        }

        Steps the_car_is_sent_to_manufacturing() {
            return this;
        }
    }

    @IsTag
    @Retention( RetentionPolicy.RUNTIME )
    @interface CarOrder {}

    private static enum CarType {
        AUDI( "Audi" ),
        BMW( "BMW" );

        final String name;

        private CarType( String name ) {
            this.name = name;
        }
    }

}
