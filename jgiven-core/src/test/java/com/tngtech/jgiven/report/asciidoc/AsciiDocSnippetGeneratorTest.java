package com.tngtech.jgiven.report.asciidoc;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;
import org.junit.Test;

public class AsciiDocSnippetGeneratorTest {

    @Test
    public void writeIndexFileForFailedScenarios() {
        // given
        final List<String> featureFileNames = new ArrayList<>();
        featureFileNames.add("com.example.application.FailedScenarioOne.asciidoc");
        featureFileNames.add("com.example.application.FailedScenarioTwo.asciidoc");

        // when
        AsciiDocSnippetGenerator asciiDocSnippetGenerator = new AsciiDocSnippetGenerator(
                "Failed Scenarios", "failed scenarios", 3, "scenario-failed", "features", featureFileNames);
        final List<String> blocks = asciiDocSnippetGenerator.generateIndexSnippet();

        // then
        assertThat(blocks).containsExactly(
                "== Failed Scenarios",
                "There are 3 failed scenarios.",
                ":leveloffset: -1",
                "include::features/com.example.application.FailedScenarioOne.asciidoc[tag=scenario-failed]",
                "include::features/com.example.application.FailedScenarioTwo.asciidoc[tag=scenario-failed]",
                ":leveloffset: +1");
    }


    @Test
    public void writeIndexFileForAllScenarios() {
        // given
        final List<String> featureFileNames = new ArrayList<>();
        featureFileNames.add("com.example.application.BigFeature.asciidoc");
        featureFileNames.add("com.example.application.OtherFeature.asciidoc");


        // when
        AsciiDocSnippetGenerator asciiDocSnippetGenerator = new AsciiDocSnippetGenerator(
                "All Scenarios", "scenarios in total", 40, "", "features", featureFileNames);
        final List<String> blocks = asciiDocSnippetGenerator.generateIndexSnippet();

        // then
        assertThat(blocks).containsExactly(
                "== All Scenarios",
                "There are 40 scenarios in total.",
                "include::features/com.example.application.BigFeature.asciidoc[]",
                "include::features/com.example.application.OtherFeature.asciidoc[]");
    }

}
