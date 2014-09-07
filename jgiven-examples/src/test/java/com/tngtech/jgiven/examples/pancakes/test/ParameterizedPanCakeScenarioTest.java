package com.tngtech.jgiven.examples.pancakes.test;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.tngtech.jgiven.annotation.ProvidedScenarioState;
import com.tngtech.jgiven.examples.pancakes.app.Cook;
import com.tngtech.jgiven.examples.pancakes.app.PanCakeCook;
import com.tngtech.jgiven.examples.pancakes.test.steps.GivenIngredients;
import com.tngtech.jgiven.examples.pancakes.test.steps.ThenMeal;
import com.tngtech.jgiven.examples.pancakes.test.steps.WhenCook;
import com.tngtech.jgiven.junit.ScenarioTest;

@RunWith( Parameterized.class )
public class ParameterizedPanCakeScenarioTest extends ScenarioTest<GivenIngredients, WhenCook, ThenMeal> {
    @ProvidedScenarioState
    private final Cook cook = new PanCakeCook();

    @Parameters
    public static List<Object[]> data() {
        return Arrays.asList( new Object[][] {
            { "flour", "pancake" },
            { "sugar", "mishmash" } } );
    }

    String ingredient;
    String expectedMeal;

    public ParameterizedPanCakeScenarioTest( String ingredient, String expectedMeal ) {
        this.ingredient = ingredient;
        this.expectedMeal = expectedMeal;
    }

    @Test
    public void a_meal_can_be_fried_out_of_an_egg_milk_and_some_ingredient() {
        given().an_egg().
            and().some_milk().
            and().the_ingredient( ingredient );

        when().the_cook_mangles_everthing_to_a_dough().
            and().the_cook_fries_the_dough_in_a_pan();

        then().the_resulting_meal_is_a( expectedMeal );
    }

}
