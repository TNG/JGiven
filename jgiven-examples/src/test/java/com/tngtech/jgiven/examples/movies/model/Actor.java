package com.tngtech.jgiven.examples.movies.model;

public class Actor {

    private String name;

    public String name() {
        return name;
    }

    public Actor name( String name ) {
        this.name = name;
        return this;
    }

}
