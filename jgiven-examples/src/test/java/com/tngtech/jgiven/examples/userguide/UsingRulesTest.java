package com.tngtech.jgiven.examples.userguide;

import com.tngtech.jgiven.annotation.ScenarioStage;
import com.tngtech.jgiven.junit.ScenarioExecutionRule;
import com.tngtech.jgiven.junit.ScenarioReportRule;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

//tag::noPackage[]

public class UsingRulesTest {

    @ClassRule
    public static final ScenarioReportRule writerRule = new ScenarioReportRule();

    @Rule
    public final ScenarioExecutionRule scenarioRule = new ScenarioExecutionRule();

    @ScenarioStage
    GivenSomeState someState;

    @ScenarioStage
    WhenSomeAction someAction;

    @ScenarioStage
    ThenSomeOutcome someOutcome;

    @Test
    public void something_should_happen() {
        someState.given().some_state();
        someAction.when().some_action();
        someOutcome.then().some_outcome();
    }
}
//end::noPackage[]
