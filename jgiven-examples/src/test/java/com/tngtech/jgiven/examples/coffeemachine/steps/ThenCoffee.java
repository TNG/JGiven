package com.tngtech.jgiven.examples.coffeemachine.steps;

import static org.assertj.core.api.Assertions.assertThat;

import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.annotation.ExpectedScenarioState;
import com.tngtech.jgiven.annotation.Format;
import com.tngtech.jgiven.examples.coffeemachine.CoffeeMachine;
import com.tngtech.jgiven.format.NotFormatter;

public class ThenCoffee extends Stage<ThenCoffee> {

    @ExpectedScenarioState
    private boolean coffeeServed;

    @ExpectedScenarioState
    private CoffeeMachine coffeeMachine;

    public void I_should_$or_should_not$_be_served_a_coffee( @Format( NotFormatter.class ) boolean coffeeServed ) {
        I_should_be_served_a_coffee( coffeeServed );
    }

    public void I_should_not_be_served_a_coffee() {
        I_should_be_served_a_coffee( false );
    }

    private ThenCoffee I_should_be_served_a_coffee( boolean b ) {
        assertThat( coffeeServed ).isEqualTo( b );
        return self();
    }

    public ThenCoffee a_coffee_should_be_served() {
        return I_should_be_served_a_coffee( true );
    }

    public ThenCoffee no_coffee_should_be_served() {
        return self();
    }

    public ThenCoffee an_error_should_be_shown() {
        assertThat( coffeeMachine.message ).startsWith( "Error" );
        return self();
    }

    public ThenCoffee the_message_$_is_shown( String message ) {
        assertThat( coffeeMachine.message ).isEqualTo( message );
        return self();
    }

    public ThenCoffee there_are_$_coffees_left_in_the_machine( int coffeesLeft ) {
        assertThat( coffeeMachine.coffees ).isEqualTo( coffeesLeft );
        return self();
    }
}
