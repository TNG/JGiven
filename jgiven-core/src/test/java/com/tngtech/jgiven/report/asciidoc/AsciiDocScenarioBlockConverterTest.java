package com.tngtech.jgiven.report.asciidoc;

import static org.assertj.core.api.Assertions.assertThat;

import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.jgiven.report.model.ExecutionStatus;
import com.tngtech.jgiven.report.model.Tag;
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
        List<Tag> tags = List.of();
        long oneSecond = 1_000_000_000L;

        // when
        String block = converter.convertScenarioHeaderBlock("my first scenario", status, oneSecond, tags, null);

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
        List<Tag> tags = List.of(mkTag("BestTag"));
        long nineMilliseconds = 10_000_000L;

        // when
        String block = converter.convertScenarioHeaderBlock("my first scenario",
                ExecutionStatus.SCENARIO_PENDING, nineMilliseconds, tags, "");

        // then
        assertThatBlockContainsLines(block,
                "// tag::scenario-pending[]",
                "// tag::com.jgiven.ArbitraryTag-BestTag[]",
                "",
                "==== My first scenario",
                "",
                "icon:ban[role=silver] (10ms)",
                "",
                "Tags: _[.jg-tag-ArbitraryTag]#BestTag#_");
    }

    @Test
    public void convert_scenario_header_with_description_and_no_tags() {
        // given
        List<Tag> tags = List.of();
        long halfMillisecond = 500_000L;

        // when
        String block = converter.convertScenarioHeaderBlock("my first scenario",
                ExecutionStatus.SOME_STEPS_PENDING, halfMillisecond, tags, "Best scenario ever!!!");

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
        List<Tag> tags = List.of(mkTag("BestTag"));
        long threeSeconds = 3_000_000_000L;

        // when
        String block =
                converter.convertScenarioHeaderBlock("my first scenario", ExecutionStatus.SUCCESS, threeSeconds, tags,
                        "Best scenario ever!!!");

        // then
        assertThatBlockContainsLines(block,
                "// tag::scenario-successful[]",
                "// tag::com.jgiven.ArbitraryTag-BestTag[]",
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
    public void convert_scenario_header_with_multiple_tags() {
        // given
        List<Tag> tags = List.of(mkTag("BestTag"), mkTag("OtherTag"), mkTag("NicestTag"));
        long threeSeconds = 3_000_000_000L;

        // when
        String block =
                converter.convertScenarioHeaderBlock("my first scenario", ExecutionStatus.SUCCESS, threeSeconds,
                        tags, "");

        // then
        assertThatBlockContainsLines(block,
                "// tag::scenario-successful[]",
                "// tag::com.jgiven.ArbitraryTag-BestTag[]",
                "// tag::com.jgiven.ArbitraryTag-OtherTag[]",
                "// tag::com.jgiven.ArbitraryTag-NicestTag[]",
                "",
                "==== My first scenario",
                "",
                "icon:check-square[role=green] (3s 0ms)",
                "",
                "Tags: _[.jg-tag-ArbitraryTag]#BestTag#_, _[.jg-tag-ArbitraryTag]#OtherTag#_, "
                        + "_[.jg-tag-ArbitraryTag]#NicestTag#_");
    }

    @Test
    @DataProvider({"SUCCESS, successful", "FAILED, failed", "SCENARIO_PENDING, pending", "SOME_STEPS_PENDING, pending"})
    public void convert_scenario_footer_without_tags(final ExecutionStatus status, final String scenarioTag) {
        // given
        List<Tag> tags = List.of();

        // when
        String block = converter.convertScenarioFooterBlock(status, tags);

        // then
        assertThat(block).isEqualTo("// end::scenario-" + scenarioTag + "[]");
    }

    @Test
    public void convert_scenario_footer_with_a_tag() {
        // given
        List<Tag> tags = List.of(mkTag("BestTag"));

        // when
        String block = converter.convertScenarioFooterBlock(ExecutionStatus.FAILED, tags);

        // then
        assertThatBlockContainsLines(block,
                "// end::com.jgiven.ArbitraryTag-BestTag[]",
                "// end::scenario-failed[]");
    }

    @Test
    public void convert_scenario_footer_with_multiple_tags() {
        // given
        List<Tag> tags = List.of(mkTag("BestTag"), mkTag("OtherTag"), mkTag("NicestTag"));

        // when
        String block = converter.convertScenarioFooterBlock(ExecutionStatus.FAILED, tags);

        // then
        assertThatBlockContainsLines(block,
                "// end::com.jgiven.ArbitraryTag-NicestTag[]",
                "// end::com.jgiven.ArbitraryTag-OtherTag[]",
                "// end::com.jgiven.ArbitraryTag-BestTag[]",
                "// end::scenario-failed[]");
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
