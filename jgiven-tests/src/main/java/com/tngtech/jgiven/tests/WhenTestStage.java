package com.tngtech.jgiven.tests;

import org.assertj.core.api.Assertions;

import com.tngtech.jgiven.Stage;

public class WhenTestStage extends Stage<WhenTestStage> {

    public void something_happens() {}

    public void a_step_fails() {
        Assertions.assertThat( true ).as( "An assertion failed" ).isFalse();
    }

}