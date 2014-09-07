package com.tngtech.jgiven.examples.pancakes.test.steps;

import java.util.ArrayList;
import java.util.List;

import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.annotation.ProvidedScenarioState;

public class GivenIngredients extends Stage<GivenIngredients> {

    @ProvidedScenarioState
    List<String> ingredients = new ArrayList<String>();

    public GivenIngredients an_egg() {
        return the_ingredient( "egg" );
    }

    public GivenIngredients the_ingredient( String ingredient ) {
        ingredients.add( ingredient );
        return this;
    }

    public GivenIngredients some_milk() {
        return the_ingredient( "milk" );
    }

}
