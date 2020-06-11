package com.tngtech.jgiven.examples.pancakes.test.stages;

import com.tngtech.jgiven.annotation.ExpectedScenarioState;
import com.tngtech.jgiven.annotation.ProvidedScenarioState;
import com.tngtech.jgiven.examples.pancakes.app.Cook;
import com.tngtech.jgiven.impl.NavigableStage;
import com.tngtech.jgiven.integration.spring.JGivenStage;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@JGivenStage
public class WhenTheCook<ROOT, BACK> extends NavigableStage<ROOT, BACK, WhenTheCook<ROOT, BACK>> {

    @Autowired
    private Cook cook;

    @ExpectedScenarioState
    private List<String> ingredients;

    @ProvidedScenarioState
    Set<String> dough;

    @ProvidedScenarioState
    String meal;

    public WhenTheCook<ROOT, BACK> mangles_everything_to_a_dough() {
        assertThat( cook ).isNotNull();
        assertThat( ingredients ).isNotNull();

        dough = cook.makeADough( ingredients );

        return self();
    }

    public WhenTheCook<ROOT, BACK> fries_the_dough_in_a_pan() {
        assertThat( cook ).isNotNull();
        assertThat( dough ).isNotNull();

        meal = cook.fryDoughInAPan( dough );

        return self();
    }

}
