package com.tngtech.jgiven.example.projects.junit5;

import com.epam.reportportal.junit5.ReportPortalExtension;
import com.tngtech.jgiven.base.ScenarioTestBase;
import com.tngtech.jgiven.impl.Scenario;
import com.tngtech.jgiven.junit5.JGivenExtension;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith({ReportPortalExtension.class, JGivenExtension.class})
public class ReportPortalScenarioTest<GIVEN, WHEN, THEN> extends ScenarioTestBase<GIVEN, WHEN, THEN> {
    private Scenario<GIVEN, WHEN, THEN> scenario = createScenario();

    @Override
    public Scenario<GIVEN, WHEN, THEN> getScenario() {
        return scenario;
    }
}
