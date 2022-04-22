package com.tngtech.jgiven.report.model;

import static org.assertj.core.api.Assertions.assertThat;

import com.tngtech.jgiven.ScenarioTestBaseForTesting;
import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.annotation.IsTag;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Collections;
import org.junit.Test;

public class ReportModelIntegrationTest extends ScenarioTestBaseForTesting<
    ReportModelIntegrationTest.TestStage, ReportModelIntegrationTest.TestStage, ReportModelIntegrationTest.TestStage> {


    @Test
    @ChildTag
    public void parent_tags_are_present_in_the_report_model() throws Throwable {
        ReportModel model = new ReportModel();
        getScenario().setModel(model);
        getScenario().startScenario(getClass(),
            getClass().getMethod("parent_tags_are_present_in_the_report_model"),
            Collections.emptyList());
        given();
        when();
        then();
        getScenario().finished();
        assertThat(model.getTagMap()).containsKeys(
            "com.tngtech.jgiven.report.model.ReportModelIntegrationTest$ParentTag",
            "com.tngtech.jgiven.report.model.ReportModelIntegrationTest$ChildTag"
        );
    }

    static class TestStage extends Stage<TestStage> {
    }

    @IsTag
    @Retention(RetentionPolicy.RUNTIME)
    @interface ParentTag {
    }

    @IsTag
    @ParentTag
    @Retention(RetentionPolicy.RUNTIME)
    @interface ChildTag {
    }


}
