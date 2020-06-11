package com.tngtech.jgiven.examples.movies.stages;

import com.tngtech.jgiven.annotation.AfterStage;
import com.tngtech.jgiven.annotation.ExpectedScenarioState;
import com.tngtech.jgiven.annotation.Quoted;
import com.tngtech.jgiven.examples.movies.model.Actor;
import com.tngtech.jgiven.impl.NavigableStage;

import java.util.List;

public class GivenActor<ROOT, BACK>
extends MyNavigableStage<ROOT, BACK, GivenActor<ROOT, BACK>> {

    @ExpectedScenarioState
    private List<Actor> actors;

    private Actor subject = new Actor();

    @AfterStage
    private void afterStage() {
        actors.add(subject);
    }

    public GivenActor<ROOT, BACK> name( @Quoted String name ) {
        subject.name( name );
        return self();
    }

}
