package com.tngtech.jgiven.examples.movies;

import com.tngtech.jgiven.annotation.ExpectedScenarioState;
import com.tngtech.jgiven.annotation.ProvidedScenarioState;
import com.tngtech.jgiven.annotation.SyntacticSugar;
import com.tngtech.jgiven.examples.movies.model.Actor;
import com.tngtech.jgiven.examples.movies.model.Movie;
import com.tngtech.jgiven.examples.movies.stages.GivenMovie;
import com.tngtech.jgiven.examples.movies.stages.MyNavigableStage;
import com.tngtech.jgiven.examples.movies.stages.ThenMovie;
import com.tngtech.jgiven.impl.NavigableStageCreator;

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;
import static java.util.stream.Collectors.toSet;
import static org.assertj.core.api.Assertions.assertThat;

public class MovieFixture {

    public static class Given
    extends MyNavigableStage.Top<Given> {

        @ProvidedScenarioState
        private NavigableStageCreator navigableStageCreator;

        @ProvidedScenarioState
        private List<Movie> movies = new ArrayList<>();

        @ProvidedScenarioState
        private List<Actor> actors = new ArrayList<>();

        public GivenMovie<Given, Given> movie() {
            Movie movie = new Movie();
            movies.add(movie);

            return navigableStageCreator.nestedStage( new GivenMovie<>(), this, this )
            .withSubject(movie);
        }

        public Given $_movies( int count ) {
            for ( int i = 0; i < count; i++ ) {
                movie()
                .title( "Movie: " + i );
            }

            return self();
        }
    }

    public static class When
    extends MyNavigableStage.Top<When> {

    }

    public static class Then
    extends MyNavigableStage.Top<Then> {

        @ExpectedScenarioState
        private NavigableStageCreator navigableStageCreator;

        @ExpectedScenarioState
        private List<Movie> movies;

        @ExpectedScenarioState
        private List<Actor> actors = new ArrayList<>();

        public Then $_movies( int count ) {
            assertThat( movies ).hasSize( count );
            return this;
        }

        @SyntacticSugar
        public ThenMovie<Then, Then> first_movie() {
            return navigableStageCreator.nestedStage( new ThenMovie<>(), this, this )
            .withSubject( movies.get(0) );
        }

        @SyntacticSugar
        public ThenMovie<Then, Then> second_movie() {
            return navigableStageCreator.nestedStage( new ThenMovie<>(), this, this )
            .withSubject( movies.get(1) );
        }

        @SyntacticSugar
        public ThenMovie<Then, Then> third_movie() {
            return navigableStageCreator.nestedStage( new ThenMovie<>(), this, this )
            .withSubject( movies.get(2) );
        }

        public Then $_unrated_movies( int count ) {
            assertThat(
                movies.stream()
                .filter( movie -> isNull( movie.rating() ) )
                .count()
            ).isEqualTo( count );

            return self();
        }

        public Then $_unique_actors( int count ) {
            assertThat(
                actors.stream()
                .map( Actor::name )
                .collect( toSet() )
            ).hasSize( count );

            return self();
        }

    }

}
