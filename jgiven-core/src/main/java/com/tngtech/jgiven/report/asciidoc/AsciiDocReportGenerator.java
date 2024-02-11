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
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
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
    private static final Logger log = LoggerFactory.getLogger(AsciiDocReportGenerator.class);

    private final AsciiDocBlockConverter blockConverter = new AsciiDocBlockConverter();
    private final Map<String, Tag> allTags = new HashMap<>();
    private final List<String> allFeatures = new ArrayList<>();
    private final List<String> failedScenarioFeatures = new ArrayList<>();
    private final List<String> pendingScenarioFeatures = new ArrayList<>();
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

        final HierarchyCalculator hierarchyCalculator = new HierarchyCalculator(allTags, taggedScenarioFeatures);

        final Map<String, Map<String, List<String>>> groupedTags = hierarchyCalculator.computeGroupedTag();

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
                    final ReportStatistics statistics = completeReportModel.getStatistics(reportModelFile);
                    final String featureName = Files.getNameWithoutExtension(reportModelFile.file().getName());
                    final List<String> asciiDocBlocks = collectReportBlocks(featureName, statistics, reportModelFile.model());

                    writeAsciiDocBlocksToFile(featuresDir, featureName, asciiDocBlocks);
                });
    }

    private void writeIndexFileForAllScenarios() {
        final int numScenarios = this.completeReportModel.getTotalStatistics().numScenarios;

        final AsciiDocSnippetGenerator snippetGenerator = new AsciiDocSnippetGenerator(
                "All Scenarios", "scenarios in total", numScenarios);

        final List<String> asciiDocBlocks = snippetGenerator.generateIntroSnippet("");
        asciiDocBlocks.addAll(snippetGenerator.generateIndexSnippet(FEATURE_PATH, this.allFeatures, "", 0));

        writeAsciiDocBlocksToFile(targetDir, "allScenarios", asciiDocBlocks);
    }

    private void writeIndexFileForFailedScenarios() {
        final int numFailedScenarios = this.completeReportModel.getTotalStatistics().numFailedScenarios;

        final AsciiDocSnippetGenerator snippetGenerator = new AsciiDocSnippetGenerator(
                "Failed Scenarios", "failed scenarios", numFailedScenarios);

        final List<String> asciiDocBlocks = snippetGenerator.generateIntroSnippet("");
        asciiDocBlocks.addAll(snippetGenerator.generateIndexSnippet(
                FEATURE_PATH, this.failedScenarioFeatures, SCENARIO_TAG + "failed", -1));

        writeAsciiDocBlocksToFile(targetDir, "failedScenarios", asciiDocBlocks);
    }

    private void writeIndexFileForPendingScenarios() {
        final int numPendingScenarios = this.completeReportModel.getTotalStatistics().numPendingScenarios;

        final AsciiDocSnippetGenerator snippetGenerator = new AsciiDocSnippetGenerator(
                "Pending Scenarios", "pending scenarios", numPendingScenarios);

        final List<String> asciiDocBlocks = snippetGenerator.generateIntroSnippet("");
        asciiDocBlocks.addAll(snippetGenerator.generateIndexSnippet(
                FEATURE_PATH, this.pendingScenarioFeatures, SCENARIO_TAG + "pending", -1));

        writeAsciiDocBlocksToFile(targetDir, "pendingScenarios", asciiDocBlocks);
    }

    private void writeIndexFileForTaggedScenarios(final String tagType, final Map<String, List<String>> taggedScenarios) {
        final Optional<Tag> firstTag = taggedScenarios.keySet().stream()
                .findFirst()
                .map(allTags::get);

        if (firstTag.isEmpty()) {
            return;
        }

        final int numTaggedScenarios = taggedScenarios.keySet().stream().mapToInt(taggedScenarioCounts::get).sum();

        final List<String> asciiDocBlocks = taggedScenarios.keySet().size() == 1
                ? singleValuedTag(taggedScenarios, firstTag.get(), numTaggedScenarios)
                : multiValuedTag(taggedScenarios, firstTag.get(), numTaggedScenarios);

        writeAsciiDocBlocksToFile(tagsDir, tagType, asciiDocBlocks);
    }

    private List<String> multiValuedTag(final Map<String, List<String>> taggedScenarios, final Tag tag, final int numTaggedScenarios) {
        final AsciiDocSnippetGenerator snippetGenerator = new AsciiDocSnippetGenerator(
                tag.getName(), "tagged scenarios", numTaggedScenarios);

        final List<String> asciiDocBlocks = snippetGenerator.generateIntroSnippet(tag.getDescription());
        taggedScenarios.forEach((tagId, features) -> {
            final List<String> snippet = snippetGenerator.generateTagSnippet(
                    allTags.get(tagId), taggedScenarioCounts.get(tagId), features, 0);
            asciiDocBlocks.addAll(snippet);
        });
        return asciiDocBlocks;
    }

    private List<String> singleValuedTag(final Map<String, List<String>> taggedScenarios, final Tag tag, final int numTaggedScenarios) {
        final AsciiDocSnippetGenerator snippetGenerator = new AsciiDocSnippetGenerator(
                tag.toString(), "tagged scenarios", numTaggedScenarios);

        final List<String> asciiDocBlocks = snippetGenerator.generateIntroSnippet(tag.getDescription());
        taggedScenarios.forEach((tagId, features) -> {
            final Tag valueTag = allTags.get(tagId);
            final String tagName = TagMapper.toAsciiDocTagName(valueTag);
            asciiDocBlocks.add("=== Scenarios");
            final List<String> snippet = snippetGenerator.generateIndexSnippet("../" + FEATURE_PATH, features, tagName, 0);
            asciiDocBlocks.addAll(snippet);
        });
        return asciiDocBlocks;
    }


    private void writeIndexFileForAllTags(final Map<String, Map<String, List<String>>> strings) {

        final List<String> tagFiles = strings.entrySet().stream()
                .sorted((o1, o2) -> {
                    final Tag tag1 = allTags.get(o1.getValue().keySet().stream().findFirst().orElse(""));
                    final Tag tag2 = allTags.get(o2.getValue().keySet().stream().findFirst().orElse(""));
                    return Objects.compare(tag1, tag2, Comparator.comparing(Tag::getName));

                })
                .map(entry -> entry.getKey().replace(' ', '_'))
                .collect(Collectors.toList());
        final Integer total = taggedScenarioCounts.values().stream().reduce(Integer::sum).orElse(999);
        final AsciiDocSnippetGenerator snippetGenerator = new AsciiDocSnippetGenerator(
                "Tags", "all tags are beautiful", total
        );

        final List<String> asciiDocBlocks = snippetGenerator.generateIntroSnippet("");
        asciiDocBlocks.addAll(snippetGenerator.generateIndexSnippet("tags", tagFiles, "", 1));
        writeAsciiDocBlocksToFile(targetDir, "allTags",
                asciiDocBlocks);

    }

    private void writeTotalStatisticsFile() {
        final ListMultimap<String, ReportStatistics> featureStatistics = completeReportModel.getAllReportModels()
                .stream()
                .collect(Multimaps.toMultimap(
                        modelFile -> modelFile.model().getName(),
                        completeReportModel::getStatistics,
                        MultimapBuilder.hashKeys().arrayListValues()::build));

        final String statisticsBlock = blockConverter.convertStatisticsBlock(
                featureStatistics, completeReportModel.getTotalStatistics());

        writeAsciiDocBlocksToFile(targetDir, "totalStatistics", Collections.singletonList(statisticsBlock));
    }

    private void writeIndexFileForFullReport(final String reportTitle) {
        final URL resourceUrl = Resources.getResource(this.getClass(), "index.asciidoc");
        try {
            final List<String> indexLines = Resources.readLines(resourceUrl, StandardCharsets.UTF_8);

            final File indexFile = new File(targetDir, "index.asciidoc");
            try (PrintWriter writer = PrintWriterUtil.getPrintWriter(indexFile)) {
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

        final AsciiDocReportModelVisitor visitor = new AsciiDocReportModelVisitor(blockConverter, statistics);
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
        final File file = new File(directory, fileName + ASCIIDOC_FILETYPE);
        try (final PrintWriter writer = PrintWriterUtil.getPrintWriter(file)) {
            for (final String block : asciiDocBlocks) {
                writer.println(block);
                writer.println();
            }
        }
    }

}
