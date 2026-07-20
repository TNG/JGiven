package com.tngtech.jgiven.report.asciidoc;

import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;

class AsciiDocIntroSnippetGeneratorTest {

    public static Stream<Arguments> data() {
        return Stream.of(
                arguments("All Scenarios", "", 0, "There are no scenarios. Keep rocking!"),
                arguments("All Scenarios", "", 1, "There is 1 scenario."),
                arguments("All Scenarios", "", 2, "There are 2 scenarios."),
                arguments("Failed Scenarios", "failed", 0, "There are no failed scenarios. Keep rocking!"),
                arguments("Failed Scenarios", "failed", 1, "There is 1 failed scenario."),
                arguments("Failed Scenarios", "failed", 2, "There are 2 failed scenarios."),
                arguments("Pending Scenarios", "pending", 0, "There are no pending scenarios. Keep rocking!"),
                arguments("Pending Scenarios", "pending", 1, "There is 1 pending scenario."),
                arguments("Pending Scenarios", "pending", 2, "There are 2 pending scenarios."),
                arguments("Aborted Scenarios", "aborted", 0, "There are no aborted scenarios. Keep rocking!"),
                arguments("Aborted Scenarios", "aborted", 1, "There is 1 aborted scenario."),
                arguments("Aborted Scenarios", "aborted", 2, "There are 2 aborted scenarios."));
    }

    @ParameterizedTest
    @MethodSource("data")
    void generateIntroSnippetWithoutDescriptionForQualifiedScenarios(
            final String title,
            final String qualifier,
            int numScenarios,
            String expectedMessage) {
        // given
        var generator = new AsciiDocSnippetGenerator(title, qualifier, numScenarios);

        // when
        var blocks = generator.generateIntroSnippet("");

        assertThat(blocks).containsExactly("== " + title, expectedMessage);
    }

    @ParameterizedTest
    @MethodSource("data")
    void generateIntroSnippetWithDescriptionForQualifiedScenarios(
            final String title,
            final String qualifier,
            int numScenarios,
            String expectedMessage) {
        // given
        var generator = new AsciiDocSnippetGenerator(title, qualifier, numScenarios);

        // when
        final var description = """
                As a tester<br />
                I want to set some nice description<br />
                so that the report speaks for itself.""";
        var blocks = generator.generateIntroSnippet(description);

        assertThat(blocks).containsExactly("== " + title, "+++\n" + description + "\n+++", expectedMessage);
    }
}
