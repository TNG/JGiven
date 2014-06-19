package com.tngtech.jgiven.examples.coffeemachine;

import com.tngtech.jgiven.examples.coffeemachine.steps.GivenSteps;
import com.tngtech.jgiven.examples.coffeemachine.steps.ThenSteps;
import com.tngtech.jgiven.examples.coffeemachine.steps.WhenSteps;
import com.tngtech.jgiven.junit.ScenarioTest;
import com.tngtech.jgiven.report.text.PlainTextReporter;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Feature: Serve coffee
 *    In order to earn money
 *    Customers should be able to 
 *    buy coffee at all times
 *  
 * Original example due to Cucumber Wiki
 */
@RunWith( JUnitParamsRunner.class )
public class JUnitParamsServeCoffeeFeature extends ScenarioTest<GivenSteps, WhenSteps, ThenSteps> {
    @After
    public void printScenario() {
        PlainTextReporter textWriter = new PlainTextReporter();
        getScenario().getModel().accept( textWriter );
    }

    @Test
    @Parameters( {
        "true, 1, 1, false",
        "true, 1, 2, true",
        "true, 0, 2, false",
        "false, 1, 2, false"
    } )
    public void buy_a_coffee( boolean on, int coffees, int dollars, boolean coffeeServed ) {

        given().there_are_$_coffees_left_in_the_machine( coffees ).
            and().the_machine_is_$on_or_off$( on ).
            and().the_coffee_costs_$_dollar( 2 );

        when().I_deposit_$_dollar( dollars ).
            and().I_press_the_coffee_button();

        then().I_should_$or_should_not$_be_served_a_coffee( coffeeServed );
    }

}
