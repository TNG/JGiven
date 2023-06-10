package com.tngtech.jgiven.report.asciidoc;

import static org.assertj.core.api.Assertions.assertThat;

import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.jgiven.report.model.ExecutionStatus;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(DataProviderRunner.class)
public class AsciiDocScenarioBlockConverterTest {

    private final AsciiDocReportBlockConverter converter = new AsciiDocReportBlockConverter();

    @Test
    @DataProvider({"SUCCESS, successful, icon:check-square[role=green]",
        "FAILED, failed, icon:exclamation-circle[role=red]",
        "SCENARIO_PENDING, pending, icon:ban[role=silver]",
        "SOME_STEPS_PENDING, pending, icon:ban[role=silver]"})
    public void convert_scenario_header_without_tags_or_description(final ExecutionStatus status,
                                                                    final String scenarioTag,
                                                                    final String humanStatus) {
        // given
        List<String> tagNames = new ArrayList<>();

        // when
        String block = converter.convertScenarioHeaderBlock("my first scenario", status, 1000300000L, tagNames, null);

        // then
        assertThatBlockContainsLines(block,
            "// tag::scenario-" + scenarioTag + "[]",
            "",
            "==== My first scenario",
            "",
            humanStatus + " (1s 0ms)");
    }

    @Test
    public void convert_scenario_header_with_tags_and_no_description() {
        // given
        List<String> tagNames = new ArrayList<>();
        tagNames.add("Best Tag");

        // when
        String block = converter.convertScenarioHeaderBlock("my first scenario",
            ExecutionStatus.SCENARIO_PENDING, 9000000L, tagNames, "");

        // then
        assertThatBlockContainsLines(block,
            "// tag::scenario-pending[]",
            "",
            "==== My first scenario",
            "",
            "icon:ban[role=silver] (9ms)",
            "",
            "Tags: _Best Tag_");
    }

    @Test
    public void convert_scenario_header_with_description_and_no_tags() {
        // given
        List<String> tagNames = new ArrayList<>();

        // when
        String block = converter.convertScenarioHeaderBlock("my first scenario",
            ExecutionStatus.SOME_STEPS_PENDING, 500000L, tagNames, "Best scenario ever!!!");

        // then
        assertThatBlockContainsLines(block,
            "// tag::scenario-pending[]",
            "",
            "==== My first scenario",
            "",
            "icon:ban[role=silver] (0ms)",
            "",
            "+++Best scenario ever!!!+++");
    }

    @Test
    public void convert_scenario_header_with_tags_and_description() {
        // given
        List<String> tagNames = new ArrayList<>();
        tagNames.add("Best Tag");

        // when
        String block =
            converter.convertScenarioHeaderBlock("my first scenario", ExecutionStatus.SUCCESS, 3000000000L, tagNames,
                "Best scenario ever!!!");

        // then
        assertThatBlockContainsLines(block,
            "// tag::scenario-successful[]",
            "",
            "==== My first scenario",
            "",
            "icon:check-square[role=green] (3s 0ms)",
            "",
            "+++Best scenario ever!!!+++",
            "",
            "Tags: _Best Tag_");
    }

    @Test
    @DataProvider({"SUCCESS, successful", "FAILED, failed", "SCENARIO_PENDING, pending", "SOME_STEPS_PENDING, pending"})
    public void convert_scenario_footer(final ExecutionStatus status, final String scenarioTag) {
        // given

        // when
        String block = converter.convertScenarioFooterBlock(status);

        // then
        assertThat(block).isEqualTo("// end::scenario-" + scenarioTag + "[]");
    }

    private static void assertThatBlockContainsLines(final String block, final String... expectedLines) {
        final String[] blockLines = block.split(System.lineSeparator());
        assertThat(blockLines).hasSize(expectedLines.length).containsExactly(expectedLines);
    }
}
