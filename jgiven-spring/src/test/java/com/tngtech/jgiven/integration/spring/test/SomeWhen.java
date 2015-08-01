package com.tngtech.jgiven.integration.spring.test;

import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.annotation.ExpectedScenarioState;
import com.tngtech.jgiven.annotation.ProvidedScenarioState;

public class SomeWhen extends Stage<SomeWhen> {

    @ExpectedScenarioState
    TestBean testBean;

    @ProvidedScenarioState
    String result;

    public SomeWhen is_used_in_combination_with_ordinary_stages() {
        result = testBean.computeSomething();
        return this;
    }

}
