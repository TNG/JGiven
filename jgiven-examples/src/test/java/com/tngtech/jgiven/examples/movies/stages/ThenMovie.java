package com.tngtech.jgiven.examples.movies.stages;

import com.tngtech.jgiven.annotation.Quoted;
import com.tngtech.jgiven.examples.movies.model.Movie;

import static org.assertj.core.api.Assertions.assertThat;

public class ThenMovie<ROOT, BACK>
extends MyNavigableStage<ROOT, BACK, ThenMovie<ROOT, BACK>> {

    private Movie subject;

    public ThenMovie<ROOT, BACK> withSubject( Movie subject ) {
        this.subject = subject;
        return self();
    }

    public ThenMovie<ROOT, BACK> title( @Quoted String title ) {
        assertThat( subject.title() ).isEqualTo( title );
        return self();
    }

}
