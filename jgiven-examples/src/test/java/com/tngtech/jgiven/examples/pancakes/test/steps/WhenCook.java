package com.tngtech.jgiven.examples.pancakes.test.steps;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;

import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.annotation.As;
import com.tngtech.jgiven.annotation.ExpectedScenarioState;
import com.tngtech.jgiven.annotation.ProvidedScenarioState;
import com.tngtech.jgiven.annotation.ScenarioState;
import com.tngtech.jgiven.examples.pancakes.app.Cook;
//tag::state[]
public class WhenCook extends Stage<WhenCook> {
    @Autowired
    @ScenarioState
    Cook cook;

    @ExpectedScenarioState
    List<String> ingredients;

    @ProvidedScenarioState
    Set<String> dough;

    @ProvidedScenarioState
    String meal;   
    public WhenCook the_cook_fries_the_dough_in_a_pan() {
        assertThat( cook ).isNotNull();
        assertThat( dough ).isNotNull();

        meal = cook.fryDoughInAPan( dough );
        return this;
    }
    // end::state[]
// tag::cookManglesDough[]
    public WhenCook the_cook_mangles_everthing_to_a_dough() {
        assertThat( cook ).isNotNull();
        assertThat( ingredients ).isNotNull();

        dough = cook.makeADough( ingredients );
        return this;
    }
// end::cookManglesDough[]

    // tag::cookDrinksMilk[]
    @As("the cook drinks 50% of the milk accidently")
    public WhenCook the_cook_drinks_50_percent_of_the_milk_accidently() {
        dough.remove("milk");
        dough.add("less milk");
        meal = cook.fryDoughInAPan( dough );
        return this;
    }
    // end::cookDrinksMilk[]

}
