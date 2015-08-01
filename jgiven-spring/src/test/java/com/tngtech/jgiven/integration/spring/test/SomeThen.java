package com.tngtech.jgiven.integration.spring.test;

import org.assertj.core.api.Assertions;

import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.annotation.ExpectedScenarioState;

public class SomeThen extends Stage<SomeThen> {

    @ExpectedScenarioState
    String result;

    public SomeThen mixing_them_works_as_expected() {
        Assertions.assertThat(result).isEqualTo("result");
        return this;
    }
}
