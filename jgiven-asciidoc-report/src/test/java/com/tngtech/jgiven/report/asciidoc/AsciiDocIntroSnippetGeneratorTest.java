package com.tngtech.jgiven.report.asciidoc;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(Parameterized.class)
public class AsciiDocIntroSnippetGeneratorTest {

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {"All Scenarios", "", 0, "There are no scenarios. Keep rocking!"},
                {"All Scenarios", "", 1, "There is 1 scenario."},
                {"All Scenarios", "", 2, "There are 2 scenarios."},
                {"Failed Scenarios", "failed", 0, "There are no failed scenarios. Keep rocking!"},
                {"Failed Scenarios", "failed", 1, "There is 1 failed scenario."},
                {"Failed Scenarios", "failed", 2, "There are 2 failed scenarios."},
                {"Pending Scenarios", "pending", 0, "There are no pending scenarios. Keep rocking!"},
                {"Pending Scenarios", "pending", 1, "There is 1 pending scenario."},
                {"Pending Scenarios", "pending", 2, "There are 2 pending scenarios."},
                {"Aborted Scenarios", "aborted", 0, "There are no aborted scenarios. Keep rocking!"},
                {"Aborted Scenarios", "aborted", 1, "There is 1 aborted scenario."},
                {"Aborted Scenarios", "aborted", 2, "There are 2 aborted scenarios."},
        });
    }

    private final String title;
    private final String qualifier;
    private final int numScenarios;
    private final String expectedMessage;

    public AsciiDocIntroSnippetGeneratorTest(final String title, final String qualifier, int numScenarios, String expectedMessage) {
        this.title = title;
        this.qualifier = qualifier;
        this.numScenarios = numScenarios;
        this.expectedMessage = expectedMessage;
    }

    @Test
    public void generateIntroSnippetWithoutDescriptionForQualifiedScenarios() {
        // given
        AsciiDocSnippetGenerator generator = new AsciiDocSnippetGenerator(title, qualifier, numScenarios);

        // when
        List<String> blocks = generator.generateIntroSnippet("");

        assertThat(blocks).containsExactly("== " + title, expectedMessage);
    }

    @Test
    public void generateIntroSnippetWithDescriptionForQualifiedScenarios() {
        // given
        AsciiDocSnippetGenerator generator = new AsciiDocSnippetGenerator(title, qualifier, numScenarios);

        // when
        final String description = """
                As a tester<br />
                I want to set some nice description<br />
                so that the report speaks for itself.""";
        List<String> blocks = generator.generateIntroSnippet(description);

        assertThat(blocks).containsExactly("== " + title, "+++\n" + description + "\n+++", expectedMessage);
    }
}
