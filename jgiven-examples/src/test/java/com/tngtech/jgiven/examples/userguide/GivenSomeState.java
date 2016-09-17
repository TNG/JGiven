package com.tngtech.jgiven.examples.userguide;

// tag::noPackage[]
import com.tngtech.jgiven.Stage;

public class GivenSomeState extends Stage<GivenSomeState> {
    public GivenSomeState some_state() {
        return self();
    }
}
// end::noPackage[]
