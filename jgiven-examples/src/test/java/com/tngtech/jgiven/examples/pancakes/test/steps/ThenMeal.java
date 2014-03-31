package com.tngtech.jgiven.examples.pancakes.test.steps;

import static org.assertj.core.api.Assertions.assertThat;

import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.annotation.ExpectedScenarioState;

public class ThenMeal extends Stage<ThenMeal> {
    @ExpectedScenarioState
    String meal;

    public void the_resulting_meal_is_a_pan_cake() {
        the_resulting_meal_is_a( "pancake" );
    }

    public void the_resulting_meal_is_a( String expectedMeal ) {
        assertThat( meal ).isEqualTo( expectedMeal );
    }
}
