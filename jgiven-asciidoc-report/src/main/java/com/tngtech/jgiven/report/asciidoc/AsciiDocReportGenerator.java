package com.tngtech.jgiven.report.asciidoc;

import com.google.common.collect.ListMultimap;
import com.google.common.collect.MultimapBuilder;
import com.google.common.collect.Multimaps;
import com.google.common.io.Files;
import com.google.common.io.Resources;
import com.tngtech.jgiven.impl.util.PrintWriterUtil;
import com.tngtech.jgiven.report.AbstractReportConfig;
import com.tngtech.jgiven.report.AbstractReportGenerator;
import com.tngtech.jgiven.report.model.ReportModel;
import com.tngtech.jgiven.report.model.ReportModelFile;
import com.tngtech.jgiven.report.model.ReportStatistics;
import com.tngtech.jgiven.report.model.Tag;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This reporter provides the functionality for reading/writing a report in AsciiDoc format.
 *
 * <p> The following flags are reused from {@link AbstractReportConfig}:
 * <ul>
 *   <li> --format= </li>
 *   <li> --sourceDir= / --dir= </li>
 *   <li> --targetDir= / --todir= </li>
 *   <li> --title= </li>
 *   <li> --exclude-empty-scenarios=&lt;boolean&gt; </li>
 *   <li> --help / -h </li>
 * </ul>
 */
public class AsciiDocReportGenerator extends AbstractReportGenerator {

    private static final String FEATURE_PATH = "features";
    private static final String ASCIIDOC_FILETYPE = ".asciidoc";
    private static final String SCENARIO_TAG = "scenario-";
    private static final String TAGGED_SCENARIO_QUALIFIER = "tagged";
    private static final Logger log = LoggerFactory.getLogger(AsciiDocReportGenerator.class);

    private final AsciiDocBlockConverter blockConverter = new AsciiDocBlockConverter();
    private final Map<String, Tag> allTags = new HashMap<>();
    private final List<String> allFeatures = new ArrayList<>();
    private final List<String> failedScenarioFeatures = new ArrayList<>();
    private final List<String> pendingScenarioFeatures = new ArrayList<>();
    private final List<String> abortedScenarioFeatures = new ArrayList<>();
    private final Map<String, List<String>> taggedScenarioFeatures = new HashMap<>();
    private final Map<String, Integer> taggedScenarioCounts = new HashMap<>();

    private File targetDir;
    private File featuresDir;
    private File tagsDir;


    @Override
    public AsciiDocReportConfig createReportConfig(String... args) {
        return new AsciiDocReportConfig(args);
    }

    @Override
    public void generate() {
        if (!loadConfigAndModel()) {
            return;
        }

        writeFeatureFiles();

        writeIndexFileForAllScenarios();

        writeIndexFileForFailedScenarios();

        writeIndexFileForPendingScenarios();

        writeIndexFileForAbortedScenarios();

        final var hierarchyCalculator = new HierarchyCalculator(allTags, taggedScenarioFeatures);

        final var groupedTags = hierarchyCalculator.computeGroupedTag();

        groupedTags.forEach(this::writeIndexFileForTaggedScenarios);

        writeIndexFileForAllTags(groupedTags);

        writeTotalStatisticsFile();

        writeIndexFileForFullReport(config.getTitle());
    }


    private boolean loadConfigAndModel() {
        if (config == null) {
            throw new IllegalStateException("AsciiDocReporter must be configured before generating a report.");
        }

        if (!prepareDirectories(config.getTargetDir())) {
            return false;
        }

        if (completeReportModel == null) {
            loadReportModel();
        }

        return true;
    }

    private void writeFeatureFiles() {
        completeReportModel.getAllReportModels().stream()
                .sorted(Comparator.comparing(AsciiDocReportGenerator::byFeatureName))
                .forEach(reportModelFile -> {
                    final var statistics = completeReportModel.getStatistics(reportModelFile);
                    final var featureName = Files.getNameWithoutExtension(reportModelFile.file().getName());
                    final var asciiDocBlocks = collectReportBlocks(featureName, statistics, reportModelFile.model());

                    writeAsciiDocBlocksToFile(featuresDir, featureName, asciiDocBlocks);
                });
    }

    private void writeIndexFileForAllScenarios() {
        final var numScenarios = this.completeReportModel.getTotalStatistics().numScenarios;

        final var snippetGenerator = new AsciiDocSnippetGenerator(
                "All Scenarios", "", numScenarios);

        final var asciiDocBlocks = snippetGenerator.generateIntroSnippet("");
        asciiDocBlocks.addAll(snippetGenerator.generateIndexSnippet(
                FEATURE_PATH, this.allFeatures, "", 0));

        writeAsciiDocBlocksToFile(targetDir, "allScenarios", asciiDocBlocks);
    }

    private void writeIndexFileForFailedScenarios() {
        final var numFailedScenarios = this.completeReportModel.getTotalStatistics().numFailedScenarios;

        final var snippetGenerator = new AsciiDocSnippetGenerator(
                "Failed Scenarios", "failed", numFailedScenarios);

        final var asciiDocBlocks = snippetGenerator.generateIntroSnippet("");
        asciiDocBlocks.addAll(snippetGenerator.generateIndexSnippet(
                FEATURE_PATH, this.failedScenarioFeatures, SCENARIO_TAG + "failed", -1));

        writeAsciiDocBlocksToFile(targetDir, "failedScenarios", asciiDocBlocks);
    }

    private void writeIndexFileForPendingScenarios() {
        final var numPendingScenarios = this.completeReportModel.getTotalStatistics().numPendingScenarios;

        final var snippetGenerator = new AsciiDocSnippetGenerator(
                "Pending Scenarios", "pending", numPendingScenarios);

        final var asciiDocBlocks = snippetGenerator.generateIntroSnippet("");
        asciiDocBlocks.addAll(snippetGenerator.generateIndexSnippet(
                FEATURE_PATH, this.pendingScenarioFeatures, SCENARIO_TAG + "pending", -1));

        writeAsciiDocBlocksToFile(targetDir, "pendingScenarios", asciiDocBlocks);
    }

    private void writeIndexFileForAbortedScenarios() {
        final var numAbortedScenarios = this.completeReportModel.getTotalStatistics().numAbortedScenarios;

        final var snippetGenerator = new AsciiDocSnippetGenerator(
                "Aborted Scenarios", "aborted", numAbortedScenarios);

        final var asciiDocBlocks = snippetGenerator.generateIntroSnippet("");
        asciiDocBlocks.addAll(snippetGenerator.generateIndexSnippet(
                FEATURE_PATH, this.abortedScenarioFeatures, SCENARIO_TAG + "aborted", -1));

        writeAsciiDocBlocksToFile(targetDir, "abortedScenarios", asciiDocBlocks);
    }

    private void writeIndexFileForTaggedScenarios(final String tagType, final Map<String, List<String>> taggedScenarios) {
        final var firstTag = taggedScenarios.keySet().stream()
                .findFirst()
                .map(allTags::get);

        if (firstTag.isEmpty()) {
            return;
        }

        final var numTaggedScenarios = taggedScenarios.keySet().stream().mapToInt(taggedScenarioCounts::get).sum();

        final var asciiDocBlocks = taggedScenarios.size() == 1
                ? singleValuedTag(taggedScenarios, firstTag.get(), numTaggedScenarios)
                : multiValuedTag(taggedScenarios, firstTag.get(), numTaggedScenarios);

        writeAsciiDocBlocksToFile(tagsDir, tagType, asciiDocBlocks);
    }

    private List<String> singleValuedTag(final Map<String, List<String>> taggedScenarios, final Tag tag, final int numTaggedScenarios) {
        final var snippetGenerator = new AsciiDocSnippetGenerator(
                tag.toString(), TAGGED_SCENARIO_QUALIFIER, numTaggedScenarios);

        final var asciiDocBlocks = snippetGenerator.generateIntroSnippet(tag.getDescription());

        taggedScenarios.forEach((tagId, features) -> {
            final var valueTag = allTags.get(tagId);
            final var tagName = TagMapper.toAsciiDocTagName(valueTag);
            asciiDocBlocks.add("=== Scenarios");
            final var snippet = snippetGenerator.generateIndexSnippet("../" + FEATURE_PATH, features, tagName, 0);
            asciiDocBlocks.addAll(snippet);
        });
        return asciiDocBlocks;
    }

    private List<String> multiValuedTag(final Map<String, List<String>> taggedScenarios, final Tag tag, final int numTaggedScenarios) {
        final var snippetGenerator = new AsciiDocSnippetGenerator(
                tag.getName(), TAGGED_SCENARIO_QUALIFIER, numTaggedScenarios);

        final var asciiDocBlocks = snippetGenerator.generateIntroSnippet(tag.getDescription());

        taggedScenarios.forEach((tagId, features) -> {
            final var snippet = snippetGenerator.generateTagSnippet(
                    allTags.get(tagId), taggedScenarioCounts.get(tagId), features);
            asciiDocBlocks.addAll(snippet);
        });
        return asciiDocBlocks;
    }

    private void writeIndexFileForAllTags(final Map<String, Map<String, List<String>>> strings) {
        final var tagFiles = strings.entrySet().stream()
                .sorted((o1, o2) -> {
                    final var tag1 = allTags.get(o1.getValue().keySet().stream().findFirst().orElse(""));
                    final var tag2 = allTags.get(o2.getValue().keySet().stream().findFirst().orElse(""));
                    return Objects.compare(tag1, tag2, Comparator.comparing(Tag::getName));

                })
                .map(entry -> entry.getKey().replace(' ', '_'))
                .toList();
        final var total = taggedScenarioCounts.values().stream().reduce(Integer::sum).orElse(999);
        final var snippetGenerator = new AsciiDocSnippetGenerator(
                "Tags", TAGGED_SCENARIO_QUALIFIER, total
        );

        final var asciiDocBlocks = snippetGenerator.generateIntroSnippet("");
        asciiDocBlocks.addAll(snippetGenerator.generateIndexSnippet("tags", tagFiles, "", 1));
        writeAsciiDocBlocksToFile(targetDir, "allTags", asciiDocBlocks);
    }

    private void writeTotalStatisticsFile() {
        final var featureStatistics = completeReportModel.getAllReportModels()
                .stream()
                .collect(Multimaps.toMultimap(
                        modelFile -> modelFile.model().getName(),
                        completeReportModel::getStatistics,
                        MultimapBuilder.hashKeys().arrayListValues()::build));

        final var statisticsBlock = blockConverter.convertStatisticsBlock(
                featureStatistics, completeReportModel.getTotalStatistics());

        writeAsciiDocBlocksToFile(targetDir, "totalStatistics", Collections.singletonList(statisticsBlock));
    }

    private void writeIndexFileForFullReport(final String reportTitle) {
        final var resourceUrl = Resources.getResource(this.getClass(), "index.asciidoc");
        try {
            final var indexLines = Resources.readLines(resourceUrl, Charset.defaultCharset());

            final var indexFile = new File(targetDir, "index.asciidoc");
            try (var writer = PrintWriterUtil.getPrintWriter(indexFile)) {
                writer.println("= " + reportTitle);
                indexLines.forEach(writer::println);
            }
        } catch (IOException e) {
            log.error("Report content could not be read.", e);
        }
    }

    private boolean prepareDirectories(final File targetDir) {
        this.targetDir = targetDir;
        if (this.targetDir == null) {
            log.error("Target directory was not configured");
            return false;
        }

        if (!ensureDirectoryExists(this.targetDir)) {
            return false;
        }

        tagsDir = new File(this.targetDir.getPath() + "/tags");
        if (!ensureDirectoryExists(tagsDir)) {
            return false;
        }

        featuresDir = new File(this.targetDir.getPath() + "/features");
        return ensureDirectoryExists(featuresDir);
    }

    private List<String> collectReportBlocks(
            final String featureName,
            final ReportStatistics statistics,
            final ReportModel model) {
        allTags.putAll(model.getTagMap());

        allFeatures.add(featureName);
        if (statistics.numFailedScenarios > 0) {
            failedScenarioFeatures.add(featureName);
        }
        if (statistics.numPendingScenarios > 0) {
            pendingScenarioFeatures.add(featureName);
        }
        if (statistics.numAbortedScenarios > 0) {
            abortedScenarioFeatures.add(featureName);
        }

        final var visitor = new AsciiDocReportModelVisitor(blockConverter, statistics);
        model.accept(visitor);

        visitor.getUsedTags().forEach((tagId, count) -> {
            taggedScenarioCounts.merge(tagId, count, Integer::sum);
            taggedScenarioFeatures.computeIfAbsent(tagId, key -> new ArrayList<>()).add(featureName);
        });

        return visitor.getAsciiDocBlocks();
    }

    private static boolean ensureDirectoryExists(final File directory) {
        if (!directory.exists() && !directory.mkdirs()) {
            log.error("Could not ensure directory exists {}", directory);
            return false;
        }
        return true;
    }

    private static String byFeatureName(final ReportModelFile modelFile) {
        return (null != modelFile.model().getName())
                ? modelFile.model().getName()
                : modelFile.model().getClassName();
    }

    private static void writeAsciiDocBlocksToFile(
            final File directory,
            final String fileName,
            final List<String> asciiDocBlocks) {
        final var file = new File(directory, fileName + ASCIIDOC_FILETYPE);
        try (final var writer = PrintWriterUtil.getPrintWriter(file)) {
            for (final var block : asciiDocBlocks) {
                writer.println(block);
                writer.println();
            }
        }
    }

}
