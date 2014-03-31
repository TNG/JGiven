package com.tngtech.jgiven.examples.coffeemachine;

import static org.assertj.core.api.Assertions.assertThat;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.annotation.ExpectedScenarioState;
import com.tngtech.jgiven.annotation.Format;
import com.tngtech.jgiven.annotation.ProvidedScenarioState;
import com.tngtech.jgiven.examples.coffeemachine.ServeCoffeeFeature.GivenSteps;
import com.tngtech.jgiven.examples.coffeemachine.ServeCoffeeFeature.ThenSteps;
import com.tngtech.jgiven.examples.coffeemachine.ServeCoffeeFeature.WhenSteps;
import com.tngtech.jgiven.format.BooleanFormatter;
import com.tngtech.jgiven.format.NotFormatter;
import com.tngtech.jgiven.junit.ScenarioTest;
import com.tngtech.jgiven.report.text.PlainTextReporter;

/**
 * Feature: Serve coffee
 *    In order to earn money
 *    Customers should be able to 
 *    buy coffee at all times
 *  
 * Original example due to Cucumber Wiki
 */
@RunWith( JUnitParamsRunner.class )
public class ServeCoffeeFeature extends ScenarioTest<GivenSteps, WhenSteps, ThenSteps> {
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

    public static class GivenSteps extends Stage<GivenSteps> {

        @ProvidedScenarioState
        private final CoffeeMachine coffeeMachine = new CoffeeMachine();

        @ProvidedScenarioState
        private int dollars;

        public GivenSteps there_are_$_coffees_left_in_the_machine( int coffees ) {
            coffeeMachine.coffees = coffees;
            return this;
        }

        public GivenSteps the_coffee_costs_$_dollar( int price ) {
            coffeeMachine.price = price;
            return this;
        }

        public GivenSteps the_machine_is_$on_or_off$( @Format( value = BooleanFormatter.class, args = { "on", "off" } ) boolean on ) {
            coffeeMachine.on = on;
            return this;
        }

    }

    public static class ThenSteps extends Stage<ThenSteps> {

        @ExpectedScenarioState
        private boolean coffeeServed;

        public void I_should_$or_should_not$_be_served_a_coffee( @Format( NotFormatter.class ) boolean coffeeServed ) {
            I_should_be_served_a_coffee( coffeeServed );
        }

        public void I_should_not_be_served_a_coffee() {
            I_should_be_served_a_coffee( false );
        }

        private void I_should_be_served_a_coffee( boolean b ) {
            assertThat( coffeeServed ).isEqualTo( b );
        }

        public void I_should_be_served_a_coffee() {
            I_should_be_served_a_coffee( true );
        }

    }

    public static class WhenSteps extends Stage<WhenSteps> {
        @ExpectedScenarioState
        private CoffeeMachine coffeeMachine;

        @ProvidedScenarioState
        private boolean coffeeServed;

        public WhenSteps I_deposit_$_dollar( int dollars ) {
            coffeeMachine.insertMoney( dollars );
            return self();
        }

        public WhenSteps I_press_the_coffee_button() {
            coffeeServed = coffeeMachine.pressButton();
            return self();
        }

    }

    static class CoffeeMachine {

        static final int DEFAULT_COFFEE_PRICE = 2;

        public int price = DEFAULT_COFFEE_PRICE;
        public boolean on;
        public int dollars;
        public int coffees;

        boolean pressButton() {
            if( on && coffees > 0 && dollars >= price ) {
                coffees--;
                dollars = 0;
                return true;
            } else
                return false;
        }

        public void insertMoney( int dollars ) {
            this.dollars += dollars;
        }
    }
}
