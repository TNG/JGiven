package com.tngtech.jgiven.examples.coffeemachine.steps;

import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.annotation.ExtendedDescription;
import com.tngtech.jgiven.annotation.Format;
import com.tngtech.jgiven.annotation.ProvidedScenarioState;
import com.tngtech.jgiven.examples.coffeemachine.CoffeeMachine;
import com.tngtech.jgiven.format.BooleanFormatter;

public class GivenCoffee extends Stage<GivenCoffee> {

    @ProvidedScenarioState
    private CoffeeMachine coffeeMachine;

    @ProvidedScenarioState
    private int dollars;

    @ExtendedDescription( "An empty coffee machine that is already turned on.<br>"
            + "The coffee price is set to 2 EUR." )
    public GivenCoffee a_coffee_machine() {
        coffeeMachine = new CoffeeMachine();
        coffeeMachine.on = true;
        return this;
    }

    @ExtendedDescription( "The number of coffees in the machine is set to the given value." )
    public GivenCoffee there_are_$_coffees_left_in_the_machine( int coffees ) {
        coffeeMachine.coffees = coffees;
        return this;
    }

    public GivenCoffee the_coffee_costs_$_dollar( int price ) {
        coffeeMachine.price = price;
        return this;
    }

    public GivenCoffee the_machine_is_$onOrOff(@Format(value = BooleanFormatter.class, args = {"on", "off"}) boolean onOrOff) {
        coffeeMachine.on = onOrOff;
        return this;
    }

    public GivenCoffee there_are_no_more_coffees_left() {
        return there_are_$_coffees_left_in_the_machine( 0 );
    }

    public GivenCoffee the_machine_is_turned_off() {
        coffeeMachine.on = false;
        return this;
    }

    public GivenCoffee an_empty_coffee_machine() {
        return a_coffee_machine()
            .and().there_are_no_more_coffees_left();
    }

}
