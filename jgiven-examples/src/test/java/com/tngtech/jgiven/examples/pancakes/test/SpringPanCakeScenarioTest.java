package com.tngtech.jgiven.examples.pancakes.test;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.tngtech.jgiven.examples.pancakes.app.SpringConfig;
import com.tngtech.jgiven.examples.pancakes.test.steps.GivenIngredients;
import com.tngtech.jgiven.examples.pancakes.test.steps.ThenMeal;
import com.tngtech.jgiven.examples.pancakes.test.steps.WhenCook;
import com.tngtech.jgiven.integration.spring.SpringCanWire;
import com.tngtech.jgiven.junit.ScenarioTest;

@RunWith( SpringJUnit4ClassRunner.class )
@ContextConfiguration( classes = SpringConfig.class )
public class SpringPanCakeScenarioTest extends ScenarioTest<GivenIngredients, WhenCook, ThenMeal> {
    @Autowired
    private AutowireCapableBeanFactory beanFactory;

    @Before
    public void setupSpring() {
        wireSteps( new SpringCanWire( beanFactory ) );
    }

    @Test
    public void a_pancake_can_be_fried_out_of_an_egg_milk_and_flour() {
        given().an_egg().
            and().some_milk().
            and().the_ingredient( "flour" );

        when().the_cook_mangles_everthing_to_a_dough().
            and().the_cook_fries_the_dough_in_a_pan();

        then().the_resulting_meal_is_a_pan_cake();
    }

}
