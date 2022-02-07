package com.tngtech.jgiven.example.projects.junit5;

import com.tngtech.jgiven.annotation.JGivenConfiguration;
import com.tngtech.jgiven.annotation.Pending;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@JGivenConfiguration(JGivenTestConfiguration.class)
@Tag("JUnit5_Tag")
public class JUnit5Test extends ReportPortalScenarioTest<GivenStage, WhenStage, ThenStage> {

    @Test
    public void junit5_report_success_scenario() {
        given().message("Hello JUnit");
        when().handle_message();
        then().the_result_is("Hello JUnit 5!");
    }

    @Test
    public void junit5_report_failure_scenario() {
        given().message("Hello JUnit");
        when().failure();
    }

    @Test
    @Pending
    public void junit5_report_skipped_scenario() {
    }
}
