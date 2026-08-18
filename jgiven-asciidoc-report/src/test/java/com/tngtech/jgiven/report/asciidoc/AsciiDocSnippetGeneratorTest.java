package com.tngtech.jgiven.report.asciidoc;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AsciiDocSnippetGeneratorTest {

    @Test
    void generateIndexForFailedScenarios() {
        // given
        final List<String> featureFileNames = new ArrayList<>();
        featureFileNames.add("com.example.application.FailedScenarioOne");
        featureFileNames.add("com.example.application.FailedScenarioTwo");

        // when
        var asciiDocSnippetGenerator = new AsciiDocSnippetGenerator(
                "Failed Scenarios", "failed", 3);
        final var blocks = asciiDocSnippetGenerator.generateIndexSnippet("features", featureFileNames, "scenario-failed", -1);

        // then
        assertThat(blocks).containsExactly(
                ":leveloffset: -1",
                "include::features/com.example.application.FailedScenarioOne.asciidoc[tag=scenario-failed]",
                "include::features/com.example.application.FailedScenarioTwo.asciidoc[tag=scenario-failed]",
                ":leveloffset: +1");
    }

    @Test
    void generateIndexForAllScenarios() {
        // given
        final List<String> featureFileNames = new ArrayList<>();
        featureFileNames.add("com.example.application.BigFeature");
        featureFileNames.add("com.example.application.OtherFeature");

        // when
        var asciiDocSnippetGenerator = new AsciiDocSnippetGenerator(
                "All Scenarios", "scenarios in total", 40);
        final var blocks = asciiDocSnippetGenerator.generateIndexSnippet("features", featureFileNames, "", 0);

        // then
        assertThat(blocks).containsExactly(
                "include::features/com.example.application.BigFeature.asciidoc[]",
                "include::features/com.example.application.OtherFeature.asciidoc[]");
    }
}
