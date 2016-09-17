package com.tngtech.jgiven.examples.userguide;

//tag::noPackage[]
import com.tngtech.jgiven.Stage;

public class ThenSomeOutcome extends Stage<ThenSomeOutcome> {
    public ThenSomeOutcome some_outcome() {
        return self();
    }
}
//end::noPackage[]
