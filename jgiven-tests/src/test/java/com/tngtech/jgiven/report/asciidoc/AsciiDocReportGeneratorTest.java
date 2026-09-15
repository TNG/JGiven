package com.tngtech.jgiven.report.asciidoc;

import com.tngtech.jgiven.JGivenScenarioTest;
import com.tngtech.jgiven.annotation.ScenarioStage;
import com.tngtech.jgiven.report.WhenReportGenerator;
import com.tngtech.jgiven.report.json.GivenJsonReports;
import com.tngtech.jgiven.report.model.GivenReportModels;
import com.tngtech.jgiven.tags.FeatureAsciiDocReport;
import java.io.IOException;
import org.junit.Test;

@FeatureAsciiDocReport
public class AsciiDocReportGeneratorTest extends
        JGivenScenarioTest<GivenReportModels<?>, WhenReportGenerator<?>, ThenAsciiDocReportGenerator<?>> {

    private static final String TAG_CLASS = "com.acme.TestTag";
    @ScenarioStage
    private GivenJsonReports<?> jsonReports;

    @Test
    public void the_AsciiDoc_reporter_generates_an_index_file_a_test_file_and_multiple_other_asciidoc_files()
            throws IOException {
        given().a_report_model();
        jsonReports.and().the_report_exist_as_JSON_file();
        when().the_asciidoc_reporter_is_executed();
        thenDefaultIndexFilesExist()
                .and().a_file_$2_exists_in_folder_$1("features", "Test.asciidoc")
                .with().content("""
                        === Test

                        icon:check-square[role=green] 1 Successful, icon:exclamation-circle[role=red] 0 Failed, icon:ban[role=silver] 0 Pending, icon:times-circle[role=gray] 0 Aborted, 1 Total (0ms)

                        // tag::scenario-something_should_happen[]
                        // tag::status-is-successful[]

                        ==== Something should happen

                        icon:check-square[role=green] (0ms)

                        [unstyled.jg-step-list]
                        * [.jg-intro-word]*Given* something

                        // end::status-is-successful[]
                        // end::scenario-something_should_happen[]

                        """)
                .and().a_file_with_name_$_exists("allTags.asciidoc")
                .with().content("""
                        == Tags

                        There are no tagged scenarios. Keep rocking!

                        """);
    }

    private ThenAsciiDocReportGenerator<?> thenDefaultIndexFilesExist() {
        return then().a_file_with_name_$_exists("index.asciidoc")
                .with().content("""
                        = JGiven Report
                        // Report title is provided via report config.
                        :toc: left
                        :toclevels: 2
                        :icons: font

                        include::totalStatistics.asciidoc[]

                        include::allScenarios.asciidoc[]

                        include::failedScenarios.asciidoc[]

                        include::pendingScenarios.asciidoc[]

                        include::abortedScenarios.asciidoc[]

                        include::allTags.asciidoc[]
                        """)
                .and().a_file_with_name_$_exists("totalStatistics.asciidoc")
                .with().content("""
                        .Total Statistics
                        [.jg-statisticsTable%autowidth%header%footer]
                        |===
                        | feature | total classes | successful scenarios | failed scenarios | pending scenarios | aborted scenarios | total scenarios | failed cases | total cases | total steps | duration
                        | null | 1 | 1 | 0 | 0 | 0 | 1 | 0 | 1 | 1 | 0ms
                        | sum | 1 | 1 | 0 | 0 | 0 | 1 | 0 | 1 | 1 | 0ms
                        |===

                        """)
                .and().a_file_with_name_$_exists("allScenarios.asciidoc")
                .with().content("""
                        == All Scenarios

                        There is 1 scenario.

                        include::features/Test.asciidoc[]

                        """)
                .and().a_file_with_name_$_exists("failedScenarios.asciidoc")
                .with().content("""
                        == Failed Scenarios

                        There are no failed scenarios. Keep rocking!

                        """)
                .and().a_file_with_name_$_exists("pendingScenarios.asciidoc")
                .with().content("""
                        == Pending Scenarios

                        There are no pending scenarios. Keep rocking!

                        """)
                .and().a_file_with_name_$_exists("abortedScenarios.asciidoc")
                .with().content("""
                        == Aborted Scenarios

                        There are no aborted scenarios. Keep rocking!

                        """);
    }

    @Test
    public void the_AsciiDoc_reporter_generates_files_that_list_tags()
            throws IOException {
        given().a_report_model()
                .and().the_first_scenario_has_tag(TAG_CLASS);
        jsonReports.and().the_report_exist_as_JSON_file();
        when().the_asciidoc_reporter_is_executed();
        thenDefaultIndexFilesExist().a_file_$2_exists_in_folder_$1("features", "Test.asciidoc")
                .with().content("""
                        === Test

                        icon:check-square[role=green] 1 Successful, icon:exclamation-circle[role=red] 0 Failed, icon:ban[role=silver] 0 Pending, icon:times-circle[role=gray] 0 Aborted, 1 Total (0ms)

                        // tag::scenario-something_should_happen[]
                        // tag::status-is-successful[]
                        // tag::tag-com.acme.TestTag[]

                        ==== Something should happen

                        icon:check-square[role=green] (0ms)

                        Tags: _[.jg-tag-com.acme.TestTag]#com.acme.TestTag#_

                        [unstyled.jg-step-list]
                        * [.jg-intro-word]*Given* something

                        // end::tag-com.acme.TestTag[]
                        // end::status-is-successful[]
                        // end::scenario-something_should_happen[]

                        """)
                .and().a_file_with_name_$_exists("allTags.asciidoc")
                .with().content("""
                        == Tags

                        There is 1 tagged scenario.

                        :leveloffset: +1

                        include::tags/com.acme.TestTag.asciidoc[]

                        :leveloffset: -1

                        """)
                .and().a_file_$2_exists_in_folder_$1("tags", TAG_CLASS + ".asciidoc")
                .with().content("""
                        == com.acme.TestTag

                        There is 1 tagged scenario.

                        === Scenarios

                        include::../features/Test.asciidoc[tag=tag-com.acme.TestTag]

                        """);
    }

    @Test
    public void the_multilines_values_are_rendered_as_literal_blocks() throws IOException {
        String content = "Some " + System.lineSeparator() + "text " + System.lineSeparator() + "with " + System.lineSeparator()
                + "newlines";
        given().a_report_model()
                .and().step_$_of_case_$_has_a_formatted_value_$_as_parameter(1, 1, content);
        jsonReports
                .and().the_report_exist_as_JSON_file();

        when().the_asciidoc_reporter_is_executed();
        then().a_file_$2_exists_in_folder_$1("features", "Test.asciidoc")
                .and().the_literal_block_is_added_$(
                        "...." + System.lineSeparator()
                                + content + System.lineSeparator()
                                + "....");
    }

}
