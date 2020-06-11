package com.tngtech.jgiven.examples.pancakes.test.stages;

import com.tngtech.jgiven.annotation.SyntacticSugar;
import com.tngtech.jgiven.integration.spring.JGivenStage;

import java.util.List;

@JGivenStage
public class GivenTheIngredients<ROOT, BACK> extends MyNavigableStage<ROOT, BACK, GivenTheIngredients<ROOT, BACK>> {

    private List<String> subject;

    public GivenTheIngredients<ROOT, BACK> withSubject(List<String> subject ) {
        this.subject = subject;
        return self();
    }

    public GivenTheIngredients<ROOT, BACK> egg() {
        return ingredient( "egg" );
    }

    public GivenTheIngredients<ROOT, BACK> milk() {
        return ingredient( "milk" );
    }

    public GivenTheIngredients<ROOT, BACK> ingredient(String ingredient) {
        subject.add( ingredient );
        return self();
    }

    @SyntacticSugar
    public GivenTheIngredients<ROOT, BACK> consisting() {
        return self();
    }

    @SyntacticSugar
    public GivenTheIngredients<ROOT, BACK> some() {
        return self();
    }

}
