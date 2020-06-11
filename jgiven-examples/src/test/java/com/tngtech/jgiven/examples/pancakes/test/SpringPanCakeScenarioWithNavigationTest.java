package com.tngtech.jgiven.examples.pancakes.test;

import com.tngtech.jgiven.examples.pancakes.test.fixtures.PanCakeScenarioFixture.Given;
import com.tngtech.jgiven.examples.pancakes.test.fixtures.PanCakeScenarioFixture.Then;
import com.tngtech.jgiven.examples.pancakes.test.fixtures.PanCakeScenarioFixture.When;
import com.tngtech.jgiven.integration.spring.SpringScenarioTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith( SpringJUnit4ClassRunner.class )
@ContextConfiguration( classes = TestSpringConfig.class )
public class SpringPanCakeScenarioWithNavigationTest extends SpringScenarioTest<Given, When, Then> {

    @Test
    public void a_pancake_can_be_fried_out_of_an_egg_milk_and_flour() {

        given() .ingredients()
            .consisting() .of() .an() .egg()
            .some() .milk()
            .and() .the() .ingredient( "flour" );

        when() .the() .cook()
            .mangles_everything_to_a_dough()
            .and() .then() .fries_the_dough_in_a_pan();

        then() .the() .resulting_meal_is_a_pan_cake();
    }

}
