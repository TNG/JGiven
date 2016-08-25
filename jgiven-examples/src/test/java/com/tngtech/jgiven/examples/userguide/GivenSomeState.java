package com.tngtech.jgiven.examples.userguide;

// tag::noPackage[]
import com.tngtech.jgiven.Stage;

public class GivenSomeState extends Stage<GivenSomeState> {
    public GivenSomeState some_state() {
        //code for executing test goes here
       return self();
    }
 }
// end::noPackage[]
