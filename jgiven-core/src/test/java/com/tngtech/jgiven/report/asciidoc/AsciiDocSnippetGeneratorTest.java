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
        featureFileNames.add("com.example.application.FailedScenarioOne");
        featureFileNames.add("com.example.application.FailedScenarioTwo");

        // when
        AsciiDocSnippetGenerator asciiDocSnippetGenerator = new AsciiDocSnippetGenerator(
                "Failed Scenarios", "failed scenarios", 3);
        final List<String> blocks = asciiDocSnippetGenerator.generateIndexSnippet("features", featureFileNames, "scenario-failed", -1);

        // then
        assertThat(blocks).containsExactly(
                ":leveloffset: -1",
                "include::features/com.example.application.FailedScenarioOne.asciidoc[tag=scenario-failed]",
                "include::features/com.example.application.FailedScenarioTwo.asciidoc[tag=scenario-failed]",
                ":leveloffset: +1");
    }


    @Test
    public void writeIndexFileForAllScenarios() {
        // given
        final List<String> featureFileNames = new ArrayList<>();
        featureFileNames.add("com.example.application.BigFeature");
        featureFileNames.add("com.example.application.OtherFeature");


        // when
        AsciiDocSnippetGenerator asciiDocSnippetGenerator = new AsciiDocSnippetGenerator(
                "All Scenarios", "scenarios in total", 40);
        final List<String> blocks = asciiDocSnippetGenerator.generateIndexSnippet("features", featureFileNames, "", 0);

        // then
        assertThat(blocks).containsExactly(
                "include::features/com.example.application.BigFeature.asciidoc[]",
                "include::features/com.example.application.OtherFeature.asciidoc[]");
    }

}
