package com.tngtech.jgiven.examples.coffeemachine;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.jgiven.examples.coffeemachine.steps.GivenCoffee;
import com.tngtech.jgiven.examples.coffeemachine.steps.ThenCoffee;
import com.tngtech.jgiven.examples.coffeemachine.steps.WhenCoffee;
import com.tngtech.jgiven.junit.ScenarioTest;

/**
 * Feature: Serve coffee
 *    In order to refresh myself
 *    as a customer 
 *    I want to coffee to be served
 *  
 * Original example due to Cucumber Wiki
 */
@RunWith( DataProviderRunner.class )
public class ServeCoffeeFeature extends ScenarioTest<GivenCoffee, WhenCoffee, ThenCoffee> {

    @Test
    public void an_empty_coffee_machine_cannot_serve_any_coffee() throws Exception {

        given().an_empty_coffee_machine();

        when().I_insert_$_one_euro_coins( 5 )
            .and().I_press_the_coffee_button();

        then().an_error_should_be_shown()
            .and().no_coffee_should_be_served();
    }

    @Test
    public void no_coffee_left_error_is_shown_when_there_is_no_coffee_left() {
        given().an_empty_coffee_machine();
        when().I_insert_$_one_euro_coins( 5 )
            .and().I_press_the_coffee_button();
        then().the_message_$_is_shown( "Error: No coffee left" );
    }

    @Test
    public void not_enough_money_message_is_shown_when_insufficient_money_was_given() throws Exception {

        given().a_coffee_machine();
        when().I_insert_$_one_euro_coins( 1 )
            .and().I_press_the_coffee_button();
        then().the_message_$_is_shown( "Error: Insufficient money" );
    }

    @Test
    @DataProvider( {
        "0, 0, Error: No coffees left",
        "0, 1, Error: No coffees left",
        "1, 0, Error: Insufficient money",
        "0, 5, Error: No coffees left",
        "1, 5, Enjoy your coffee!",
    } )
    public void correct_messages_are_shown( int coffeesLeft, int numberOfCoins, String message ) throws Exception {
        given().a_coffee_machine()
            .and().there_are_$_coffees_left_in_the_machine( coffeesLeft );
        when().I_insert_$_one_euro_coins( numberOfCoins )
            .and().I_press_the_coffee_button();
        then().the_message_$_is_shown( message );
    }

    @Test
    public void a_turned_off_coffee_machine_cannot_serve_coffee() throws Exception {

        given().a_coffee_machine()
            .and().the_machine_is_turned_off();

        when().I_press_the_coffee_button();

        then().no_coffee_should_be_served();

    }

    @Test
    @DataProvider( {
        "true, 1, 1, false",
        "true, 1, 2, true",
        "true, 0, 2, false",
        "false, 1, 2, false",
    } )
    public void buy_a_coffee( boolean on, int coffees, int dollars, boolean coffeeServed ) {

        given().there_are_$_coffees_left_in_the_machine( coffees ).
            and().the_machine_is_$on_or_off$( on ).
            and().the_coffee_costs_$_dollar( 2 );

        when().I_insert_$_one_euro_coins( dollars ).
            and().I_press_the_coffee_button();

        then().I_should_$or_should_not$_be_served_a_coffee( coffeeServed );
    }

}
