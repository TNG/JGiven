package com.tngtech.jgiven.examples.coffeemachine;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.tngtech.jgiven.examples.coffeemachine.steps.GivenCoffee;
import com.tngtech.jgiven.examples.coffeemachine.steps.ThenCoffee;
import com.tngtech.jgiven.examples.coffeemachine.steps.WhenCoffee;
import com.tngtech.jgiven.junit.ScenarioTest;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

/**
 * Feature: Serve coffee
 *    In order to earn money
 *    Customers should be able to
 *    buy coffee at all times
 *
 * Original example due to Cucumber Wiki
 */
@RunWith( JUnitParamsRunner.class )
public class JUnitParamsServeCoffeeTest extends ScenarioTest<GivenCoffee, WhenCoffee, ThenCoffee> {

    @Test
    @Parameters( {
        "true, 1, 1, false",
        "true, 1, 2, true",
        "true, 0, 2, false",
        "false, 1, 2, false"
    } )
    public void buy_a_coffee( boolean onOrOff, int coffees, int dollars, boolean shouldOrShouldNot ) {

        given().a_coffee_machine().
            and().there_are_$_coffees_left_in_the_machine( coffees ).
            and().the_machine_is_$onOrOff(onOrOff).
            and().the_coffee_costs_$_dollar( 2 );

        when().I_insert_$_one_euro_coins( dollars ).
            and().I_press_the_coffee_button();

        then().I_$shouldOrShouldNot_be_served_a_coffee( shouldOrShouldNot );
    }

}
