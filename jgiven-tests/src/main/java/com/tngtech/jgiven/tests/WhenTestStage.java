package com.tngtech.jgiven.tests;

import com.tngtech.jgiven.Stage;

public class WhenTestStage extends Stage<WhenTestStage> {

    public void something_happens() {}

    public void a_step_fails() {
        throw new AssertionError( "some assertion" );
    }

}