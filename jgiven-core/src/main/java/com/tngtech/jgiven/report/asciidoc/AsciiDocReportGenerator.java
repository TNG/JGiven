package com.tngtech.jgiven.report.asciidoc;

import com.google.common.io.Files;
import com.tngtech.jgiven.impl.util.PrintWriterUtil;
import com.tngtech.jgiven.report.AbstractReportConfig;
import com.tngtech.jgiven.report.AbstractReportGenerator;
import com.tngtech.jgiven.report.ReportBlockConverter;
import com.tngtech.jgiven.report.model.ReportModelFile;
import com.tngtech.jgiven.report.model.ReportStatistics;
import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
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

    private final List<String> featureFiles = new ArrayList<>();
    private final List<String> failedScenarioFiles = new ArrayList<>();
    private final List<String> pendingScenarioFiles = new ArrayList<>();
    private final ReportBlockConverter blockConverter = new AsciiDocReportBlockConverter();
    private File featuresDir;
    private File targetDir;


    public AbstractReportConfig createReportConfig(String... args) {
        return new AsciiDocReportConfig(args);
    }

    @Override
    public void generate() {
        if (!prepareDirectories()) {
            return;
        }

        completeReportModel.getAllReportModels().stream()
                .sorted(Comparator.comparing(AsciiDocReportGenerator::byFeatureName))
                .forEach(this::writeFeatureFile);

        writeAllScenariosFile();

        writeFailedScenariosFiled();

        writePendingScenariosFile();

        writeStatisticsFile();

        writeIndexFile(config.getTitle());

    }

    private boolean prepareDirectories() {
        targetDir = config.getTargetDir();
        if (!targetDir.exists() && !targetDir.mkdirs()) {
            log.error("Could not ensure target directory exists {}", targetDir);
            return false;
        }

        featuresDir = new File(targetDir.getPath() + "/features");
        if (!featuresDir.exists() && !featuresDir.mkdirs()) {
            log.error("Could not ensure feature directory exists {}", featuresDir);
            return false;
        }

        return true;
    }

    private void writeFeatureFile(ReportModelFile reportModelFile) {
        String featureFileName = Files.getNameWithoutExtension(reportModelFile.file.getName()) + ".asciidoc";
        featureFiles.add(featureFileName);

        final ReportStatistics statistics = completeReportModel.getStatistics(reportModelFile);
        if (statistics.numFailedScenarios > 0) {
            failedScenarioFiles.add(featureFileName);
        }
        if (statistics.numPendingScenarios > 0) {
            pendingScenarioFiles.add(featureFileName);
        }

        final AsciiDocReportModelVisitor visitor = new AsciiDocReportModelVisitor(blockConverter, statistics);
        reportModelFile.model.accept(visitor);

        writeAsciiDocToFile(featureFileName, visitor.getResult());
    }

    private void writeAsciiDocToFile(final String fileName, final List<String> asciiDocBlocks) {
        try (PrintWriter writer = PrintWriterUtil.getPrintWriter(new File(featuresDir, fileName))) {

            for (String block : asciiDocBlocks) {
                writer.println(block);
                writer.println();
            }
        }
    }

    private void writeAllScenariosFile() {
        try (PrintWriter writer = PrintWriterUtil.getPrintWriter(new File(targetDir, "allScenarios.asciidoc"))) {
            writer.println("== All Scenarios");
            writer.println();

            for (String fileName : featureFiles) {
                writer.println(includeMacroFor(fileName, ""));
                writer.println();
            }
        }
    }

    private void writeFailedScenariosFiled() {
        try (PrintWriter writer = PrintWriterUtil.getPrintWriter(new File(targetDir, "failedScenarios.asciidoc"))) {
            writer.println("== Failed Scenarios");
            writer.println();
            if (failedScenarioFiles.isEmpty()) {
                writer.println("There are no failed scenarios. Keep rocking!");
            } else {
                writer.println(
                        "There are " + completeReportModel.getTotalStatistics().numFailedScenarios
                                + " failed scenarios");
                writer.println();
                writer.println(":leveloffset: -1");
                writer.println();
                for (String fileName : failedScenarioFiles) {
                    writer.println(includeMacroFor(fileName, "tag=scenario-failed"));
                    writer.println();
                }
                writer.println(":leveloffset: +1");
            }
        }
    }

    private void writePendingScenariosFile() {
        try (PrintWriter writer = PrintWriterUtil.getPrintWriter(new File(targetDir, "pendingScenarios.asciidoc"))) {
            writer.println("== Pending Scenarios");
            writer.println();
            if (pendingScenarioFiles.isEmpty()) {
                writer.println("There are no pending scenarios. Keep rocking!");
            } else {
                writer.println(
                        "There are " + completeReportModel.getTotalStatistics().numPendingScenarios
                                + " pending scenarios");
                writer.println();
                writer.println(":leveloffset: -1");
                writer.println();
                for (String fileName : pendingScenarioFiles) {
                    writer.println(includeMacroFor(fileName, "tag=scenario-pending"));
                    writer.println();
                }
                writer.println(":leveloffset: +1");
            }
        }
    }

    private void writeStatisticsFile() {
        try (PrintWriter writer = PrintWriterUtil.getPrintWriter(new File(targetDir, "totalStatistics.asciidoc"))) {
            Map<String, ReportStatistics> featureStatistics = completeReportModel.getAllReportModels().stream()
                    .collect(Collectors.toMap(
                            reportModelFile -> reportModelFile.model.getName(),
                            reportModelFile -> completeReportModel.getStatistics(reportModelFile)));

            writer.println(blockConverter.convertStatisticsBlock(featureStatistics,
                    completeReportModel.getTotalStatistics()));
        }
    }

    private void writeIndexFile(final String reportTitle) {
        try (PrintWriter writer = PrintWriterUtil.getPrintWriter(new File(targetDir, "index.asciidoc"))) {
            writer.println("= " + reportTitle);
            writer.println(":toc: left");
            writer.println(":toclevels: 3");
            writer.println(":icons: font");
            writer.println();

            writer.println("include::totalStatistics.asciidoc[]");
            writer.println();

            writer.println("include::allScenarios.asciidoc[]");
            writer.println();

            writer.println("include::failedScenarios.asciidoc[]");
            writer.println();

            writer.println("include::pendingScenarios.asciidoc[]");
        }
    }

    private static String byFeatureName(ReportModelFile modelFile) {
        return (null != modelFile.model.getName())
                ? modelFile.model.getName()
                : modelFile.model.getClassName();
    }

    private static String includeMacroFor(final String fileName, final String tags) {
        return "include::features/" + fileName + "[" + tags + "]";
    }
}
