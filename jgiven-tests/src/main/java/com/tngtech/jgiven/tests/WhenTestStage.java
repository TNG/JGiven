package com.tngtech.jgiven.tests;

import com.tngtech.jgiven.Stage;

public class WhenTestStage extends Stage<WhenTestStage> {

    public WhenTestStage something_happens() {
        return self();
    }

    public void a_step_fails() {
        throw new AssertionError( "assertion failed in test step" );
    }

}