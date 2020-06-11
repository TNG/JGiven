package com.tngtech.jgiven.examples.movies.model;

public class Movie {

    private String title;
    private String rating;

    public String title() {
        return title;
    }

    public Movie title( String title ) {
        this.title = title;
        return this;
    }

    public String rating() {
        return rating;
    }

    public Movie rating( String rating ) {
        this.rating = rating;
        return this;
    }

}
