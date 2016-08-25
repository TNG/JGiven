package com.tngtech.jgiven.examples.userguide;

import com.tngtech.jgiven.Stage;
//tag::noPackage[]
public class ThenSomeOutcome extends Stage<ThenSomeOutcome> {
    public ThenSomeOutcome some_outcome() {
        //code for executing test goes here
       return self();
    }
 }
//end::noPackage[]
