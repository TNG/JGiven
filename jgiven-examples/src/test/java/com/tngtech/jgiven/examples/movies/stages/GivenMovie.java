package com.tngtech.jgiven.examples.movies.stages;

import com.tngtech.jgiven.annotation.ProvidedScenarioState;
import com.tngtech.jgiven.annotation.Quoted;
import com.tngtech.jgiven.examples.movies.model.Movie;
import com.tngtech.jgiven.impl.NavigableStageCreator;

public class GivenMovie<ROOT, BACK>
extends MyNavigableStage<ROOT, BACK, GivenMovie<ROOT, BACK>> {

    @ProvidedScenarioState
    private NavigableStageCreator navigableStageCreator;

    private Movie subject;

    public GivenMovie<ROOT, BACK> withSubject( Movie subject) {
        this.subject = subject;
        return self();
    }

    public GivenMovie<ROOT, BACK> title( @Quoted String title ) {
        subject.title( title );
        return self();
    }

    public GivenMovie<ROOT, BACK> rating( @Quoted String rating ) {
        subject.rating( rating );
        return self();
    }

    public GivenActor<ROOT, GivenMovie<ROOT, BACK>> actor() {
        return navigableStageCreator.nestedStage( new GivenActor<>(), root(), this );
    }

}
