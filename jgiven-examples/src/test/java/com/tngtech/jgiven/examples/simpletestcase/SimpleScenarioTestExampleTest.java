package com.tngtech.jgiven.examples.simpletestcase;

import org.junit.Assert;
import org.junit.Test;

import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.examples.simpletestcase.SimpleScenarioTestExampleTest.TestSteps;
import com.tngtech.jgiven.junit.SimpleScenarioTest;

public class SimpleScenarioTestExampleTest extends SimpleScenarioTest<TestSteps> {

    public static class CoffeeMachine {

        private static final int PRICE = 2;
        private boolean servedCoffee;
        private int coffees;
        private int money;

        public void pressCoffeeButton() {
            if( coffees > 0 && money >= PRICE ) {
                servedCoffee = true;
            }
        }

        public void depositMoney( int money ) {
            this.money += money;
        }

    }

    @Test
    public void coffee_should_be_served() throws Exception {
        given().a_coffee_machine_with_$n_coffees( 100 );

        when().enough_money_is_deposited()
            .and().the_coffee_button_is_pressed();

        then().a_cup_of_coffee_is_served();
    }

    @Test
    public void coffee_should_not_be_served_if_there_are_no_coffees_left() throws Exception {
        given().a_coffee_machine_with_$n_coffees( 0 );

        when().enough_money_is_deposited()
            .and().the_coffee_button_is_pressed();

        then().no_cup_of_coffee_is_served();
    }

    @Test
    public void coffee_should_not_be_served_if_not_enough_money_is_deposited() throws Exception {
        given().a_coffee_machine();

        when().$_euros_are_deposited( 1 )
            .and().the_coffee_button_is_pressed();

        then().no_cup_of_coffee_is_served();
    }

    public static class TestSteps extends Stage<TestSteps> {

        private CoffeeMachine coffeeMachine;

        public void a_coffee_machine_with_$n_coffees( int ncoffees ) {
            coffeeMachine = new CoffeeMachine();
            coffeeMachine.coffees = ncoffees;
        }

        public TestSteps $_euros_are_deposited( int euros ) {
            coffeeMachine.money = euros;
            return this;
        }

        public void a_coffee_machine() {
            a_coffee_machine_with_$n_coffees( 100 );
        }

        public void the_coffee_button_is_pressed() {
            coffeeMachine.pressCoffeeButton();
        }

        public TestSteps enough_money_is_deposited() {
            coffeeMachine.depositMoney( 2 );
            return this;
        }

        public void a_cup_of_coffee_is_served() {
            Assert.assertTrue( "no coffee was served", coffeeMachine.servedCoffee );
        }

        public void no_cup_of_coffee_is_served() {
            Assert.assertFalse( "coffee was served", coffeeMachine.servedCoffee );
        }

    }

}
