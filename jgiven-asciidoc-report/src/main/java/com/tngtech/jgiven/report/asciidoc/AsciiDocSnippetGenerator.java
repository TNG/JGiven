package com.tngtech.jgiven.report.asciidoc;

import com.google.common.base.Strings;
import com.tngtech.jgiven.report.model.Tag;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Generate snippets for including feature files via AsciiDoc include macro.
 */
final class AsciiDocSnippetGenerator {
    private static final String LINE_BREAK = System.lineSeparator();
    private static final String LEVEL_OFFSET_ATTRIBUTE = ":leveloffset:";
    private final String title;
    private final String scenarioQualifier;
    private final int numScenarios;

    AsciiDocSnippetGenerator(
            final String title,
            final String scenarioQualifier,
            final int numScenarios) {
        this.title = title;
        this.scenarioQualifier = scenarioQualifier;
        this.numScenarios = numScenarios;
    }

    List<String> generateIntroSnippet(final String description) {
        final List<String> result = new ArrayList<>();

        result.add("== " + this.title);

        if (!description.isEmpty()) {
            result.add("+++" + LINE_BREAK + description + LINE_BREAK + "+++");
        }

        result.add(createIntroSentence(numScenarios, scenarioQualifier));

        return result;
    }

    List<String> generateIndexSnippet(final String featurePath, final List<String> features, final String tags, final int levelOffset) {
        final List<String> result = new ArrayList<>();

        final var tagSelector = Strings.isNullOrEmpty(tags) ? "" : "tag=" + tags;

        if (!features.isEmpty()) {
            result.addAll(generateIncludeSnippet("", levelOffset, featurePath, features, tagSelector));
        }

        return result;
    }

    List<String> generateTagSnippet(final Tag tag, int scenarioCount, final List<String> features) {
        final var result = new ArrayList<String>();

        result.add("=== " + tag.toString());

        final var intro = createIntroSentence(scenarioCount, scenarioQualifier);
        final var tagSelector = TagMapper.toAsciiDocTagName(tag);
        result.addAll(generateIncludeSnippet(intro, 0, "../features", features, tagSelector));

        return result;
    }

    private List<String> generateIncludeSnippet(
            final String intro,
            final int leveloffset,
            final String featurePath,
            final List<String> featureFiles,
            final String tags) {
        final var result = new ArrayList<String>();

        if (!intro.isBlank()) {
            result.add(intro);
        }

        createLevelOffset(leveloffset).ifPresent(result::add);
        featureFiles.forEach(fileName -> result.add(includeMacroFor(featurePath, fileName, tags)));
        createLevelOffset(-leveloffset).ifPresent(result::add);
        return result;
    }

    private Optional<String> createLevelOffset(final int leveloffset) {
        return Optional.of(leveloffset)
                .filter(offset -> offset != 0)
                .map(offset -> LEVEL_OFFSET_ATTRIBUTE
                        + (offset > 0 ? " +" : " -")
                        + Math.abs(offset));
    }

    private String createIntroSentence(final int scenarioCount, final String scenarioQualifier) {
        final var qualifiedScenario = scenarioQualifier.isBlank() ? "scenario" : scenarioQualifier + " scenario";

        if (scenarioCount <= 0) {
            return "There are no " + qualifiedScenario + "s. Keep rocking!";
        } else if (scenarioCount == 1) {
            return "There is " + scenarioCount + " " + qualifiedScenario + ".";
        } else {
            return "There are " + scenarioCount + " " + qualifiedScenario + "s.";
        }
    }

    private static String includeMacroFor(final String featurePath, final String featureName, final String tags) {
        return "include::" + featurePath + "/" + featureName + ".asciidoc[" + tags + "]";
    }
}
