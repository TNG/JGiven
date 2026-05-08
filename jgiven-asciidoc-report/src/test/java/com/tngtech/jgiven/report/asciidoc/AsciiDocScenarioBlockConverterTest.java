package com.tngtech.jgiven.report.asciidoc;

import com.tngtech.jgiven.report.model.ExecutionStatus;
import com.tngtech.jgiven.report.model.Tag;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;

class AsciiDocScenarioBlockConverterTest {

    private final AsciiDocBlockConverter converter = new AsciiDocBlockConverter();

    @ParameterizedTest
    @CsvSource({ "SUCCESS, successful, icon:check-square[role=green]",
            "FAILED, failed, icon:exclamation-circle[role=red]",
            "SCENARIO_PENDING, pending, icon:ban[role=silver]",
            "SOME_STEPS_PENDING, pending, icon:ban[role=silver]"})
    void convert_scenario_header_without_tags_or_description(final ExecutionStatus status,
                                                                    final String scenarioTag,
                                                                    final String humanStatus) {
        // given
        List<Tag> tags = List.of();
        var oneSecond = 1_000_000_000L;

        // when
        var block = converter.convertScenarioHeaderBlock("method_1", "my first scenario", status, oneSecond, tags, null);

        // then
        assertThatBlockContainsLines(block,
                "// tag::scenario-method_1[]",
                "// tag::status-is-" + scenarioTag + "[]",
                "",
                "==== My first scenario",
                "",
                humanStatus + " (1s 0ms)");
    }

    @Test
    void convert_scenario_header_with_a_tag_and_no_description() {
        // given
        List<Tag> tags = List.of(mkTag("BestTag"));
        var nineMilliseconds = 10_000_000L;

        // when
        var block = converter.convertScenarioHeaderBlock("method_1", "my first scenario",
                ExecutionStatus.SCENARIO_PENDING, nineMilliseconds, tags, "");

        // then
        assertThatBlockContainsLines(block,
                "// tag::scenario-method_1[]",
                "// tag::status-is-pending[]",
                "// tag::tag-com.jgiven.ArbitraryTag-BestTag[]",
                "",
                "==== My first scenario",
                "",
                "icon:ban[role=silver] (10ms)",
                "",
                "Tags: _[.jg-tag-ArbitraryTag]#BestTag#_");
    }

    @Test
    void convert_scenario_header_with_description_and_no_tags() {
        // given
        List<Tag> tags = List.of();
        var halfMillisecond = 500_000L;

        // when
        var block = converter.convertScenarioHeaderBlock("method_1", "my first scenario",
                ExecutionStatus.SOME_STEPS_PENDING, halfMillisecond, tags, "Best scenario ever!!!");

        // then
        assertThatBlockContainsLines(block,
                "// tag::scenario-method_1[]",
                "// tag::status-is-pending[]",
                "",
                "==== My first scenario",
                "",
                "icon:ban[role=silver] (0ms)",
                "",
                "+++Best scenario ever!!!+++");
    }

    @Test
    void convert_scenario_header_with_a_tag_and_description() {
        // given
        List<Tag> tags = List.of(mkTag("BestTag"));
        var threeSeconds = 3_000_000_000L;

        // when
        var block =
                converter.convertScenarioHeaderBlock("method_1", "my first scenario", ExecutionStatus.SUCCESS, threeSeconds, tags,
                        "Best scenario ever!!!");

        // then
        assertThatBlockContainsLines(block,
                "// tag::scenario-method_1[]",
                "// tag::status-is-successful[]",
                "// tag::tag-com.jgiven.ArbitraryTag-BestTag[]",
                "",
                "==== My first scenario",
                "",
                "icon:check-square[role=green] (3s 0ms)",
                "",
                "+++Best scenario ever!!!+++",
                "",
                "Tags: _[.jg-tag-ArbitraryTag]#BestTag#_");
    }

    @Test
    void convert_scenario_header_with_multiple_tags() {
        // given
        List<Tag> tags = List.of(mkTag("BestTag"), mkTag("OtherTag"), mkTag("NicestTag"));
        var threeSeconds = 3_000_000_000L;

        // when
        var block =
                converter.convertScenarioHeaderBlock("method_1", "my first scenario", ExecutionStatus.SUCCESS, threeSeconds,
                        tags, "");

        // then
        assertThatBlockContainsLines(block,
                "// tag::scenario-method_1[]",
                "// tag::status-is-successful[]",
                "// tag::tag-com.jgiven.ArbitraryTag-BestTag[]",
                "// tag::tag-com.jgiven.ArbitraryTag-OtherTag[]",
                "// tag::tag-com.jgiven.ArbitraryTag-NicestTag[]",
                "",
                "==== My first scenario",
                "",
                "icon:check-square[role=green] (3s 0ms)",
                "",
                "Tags: _[.jg-tag-ArbitraryTag]#BestTag#_, _[.jg-tag-ArbitraryTag]#OtherTag#_, "
                        + "_[.jg-tag-ArbitraryTag]#NicestTag#_");
    }

    @ParameterizedTest
    @CsvSource({ "SUCCESS, successful", "FAILED, failed", "SCENARIO_PENDING, pending", "SOME_STEPS_PENDING, pending" })
    void convert_scenario_footer_without_tags(final ExecutionStatus status, final String scenarioTag) {
        // given
        List<Tag> tags = List.of();

        // when
        var block = converter.convertScenarioFooterBlock("method_3", status, tags);

        // then
        assertThatBlockContainsLines(block,
                "// end::status-is-" + scenarioTag + "[]",
                "// end::scenario-method_3[]");
    }

    @Test
    void convert_scenario_footer_with_a_tag() {
        // given
        List<Tag> tags = List.of(mkTag("BestTag"));

        // when
        var block = converter.convertScenarioFooterBlock("method_1", ExecutionStatus.FAILED, tags);

        // then
        assertThatBlockContainsLines(block,
                "// end::tag-com.jgiven.ArbitraryTag-BestTag[]",
                "// end::status-is-failed[]",
                "// end::scenario-method_1[]");
    }

    @Test
    void convert_scenario_footer_with_multiple_tags() {
        // given
        List<Tag> tags = List.of(mkTag("BestTag"), mkTag("OtherTag"), mkTag("NicestTag"));

        // when
        var block = converter.convertScenarioFooterBlock("method_2", ExecutionStatus.FAILED, tags);

        // then
        assertThatBlockContainsLines(block,
                "// end::tag-com.jgiven.ArbitraryTag-NicestTag[]",
                "// end::tag-com.jgiven.ArbitraryTag-OtherTag[]",
                "// end::tag-com.jgiven.ArbitraryTag-BestTag[]",
                "// end::status-is-failed[]",
                "// end::scenario-method_2[]");
    }

    private static Tag mkTag(final String value) {
        final var tag = new Tag("com.jgiven.ArbitraryTag", value);
        tag.setType("ArbitraryTag");
        return tag;
    }

    private static void assertThatBlockContainsLines(final String block, final String... expectedLines) {
        final var blockLines = block.split(System.lineSeparator());
        assertThat(blockLines).hasSize(expectedLines.length).containsExactly(expectedLines);
    }
}
