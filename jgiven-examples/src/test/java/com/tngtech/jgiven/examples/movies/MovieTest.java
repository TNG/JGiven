package com.tngtech.jgiven.examples.movies;

import com.tngtech.jgiven.junit.ScenarioTest;
import org.junit.Test;

public class MovieTest
extends ScenarioTest<MovieFixture.Given, MovieFixture.When, MovieFixture.Then> {

    @Test
    public void movie_test() {

        given() .a() .movie()
            .with() .the() .title( "Die Hard" )
            .and() .the() .rating( "18" )
            .and() .an() .actor()
                .with() .the() .name( "Bruce Willis" )

        .also() .a() .movie()
            .with() .the() .title( "Die Hard 2" )
            .and() .an() .actor()
                .with() .the() .name( "Bruce Willis" )

        .also() .a() .movie()
            .with() .the() .title( "The Terminator" )
            .and() .an() .actor()
                .with() .the() .name( "Arnold Schwarzenegger" );

        then() .there() .are() .$_movies( 3 )
            .the() .first_movie()
                .has() .the() .title( "Die Hard" )
            .and_the() .second_movie()
                .has() .the() .title( "Die Hard 2" )
            .and_the() .third_movie()
                .has() .the() .title( "The Terminator" )
            .and_there() .are() .$_unrated_movies( 2 )
            .and() .there() .are() .$_unique_actors( 2 );

    }

    @Test
    public void multiple_movies() {

        given() .$_movies( 5 );

        then() .there() .are() .$_movies( 5 );

    }

}
