package com.tngtech.jgiven.examples.pancakes.test.fixtures;

import com.tngtech.jgiven.annotation.ExpectedScenarioState;
import com.tngtech.jgiven.annotation.ProvidedScenarioState;
import com.tngtech.jgiven.examples.pancakes.test.stages.GivenTheIngredients;
import com.tngtech.jgiven.examples.pancakes.test.stages.MyNavigableStage;
import com.tngtech.jgiven.examples.pancakes.test.stages.WhenTheCook;
import com.tngtech.jgiven.impl.NavigableStageCreator;
import com.tngtech.jgiven.integration.spring.JGivenStage;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class PanCakeScenarioFixture {

    @JGivenStage
    public static class Given extends MyNavigableStage.Top<Given> {

        @ExpectedScenarioState
        NavigableStageCreator navigableStageCreator;

        @ProvidedScenarioState
        private List<String> ingredients = new ArrayList<>();

        public GivenTheIngredients<Given, Given> ingredients() {
            return navigableStageCreator.nestedStage( new GivenTheIngredients<>(), this, this)
            .withSubject(ingredients);
        }

    }

    @JGivenStage
    public static class When extends MyNavigableStage.Top<When> {

        @ExpectedScenarioState
        NavigableStageCreator navigableStageCreator;

        public WhenTheCook<When, When> cook() {
            return navigableStageCreator.nestedStage(new WhenTheCook<>(), this, this);
        }

    }

    @JGivenStage
    public static class Then extends MyNavigableStage.Top<Then> {

        @ExpectedScenarioState
        private String meal;

        public Then resulting_meal_is_a_pan_cake() {
            assertThat( meal ).isEqualTo( "pancake" );
            return self();
        }

    }

}
