package com.tngtech.jgiven.examples.coffeemachine;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;
import com.tngtech.jgiven.examples.coffeemachine.steps.GivenSteps;
import com.tngtech.jgiven.examples.coffeemachine.steps.ThenSteps;
import com.tngtech.jgiven.examples.coffeemachine.steps.WhenSteps;
import com.tngtech.jgiven.junit.ScenarioTest;

/**
 * Feature: Serve coffee
 *    In order to earn money
 *    Customers should be able to 
 *    buy coffee at all times
 *  
 * Original example due to Cucumber Wiki
 */
@RunWith( DataProviderRunner.class )
public class ServeCoffeeFeature extends ScenarioTest<GivenSteps, WhenSteps, ThenSteps> {

    @DataProvider
    public static Object[][] buyCoffeeData() {
        return new Object[][] {
            { true, 1, 1, false },
            { true, 1, 2, true },
            { true, 0, 2, false },
            { false, 1, 2, false },
        };
    }

    @Test
    @UseDataProvider( "buyCoffeeData" )
    public void buy_a_coffee( boolean on, int coffees, int dollars, boolean coffeeServed ) {

        given().there_are_$_coffees_left_in_the_machine( coffees ).
            and().the_machine_is_$on_or_off$( on ).
            and().the_coffee_costs_$_dollar( 2 );

        when().I_deposit_$_dollar( dollars ).
            and().I_press_the_coffee_button();

        then().I_should_$or_should_not$_be_served_a_coffee( coffeeServed );
    }

}
