package com.tngtech.jgiven.examples.coffeemachine.steps;

import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.annotation.Format;
import com.tngtech.jgiven.annotation.ProvidedScenarioState;
import com.tngtech.jgiven.examples.coffeemachine.CoffeeMachine;
import com.tngtech.jgiven.format.BooleanFormatter;

public class GivenSteps extends Stage<GivenSteps> {

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
