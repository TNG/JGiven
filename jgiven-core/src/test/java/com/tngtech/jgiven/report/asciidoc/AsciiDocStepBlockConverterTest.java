package com.tngtech.jgiven.report.asciidoc;

import static org.assertj.core.api.Assertions.assertThat;

import com.google.common.collect.ImmutableList;
import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.jgiven.annotation.Table;
import com.tngtech.jgiven.report.ReportBlockConverter;
import com.tngtech.jgiven.report.model.DataTable;
import com.tngtech.jgiven.report.model.StepStatus;
import com.tngtech.jgiven.report.model.Word;
import java.util.Collections;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(DataProviderRunner.class)
public class AsciiDocStepBlockConverterTest {

    private static final int ARBITRARY_DURATION = 3899;
    private final ReportBlockConverter converter = new AsciiDocReportBlockConverter();

    @Test
    public void convert_step_without_description() {
        // given
        List<Word> words = ImmutableList.of(Word.introWord("given"), new Word("a coffee machine"));

        // when
        String block = converter.convertStepBlock(0, words, StepStatus.PASSED, ARBITRARY_DURATION, null, false);

        // then
        assertThatBlockContainsLines(block,
            "* [.jg-intro-word]*Given* a coffee machine");
    }

    @Test
    public void convert_step_with_description() {
        // given
        List<Word> words = ImmutableList.of(Word.introWord("given"), new Word("a coffee machine"));

        // when
        String block = converter.convertStepBlock(0, words, StepStatus.PASSED, ARBITRARY_DURATION,
                "It is a brand new machine.", false);

        // then
        assertThatBlockContainsLines(block,
            "* [.jg-intro-word]*Given* a coffee machine +",
            "  _+++It is a brand new machine.+++_");
    }

    @Test
    @DataProvider({"PASSED, check-square[role=green]", "FAILED, exclamation-circle[role=red]",
        "SKIPPED, step-forward[role=silver]", "PENDING, ban[role=silver]"})
    public void convert_step_from_unsuccessful_scenario_case(final StepStatus stepStatus, final String icon) {
        // given
        List<Word> words = Collections.singletonList(Word.introWord("given"));

        // when
        String block = converter.convertStepBlock(0, words, stepStatus, 3000899, null, true);

        // then
        assertThatBlockContainsLines(block, "* [.jg-intro-word]*Given* icon:" + icon + " (3ms)");
    }

    @Test
    public void convert_first_step_in_section() {
        // given
        List<Word> words = Collections.singletonList(Word.introWord("given"));

        // when
        String block = converter.convertFirstStepBlock(0, words, StepStatus.PASSED, ARBITRARY_DURATION,
                null, false, "First section");

        // then
        assertThatBlockContainsLines(block,
            ".First section",
            "[unstyled.jg-step-list]",
            "* [.jg-intro-word]*Given*");
    }

    @Test
    public void convert_first_step_without_section() {
        // given
        List<Word> words = Collections.singletonList(Word.introWord("given"));

        // when
        String block = converter.convertFirstStepBlock(0, words, StepStatus.PASSED, ARBITRARY_DURATION, null, false,
                null);

        // then
        assertThatBlockContainsLines(block,
            "[unstyled.jg-step-list]",
            "* [.jg-intro-word]*Given*");
    }

    @Test
    public void convert_step_with_simple_argument() {
        // given
        List<Word> words =
            ImmutableList.of(Word.introWord("given"), new Word("a coffee machine with"),
                Word.argWord("ncoffees", "0", "0"),
                new Word("coffees"));

        // when
        String block = converter.convertStepBlock(0, words, StepStatus.PASSED, ARBITRARY_DURATION, null, false);

        // then
        assertThatBlockContainsLines(block,
            "* [.jg-intro-word]*Given* a coffee machine with [.jg-argument]_++0++_ coffees");
    }

    @Test
    public void convert_step_with_special_argument() {
        // given
        List<Word> words =
                ImmutableList.of(Word.introWord("given"), new Word("the coffee machine"),
                        Word.argWord("state", "false", "is *not*"),
                        new Word("active"));

        // when
        String block = converter.convertStepBlock(0, words, StepStatus.PASSED, ARBITRARY_DURATION, null, false);

        // then
        assertThatBlockContainsLines(block,
                "* [.jg-intro-word]*Given* the coffee machine [.jg-argument]_++is *not*++_ active");
    }

    @Test
    public void convert_step_with_multiline_argument() {
        // given
        List<Word> words = ImmutableList.of(Word.introWord("given"), new Word("a coffee machine with"),
            Word.argWord("description", "0", "very nice text\nand also more text"));

        // when
        String block = converter.convertStepBlock(0, words, StepStatus.PASSED, ARBITRARY_DURATION, null, false);

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
    public void convert_step_with_parameter() {
        // given
        Word ncoffees = Word.argWord("coffee count", "0", "0");
        ncoffees.getArgumentInfo().setParameterName("coffee count");

        List<Word> words =
            ImmutableList.of(Word.introWord("given"), new Word("a coffee machine with"), ncoffees, new Word("coffees"));

        // when
        String block = converter.convertStepBlock(0, words, StepStatus.PASSED, ARBITRARY_DURATION, null, false);

        // then
        assertThatBlockContainsLines(block,
            "* [.jg-intro-word]*Given* a coffee machine with [.jg-argument]*<coffee count>* coffees");
    }

    @Test
    public void convert_step_with_data_table_with_horizontal_header() {
        // given
        ImmutableList<List<String>> productsTable =
            ImmutableList.of(ImmutableList.of("product", "price"), ImmutableList.of("apples", "23"),
                ImmutableList.of("pears", "42"));
        List<Word> words = ImmutableList.of(Word.introWord("given"), new Word("the products"),
            Word.argWord("products", productsTable.toString(),
                new DataTable(Table.HeaderType.HORIZONTAL, productsTable)));

        // when
        String block = converter.convertStepBlock(0, words, StepStatus.PASSED, ARBITRARY_DURATION, null, false);

        // then
        assertThatBlockContainsLines(block,
            "* [.jg-intro-word]*Given* the products",
            "+",
            "[.jg-argumentTable%header,cols=\"1,1\"]",
            "|===",
            "| product | price ",
            "| apples | 23 ",
            "| pears | 42 ",
            "|===");
    }

    @Test
    public void convert_step_with_data_table_vertical_header() {
        // given
        ImmutableList<List<String>> productsTable =
            ImmutableList.of(ImmutableList.of("product", "apples", "pears"), ImmutableList.of("price", "23", "42"));
        List<Word> words = ImmutableList.of(Word.introWord("given"), new Word("the products"),
            Word.argWord("products", productsTable.toString(),
                new DataTable(Table.HeaderType.VERTICAL, productsTable)));

        // when
        String block = converter.convertStepBlock(0, words, StepStatus.PASSED, ARBITRARY_DURATION, null, false);

        // then
        assertThatBlockContainsLines(block,
            "* [.jg-intro-word]*Given* the products",
            "+",
            "[.jg-argumentTable,cols=\"h,1,1\"]",
            "|===",
            "| product | apples | pears ",
            "| price | 23 | 42 ",
            "|===");
    }

    private static void assertThatBlockContainsLines(final String block, final String... expectedLines) {
        final String[] blockLines = block.split(System.lineSeparator());
        assertThat(blockLines).hasSize(expectedLines.length).containsExactly(expectedLines);
    }
}
