package com.tngtech.jgiven.examples.coffeemachine.steps;

import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.annotation.ExpectedScenarioState;
import com.tngtech.jgiven.annotation.ProvidedScenarioState;
import com.tngtech.jgiven.examples.coffeemachine.CoffeeMachine;

public class WhenSteps extends Stage<WhenSteps> {
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
