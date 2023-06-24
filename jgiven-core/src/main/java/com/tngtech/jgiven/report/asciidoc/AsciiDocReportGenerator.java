package com.tngtech.jgiven.report.asciidoc;

import com.google.common.io.Files;
import com.google.common.io.Resources;
import com.tngtech.jgiven.impl.util.PrintWriterUtil;
import com.tngtech.jgiven.report.AbstractReportConfig;
import com.tngtech.jgiven.report.AbstractReportGenerator;
import com.tngtech.jgiven.report.model.ReportModelFile;
import com.tngtech.jgiven.report.model.ReportStatistics;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
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

    private static final Logger log = LoggerFactory.getLogger(AsciiDocReportGenerator.class);

    private final AsciiDocReportBlockConverter blockConverter = new AsciiDocReportBlockConverter();
    private final List<String> featureFiles = new ArrayList<>();
    private final List<String> failedScenarioFiles = new ArrayList<>();
    private final List<String> pendingScenarioFiles = new ArrayList<>();
    private File targetDir;
    private File featuresDir;

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

        featuresDir = new File(this.targetDir.getPath() + "/features");
        return ensureDirectoryExists(featuresDir);
    }

    private void writeFeatureFiles() {
        completeReportModel.getAllReportModels().stream()
                .sorted(Comparator.comparing(AsciiDocReportGenerator::byFeatureName))
                .forEach(reportModelFile -> {
                    final String featureFileName = Files.getNameWithoutExtension(
                            reportModelFile.file().getName()) + ".asciidoc";
                    writeAsciiDocBlocksToFile(new File(featuresDir, featureFileName),
                            collectReportBlocks(reportModelFile, featureFileName));
                });
    }

    private List<String> collectReportBlocks(final ReportModelFile reportModelFile, final String featureFileName) {
        featureFiles.add(featureFileName);

        final ReportStatistics statistics = completeReportModel.getStatistics(reportModelFile);
        if (statistics.numFailedScenarios > 0) {
            failedScenarioFiles.add(featureFileName);
        }
        if (statistics.numPendingScenarios > 0) {
            pendingScenarioFiles.add(featureFileName);
        }

        final AsciiDocReportModelVisitor visitor = new AsciiDocReportModelVisitor(blockConverter, statistics);
        reportModelFile.model().accept(visitor);

        return visitor.getResult();
    }

    private void writeIndexFileForAllScenarios() {
        final AsciiDocSnippetGenerator snippetGenerator = new AsciiDocSnippetGenerator(
                "All Scenarios", "scenarios in total", this.featureFiles, "",
                this.completeReportModel.getTotalStatistics().numScenarios);

        writeAsciiDocBlocksToFile(new File(targetDir, "allScenarios.asciidoc"),
                snippetGenerator.generateIndexSnippet());
    }

    private void writeIndexFileForFailedScenarios() {
        final String scenarioKind = "failed";
        final AsciiDocSnippetGenerator snippetGenerator = new AsciiDocSnippetGenerator(
                "Failed Scenarios", "failed scenarios", this.failedScenarioFiles, scenarioKind,
                this.completeReportModel.getTotalStatistics().numFailedScenarios);

        writeAsciiDocBlocksToFile(new File(targetDir, scenarioKind + "Scenarios.asciidoc"),
                snippetGenerator.generateIndexSnippet());
    }

    private void writeIndexFileForPendingScenarios() {
        final String scenarioKind = "pending";
        final AsciiDocSnippetGenerator snippetGenerator = new AsciiDocSnippetGenerator(
                "Pending Scenarios", "pending scenarios", this.pendingScenarioFiles, scenarioKind,
                this.completeReportModel.getTotalStatistics().numPendingScenarios);

        writeAsciiDocBlocksToFile(new File(targetDir, scenarioKind + "Scenarios.asciidoc"),
                snippetGenerator.generateIndexSnippet());
    }

    private void writeTotalStatisticsFile() {
        final Map<String, ReportStatistics> featureStatistics = completeReportModel.getAllReportModels().stream()
                .collect(Collectors.toMap(
                        reportModelFile -> reportModelFile.model().getName(),
                        reportModelFile -> completeReportModel.getStatistics(reportModelFile)));

        final String statisticsBlock = blockConverter.convertStatisticsBlock(
                featureStatistics, completeReportModel.getTotalStatistics());

        writeAsciiDocBlocksToFile(new File(targetDir, "totalStatistics.asciidoc"),
                Collections.singletonList(statisticsBlock));
    }

    private void writeIndexFileForFullReport(final String reportTitle) {
        final URL resourceUrl = Resources.getResource(this.getClass(), "index.asciidoc");
        try {
            final List<String> indexLines = Resources.readLines(resourceUrl, Charset.defaultCharset());

            try (PrintWriter writer = PrintWriterUtil.getPrintWriter(new File(targetDir, "index.asciidoc"))) {
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

    private static String byFeatureName(ReportModelFile modelFile) {
        return (null != modelFile.model().getName())
                ? modelFile.model().getName()
                : modelFile.model().getClassName();
    }

    private static void writeAsciiDocBlocksToFile(final File file, final List<String> asciiDocBlocks) {
        try (final PrintWriter writer = PrintWriterUtil.getPrintWriter(file)) {
            for (final String block : asciiDocBlocks) {
                writer.println(block);
                writer.println();
            }
        }
    }

}
