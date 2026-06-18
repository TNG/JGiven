package com.tngtech.jgiven.report.asciidoc;

import com.tngtech.jgiven.annotation.Table;
import com.tngtech.jgiven.report.ReportBlockConverter;
import com.tngtech.jgiven.report.model.DataTable;
import com.tngtech.jgiven.report.model.StepStatus;
import com.tngtech.jgiven.report.model.Word;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;

class AsciiDocStepBlockConverterTest {

    private static final int ARBITRARY_SHORT_DURATION = 3899;
    private static final long DURATION_JUST_BELOW_THRESHOLD = 9_999_999L;
    private static final long DURATION_JUST_ABOVE_THRESHOLD = 10_000_000L;
    private static final String LINE_BREAK = System.lineSeparator();
    private final ReportBlockConverter converter = new AsciiDocBlockConverter();

    ////
    // All step conversations in this section assume, that the containing scenario case was executed successful
    ////

    @Test
    void convert_fast_step() {
        // given
        List<Word> words = List.of(Word.introWord("given"), new Word("a coffee machine"));

        // when
        var block = converter.convertStepBlock(0, words, StepStatus.PASSED, DURATION_JUST_BELOW_THRESHOLD, null, false);

        // then
        assertThatBlockContainsLines(block,
            "* [.jg-intro-word]*Given* a coffee machine");
    }

    @Test
    void convert_slow_step() {
        // given
        List<Word> words = List.of(Word.introWord("given"), new Word("a coffee machine"));

        // when
        var block = converter.convertStepBlock(0, words, StepStatus.PASSED, DURATION_JUST_ABOVE_THRESHOLD, null, false);

        // then
        assertThatBlockContainsLines(block,
                "* [.jg-intro-word]*Given* a coffee machine (10ms)");
    }

    @Test
    void convert_step_with_description() {
        // given
        List<Word> words = List.of(Word.introWord("given"), new Word("a coffee machine"));

        // when
        var block = converter.convertStepBlock(0, words, StepStatus.PASSED, ARBITRARY_SHORT_DURATION,
                "It is a brand new machine.", false);

        // then
        assertThatBlockContainsLines(block,
            "* [.jg-intro-word]*Given* a coffee machine +",
            "icon:plus-circle[title=Extended Description] _+++It is a brand new machine.+++_");
    }

    @Test
    void convert_first_step_in_section() {
        // given
        List<Word> words = Collections.singletonList(Word.introWord("given"));

        // when
        var block = converter.convertFirstStepBlock(0, words, StepStatus.PASSED, ARBITRARY_SHORT_DURATION,
                null, false, "First section");

        // then
        assertThatBlockContainsLines(block,
            ".First section",
            "[unstyled.jg-step-list]",
            "* [.jg-intro-word]*Given*");
    }

    @Test
    void convert_first_step_without_section() {
        // given
        List<Word> words = Collections.singletonList(Word.introWord("given"));

        // when
        var block = converter.convertFirstStepBlock(0, words, StepStatus.PASSED, ARBITRARY_SHORT_DURATION, null, false,
                null);

        // then
        assertThatBlockContainsLines(block,
            "[unstyled.jg-step-list]",
            "* [.jg-intro-word]*Given*");
    }

    @Test
    void convert_step_with_simple_argument() {
        // given
        List<Word> words =
                List.of(Word.introWord("given"), new Word("a coffee machine with"),
                Word.argWord("ncoffees", "0", "0"),
                new Word("coffees"));

        // when
        var block = converter.convertStepBlock(0, words, StepStatus.PASSED, ARBITRARY_SHORT_DURATION, null, false);

        // then
        assertThatBlockContainsLines(block,
            "* [.jg-intro-word]*Given* a coffee machine with [.jg-argument]_++0++_ coffees");
    }

    @Test
    void convert_step_with_special_argument() {
        // given
        List<Word> words =
                List.of(Word.introWord("given"), new Word("the coffee machine"),
                        Word.argWord("state", "false", "is *not*"),
                        new Word("active"));

        // when
        var block = converter.convertStepBlock(0, words, StepStatus.PASSED, ARBITRARY_SHORT_DURATION, null, false);

        // then
        assertThatBlockContainsLines(block,
                "* [.jg-intro-word]*Given* the coffee machine [.jg-argument]_++is *not*++_ active");
    }

    @Test
    void convert_step_with_multiline_argument() {
        // given
        List<Word> words = List.of(Word.introWord("given"), new Word("a coffee machine with"),
            Word.argWord("description", "0", "very nice text" + LINE_BREAK + "and also more text"));

        // when
        var block = converter.convertStepBlock(0, words, StepStatus.PASSED, ARBITRARY_SHORT_DURATION, null, false);

        // then
        assertThatBlockContainsLines(block,
            "* [.jg-intro-word]*Given* a coffee machine with",
            "+",
            "[.jg-argument]",
            "....",
            "very nice text",
            "and also more text",
            "....");
    }


    @Test
    void convert_step_with_multiline_argument_which_itself_contains_literal_block_delimiter() {
        // given
        List<Word> words = List.of(Word.introWord("given"), new Word("a coffee machine with"),
                Word.argWord("description", "0", "text above" + LINE_BREAK + "...." + LINE_BREAK + "and text below"));

        // when
        var block = converter.convertStepBlock(0, words, StepStatus.PASSED, ARBITRARY_SHORT_DURATION, null, false);

        // then
        assertThatBlockContainsLines(block,
                "* [.jg-intro-word]*Given* a coffee machine with",
                "+",
                "[.jg-argument]",
                ".....",
                "text above",
                "....",
                "and text below",
                ".....");
    }


    @Test
    void convert_step_with_parameter() {
        // given
        var ncoffees = Word.argWord("coffee count", "0", "0");
        ncoffees.getArgumentInfo().setParameterName("coffee count");

        List<Word> words =
                List.of(Word.introWord("given"), new Word("a coffee machine with"), ncoffees, new Word("coffees"));

        // when
        var block = converter.convertStepBlock(0, words, StepStatus.PASSED, ARBITRARY_SHORT_DURATION, null, false);

        // then
        assertThatBlockContainsLines(block,
            "* [.jg-intro-word]*Given* a coffee machine with [.jg-argument]*<coffee count>* coffees");
    }

    @Test
    void convert_step_with_data_table_with_horizontal_header() {
        // given
        List<List<String>> productsTable =
                List.of(List.of("product", "price"), List.of("apples", "23"),
                        List.of("pears", "42"));
        List<Word> words = List.of(Word.introWord("given"), new Word("the products"),
            Word.argWord("products", productsTable.toString(),
                new DataTable(Table.HeaderType.HORIZONTAL, productsTable)));

        // when
        var block = converter.convertStepBlock(0, words, StepStatus.PASSED, ARBITRARY_SHORT_DURATION, null, false);

        // then
        assertThatBlockContainsLines(block,
            "* [.jg-intro-word]*Given* the products",
            "+",
            "[.jg-argumentTable%autowidth%header,cols=\"1,1\"]",
            "|===",
            "| product | price ",
            "| apples | 23 ",
            "| pears | 42 ",
            "|===");
    }

    @Test
    void convert_step_with_data_table_vertical_header() {
        // given
        List<List<String>> productsTable =
                List.of(List.of("product", "apples", "pears"), List.of("price", "23", "42"));
        List<Word> words = List.of(Word.introWord("given"), new Word("the products"),
            Word.argWord("products", productsTable.toString(),
                new DataTable(Table.HeaderType.VERTICAL, productsTable)));

        // when
        var block = converter.convertStepBlock(0, words, StepStatus.PASSED, ARBITRARY_SHORT_DURATION, null, false);

        // then
        assertThatBlockContainsLines(block,
            "* [.jg-intro-word]*Given* the products",
            "+",
            "[.jg-argumentTable%autowidth,cols=\"h,1,1\"]",
            "|===",
            "| product | apples | pears ",
            "| price | 23 | 42 ",
            "|===");
    }

    @Test
    void convert_step_with_multiline_argument_and_extended_description() {
        // given
        List<Word> words = List.of(Word.introWord("given"), new Word("a coffee machine with"),
                Word.argWord("description", "0", "very nice text" + LINE_BREAK + "and also more text"));

        // when
        var block = converter.convertStepBlock(0, words, StepStatus.PASSED, ARBITRARY_SHORT_DURATION,
                "It is a brand new machine.", false);

        // then
        assertThatBlockContainsLines(block,
                "* [.jg-intro-word]*Given* a coffee machine with",
                "+",
                "[.jg-argument]",
                "....",
                "very nice text",
                "and also more text",
                "....",
                "icon:plus-circle[title=Extended Description] _+++It is a brand new machine.+++_");
    }

    @Test
    void convert_step_with_multiline_argument_and_data_table() {
        // given
        List<List<String>> productsTable =
                List.of(List.of("product", "price"), List.of("apples", "23"));
        List<Word> words = List.of(Word.introWord("given"), new Word("a coffee machine with"),
                Word.argWord("description", "0", "very nice text" + LINE_BREAK + "and also more text"),
                new Word("and the products"), Word.argWord("products", productsTable.toString(),
                        new DataTable(Table.HeaderType.HORIZONTAL, productsTable)));

        // when
        var block = converter.convertStepBlock(0, words, StepStatus.PASSED, ARBITRARY_SHORT_DURATION, null, false);

        // then
        assertThatBlockContainsLines(block,
                "* [.jg-intro-word]*Given* a coffee machine with",
                "+",
                "[.jg-argument]",
                "....",
                "very nice text",
                "and also more text",
                "....",
                "and the products",
                "+",
                "[.jg-argumentTable%autowidth%header,cols=\"1,1\"]",
                "|===",
                "| product | price ",
                "| apples | 23 ",
                "|===");
    }

    ////
    // All step conversations in this section assume, that the containing scenario case was not executed successful
    ////

    @Test
    void convert_fast_step_within_unsuccessful_scenario_case() {
        // given
        List<Word> words = Collections.singletonList(Word.introWord("given"));

        // when
        var block = converter.convertStepBlock(0, words, StepStatus.PASSED, DURATION_JUST_BELOW_THRESHOLD, null, true);

        // then
        assertThatBlockContainsLines(block, "* [.jg-intro-word]*Given* icon:check-square[role=green]");
    }

    @ParameterizedTest
    @CsvSource({ "PASSED, check-square[role=green]", "FAILED, exclamation-circle[role=red]",
        "SKIPPED, step-forward[role=silver]", "PENDING, ban[role=silver]"})
    void convert_slow_step_within_unsuccessful_scenario_case(final StepStatus stepStatus, final String icon) {
        // given
        List<Word> words = Collections.singletonList(Word.introWord("given"));

        // when
        var block = converter.convertStepBlock(0, words, stepStatus, DURATION_JUST_ABOVE_THRESHOLD, null, true);

        // then
        assertThatBlockContainsLines(block, "* [.jg-intro-word]*Given* icon:" + icon + " (10ms)");
    }

    @Test
    void convert_step_with_description_within_unsuccessful_scenario_case() {
        // given
        List<Word> words = List.of(Word.introWord("given"), new Word("a coffee machine"));

        // when
        var block = converter.convertStepBlock(0, words, StepStatus.PENDING, ARBITRARY_SHORT_DURATION,
                "It is a brand new machine.", true);

        // then
        assertThatBlockContainsLines(block,
                "* [.jg-intro-word]*Given* a coffee machine icon:ban[role=silver] +",
                "icon:plus-circle[title=Extended Description] _+++It is a brand new machine.+++_");
    }

    @Test
    void convert_step_with_multiline_argument_within_unsuccessful_scenario_case() {
        // given
        List<Word> words = List.of(Word.introWord("given"), new Word("a coffee machine with"),
                Word.argWord("description", "0", "very nice text" + LINE_BREAK + "and also more text"));

        // when
        var block = converter.convertStepBlock(0, words, StepStatus.FAILED, ARBITRARY_SHORT_DURATION, null, true);

        // then
        assertThatBlockContainsLines(block,
                "* [.jg-intro-word]*Given* a coffee machine with icon:exclamation-circle[role=red]",
                "+",
                "[.jg-argument]",
                "....",
                "very nice text",
                "and also more text",
                "....");
    }

    @Test
    void convert_step_with_data_table_within_unsuccessful_scenario_case() {
        // given
        List<List<String>> productsTable =
                List.of(List.of("product", "price"), List.of("apples", "23"));
        List<Word> words = List.of(Word.introWord("given"), new Word("the products"),
                Word.argWord("products", productsTable.toString(),
                        new DataTable(Table.HeaderType.HORIZONTAL, productsTable)));

        // when
        var block = converter.convertStepBlock(0, words, StepStatus.SKIPPED, ARBITRARY_SHORT_DURATION, null, true);

        // then
        assertThatBlockContainsLines(block,
                "* [.jg-intro-word]*Given* the products icon:step-forward[role=silver]",
                "+",
                "[.jg-argumentTable%autowidth%header,cols=\"1,1\"]",
                "|===",
                "| product | price ",
                "| apples | 23 ",
                "|===");
    }

    private static void assertThatBlockContainsLines(final String block, final String... expectedLines) {
        final var blockLines = block.split(System.lineSeparator());
        assertThat(blockLines).hasSize(expectedLines.length).containsExactly(expectedLines);
    }
}
