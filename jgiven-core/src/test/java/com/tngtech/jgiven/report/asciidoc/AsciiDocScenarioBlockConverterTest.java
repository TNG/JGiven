package com.tngtech.jgiven.report.asciidoc;

import static org.assertj.core.api.Assertions.assertThat;

import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.jgiven.report.model.ExecutionStatus;
import com.tngtech.jgiven.report.model.Tag;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(DataProviderRunner.class)
public class AsciiDocScenarioBlockConverterTest {

    private final AsciiDocBlockConverter converter = new AsciiDocBlockConverter();

    @Test
    @DataProvider({"SUCCESS, successful, icon:check-square[role=green]",
        "FAILED, failed, icon:exclamation-circle[role=red]",
        "SCENARIO_PENDING, pending, icon:ban[role=silver]",
        "SOME_STEPS_PENDING, pending, icon:ban[role=silver]"})
    public void convert_scenario_header_without_tags_or_description(final ExecutionStatus status,
                                                                    final String scenarioTag,
                                                                    final String humanStatus) {
        // given
        List<Tag> tagNames = new ArrayList<>();
        final long oneSecond = 1_000_000_000L;

        // when
        String block = converter.convertScenarioHeaderBlock("my first scenario", status, oneSecond, tagNames, null);

        // then
        assertThatBlockContainsLines(block,
            "// tag::scenario-" + scenarioTag + "[]",
            "",
            "==== My first scenario",
            "",
            humanStatus + " (1s 0ms)");
    }

    @Test
    public void convert_scenario_header_with_a_tag_and_no_description() {
        // given
        List<Tag> tagNames = new ArrayList<>();
        tagNames.add(mkTag("Best Tag"));
        final long nineMilliseconds = 10_000_000L;

        // when
        String block = converter.convertScenarioHeaderBlock("my first scenario",
                ExecutionStatus.SCENARIO_PENDING, nineMilliseconds, tagNames, "");

        // then
        assertThatBlockContainsLines(block,
            "// tag::scenario-pending[]",
            "",
            "==== My first scenario",
            "",
            "icon:ban[role=silver] (10ms)",
            "",
            "Tags: _[.jg-tag-ArbitraryTag]#Best Tag#_");
    }

    @Test
    public void convert_scenario_header_with_description_and_no_tags() {
        // given
        List<Tag> tagNames = new ArrayList<>();
        final long halfMillisecond = 500_000L;

        // when
        String block = converter.convertScenarioHeaderBlock("my first scenario",
            ExecutionStatus.SOME_STEPS_PENDING, halfMillisecond, tagNames, "Best scenario ever!!!");

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
    public void convert_scenario_header_with_a_tag_and_description() {
        // given
        List<Tag> tagNames = new ArrayList<>();
        tagNames.add(mkTag("Best Tag"));
        final long threeSeconds = 3_000_000_000L;

        // when
        String block =
            converter.convertScenarioHeaderBlock("my first scenario", ExecutionStatus.SUCCESS, threeSeconds, tagNames,
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
            "Tags: _[.jg-tag-ArbitraryTag]#Best Tag#_");
    }

    @Test
    public void convert_scenario_header_with_multiple_tags() {
        // given
        List<Tag> tagNames = new ArrayList<>();
        tagNames.add(mkTag("Best Tag"));
        tagNames.add(mkTag("Other Tag"));
        tagNames.add(mkTag("Nicest Tag"));
        final long threeSeconds = 3_000_000_000L;

        // when
        String block =
                converter.convertScenarioHeaderBlock("my first scenario", ExecutionStatus.SUCCESS, threeSeconds,
                        tagNames, "");

        // then
        assertThatBlockContainsLines(block,
                "// tag::scenario-successful[]",
                "",
                "==== My first scenario",
                "",
                "icon:check-square[role=green] (3s 0ms)",
                "",
                "Tags: _[.jg-tag-ArbitraryTag]#Best Tag#_, _[.jg-tag-ArbitraryTag]#Other Tag#_, "
                        + "_[.jg-tag-ArbitraryTag]#Nicest Tag#_");
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

    private static Tag mkTag(final String value) {
        final Tag tag = new Tag("com.jgiven.ArbitraryTag", value);
        tag.setType("ArbitraryTag");
        return tag;
    }

    private static void assertThatBlockContainsLines(final String block, final String... expectedLines) {
        final String[] blockLines = block.split(System.lineSeparator());
        assertThat(blockLines).hasSize(expectedLines.length).containsExactly(expectedLines);
    }
}
