package com.tngtech.jgiven.examples.coffeemachine.steps;

import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.annotation.ExpectedScenarioState;
import com.tngtech.jgiven.annotation.ProvidedScenarioState;
import com.tngtech.jgiven.examples.coffeemachine.CoffeeMachine;

public class WhenCoffee extends Stage<WhenCoffee> {
    @ExpectedScenarioState
    private CoffeeMachine coffeeMachine;

    @ProvidedScenarioState
    private boolean coffeeServed;

    public WhenCoffee I_insert_$_one_euro_coins(int euros) {
        coffeeMachine.insertOneEuroCoin(euros);
        return self();
    }

    public WhenCoffee I_press_the_coffee_button() {
        coffeeServed = coffeeMachine.pressButton();
        return self();
    }

}
