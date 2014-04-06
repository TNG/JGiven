package com.tngtech.jgiven;

import com.tngtech.jgiven.Scenario;
import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.annotation.ExpectedScenarioState;

public class ThenScenario<SELF extends ThenScenario<?>> extends Stage<SELF> {
    @ExpectedScenarioState
    protected Scenario<?, ?, ?> scenario;

}
