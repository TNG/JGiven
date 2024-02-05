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
    private static final Logger log = LoggerFactory.getLogger(AsciiDocReportGenerator.class);

    private final AsciiDocBlockConverter blockConverter = new AsciiDocBlockConverter();
    private final HashMap<String, Tag> allTags = new HashMap<>();
    private final List<String> featureFiles = new ArrayList<>();
    private final List<String> failedScenarioFiles = new ArrayList<>();
    private final List<String> pendingScenarioFiles = new ArrayList<>();
    private final HashMap<String, List<String>> taggedScenarioFiles = new HashMap<>();
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
        if (config == null) {
            throw new IllegalStateException("AsciiDocReporter must be configured before generating a report.");
        }

        if (!prepareDirectories(config.getTargetDir())) {
            return;
        }

        if (completeReportModel == null) {
            loadReportModel();
        }

        writeFeatureFiles();

        writeIndexFileForAllScenarios();

        writeIndexFileForFailedScenarios();

        writeIndexFileForPendingScenarios();

        taggedScenarioFiles.forEach(this::writeIndexFileForTaggedScenarios);

        writeTotalStatisticsFile();

        writeIndexFileForFullReport(config.getTitle());

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
        if (!ensureDirectoryExists(this.tagsDir)) {
            return false;
        }

        featuresDir = new File(this.targetDir.getPath() + "/features");
        return ensureDirectoryExists(featuresDir);
    }

    private void writeFeatureFiles() {
        completeReportModel.getAllReportModels().stream()
                .sorted(Comparator.comparing(AsciiDocReportGenerator::byFeatureName))
                .forEach(reportModelFile -> {
                    final ReportStatistics statistics = completeReportModel.getStatistics(reportModelFile);
                    final String fileName = Files.getNameWithoutExtension(
                            reportModelFile.file().getName()) + ".asciidoc";
                    final List<String> asciiDocBlocks = collectReportBlocks(fileName, statistics, reportModelFile.model());
                    writeAsciiDocBlocksToFile(featuresDir, fileName, asciiDocBlocks);
                });
    }

    private List<String> collectReportBlocks(
            final String featureFileName,
            final ReportStatistics statistics,
            final ReportModel model) {
        allTags.putAll(model.getTagMap());

        featureFiles.add(featureFileName);
        if (statistics.numFailedScenarios > 0) {
            failedScenarioFiles.add(featureFileName);
        }
        if (statistics.numPendingScenarios > 0) {
            pendingScenarioFiles.add(featureFileName);
        }

        final AsciiDocReportModelVisitor visitor = new AsciiDocReportModelVisitor(blockConverter, statistics);
        model.accept(visitor);

        visitor.getUsedTags().forEach((tagId, count) -> {
            taggedScenarioCounts.merge(tagId, count, Integer::sum);
            taggedScenarioFiles.computeIfAbsent(tagId, key -> new ArrayList<>()).add(featureFileName);
        });

        return visitor.getAsciiDocBlocks();
    }

    private void writeIndexFileForAllScenarios() {
        final int numScenarios = this.completeReportModel.getTotalStatistics().numScenarios;
        final AsciiDocSnippetGenerator snippetGenerator = new AsciiDocSnippetGenerator(
                "All Scenarios", "scenarios in total", numScenarios, "",
                FEATURE_PATH, this.featureFiles
        );

        writeAsciiDocBlocksToFile(targetDir, "allScenarios.asciidoc", snippetGenerator.generateIndexSnippet());
    }

    private void writeIndexFileForFailedScenarios() {
        final String scenarioKind = "failed";
        final int numFailedScenarios = this.completeReportModel.getTotalStatistics().numFailedScenarios;
        final AsciiDocSnippetGenerator snippetGenerator = new AsciiDocSnippetGenerator(
                "Failed Scenarios", "failed scenarios", numFailedScenarios, "scenario-" + scenarioKind,
                FEATURE_PATH, this.failedScenarioFiles
        );

        writeAsciiDocBlocksToFile(targetDir, scenarioKind + "Scenarios.asciidoc",
                snippetGenerator.generateIndexSnippet());
    }

    private void writeIndexFileForPendingScenarios() {
        final String scenarioKind = "pending";
        final int numPendingScenarios = this.completeReportModel.getTotalStatistics().numPendingScenarios;
        final AsciiDocSnippetGenerator snippetGenerator = new AsciiDocSnippetGenerator(
                "Pending Scenarios", "pending scenarios", numPendingScenarios, "scenario-" + scenarioKind,
                FEATURE_PATH, this.pendingScenarioFiles
        );

        writeAsciiDocBlocksToFile(targetDir, scenarioKind + "Scenarios.asciidoc",
                snippetGenerator.generateIndexSnippet());
    }

    private void writeIndexFileForTaggedScenarios(final String tagId, final List<String> files) {
        final Tag tag = allTags.get(tagId);
        final String tagName = TagMapper.toAsciiDocTagName(tag);
        final int numTaggedScenarios = taggedScenarioCounts.get(tagId);
        final AsciiDocSnippetGenerator snippetGenerator = new AsciiDocSnippetGenerator(
                tag.getName(), "tagged scenarios", numTaggedScenarios, tagName,
                "../" + FEATURE_PATH, files);

        writeAsciiDocBlocksToFile(tagsDir, tagName + ".asciidoc", snippetGenerator.generateIndexSnippet());
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

        writeAsciiDocBlocksToFile(targetDir, "totalStatistics.asciidoc", Collections.singletonList(statisticsBlock));
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
        final File file = new File(directory, fileName);
        try (final PrintWriter writer = PrintWriterUtil.getPrintWriter(file)) {
            for (final String block : asciiDocBlocks) {
                writer.println(block);
                writer.println();
            }
        }
    }

}
