package com.tngtech.jgiven.report.asciidoc;

import com.google.common.io.Files;
import com.tngtech.jgiven.impl.util.PrintWriterUtil;
import com.tngtech.jgiven.report.AbstractReportConfig;
import com.tngtech.jgiven.report.AbstractReportGenerator;
import com.tngtech.jgiven.report.model.ReportModel;
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

public class AsciiDocReportGenerator extends AbstractReportGenerator {

    private static final Logger log = LoggerFactory.getLogger(AsciiDocReportGenerator.class);

    private final List<String> featureFiles = new ArrayList<>();
    private final List<String> failedScenarioFiles = new ArrayList<>();
    private final List<String> pendingScenarioFiles = new ArrayList<>();
    private final AsciiDocReportBlockConverter blockConverter;
    private File featuresDir;
    private final Comparator<ReportModelFile> byFeatureName = Comparator.comparing(
            modelFile -> null != modelFile.model.getName() ? modelFile.model.getName() : modelFile.model.getClassName());

    public AsciiDocReportGenerator() {
        blockConverter = new AsciiDocReportBlockConverter();
    }

    public AbstractReportConfig createReportConfig(String... args) {
        return new AsciiDocReportConfig(args);
    }

    public void generate() {
        File targetDir = config.getTargetDir();
        if (!targetDir.exists() && !targetDir.mkdirs()) {
            log.error("Could not ensure target directory exists {}", targetDir);
            return;
        }
        featuresDir = new File(targetDir.getPath() + "/features");
        if (!featuresDir.exists() && !featuresDir.mkdirs()) {
            log.error("Could not ensure feature directory exists {}", featuresDir);
            return;
        }

        completeReportModel.getAllReportModels().stream().sorted(byFeatureName).forEach(
                reportModelFile -> writeFeatureToFile(reportModelFile.model, reportModelFile.file,
                        completeReportModel.getStatistics(reportModelFile)));

        try (PrintWriter printWriter = PrintWriterUtil.getPrintWriter(new File(targetDir, "allScenarios.asciidoc"))) {
            printWriter.println("== All Scenarios");
            printWriter.println();

            generateFeatureIncludes(printWriter, featureFiles, "");
        }

        try (PrintWriter printWriter = PrintWriterUtil.getPrintWriter(new File(targetDir, "failedScenarios.asciidoc"))) {
            printWriter.println("== Failed Scenarios");
            printWriter.println();
            if (failedScenarioFiles.isEmpty()) {
                printWriter.println("There are no failed scenarios. Keep rocking!");
            } else {
                printWriter.println("There are " + completeReportModel.getTotalStatistics().numFailedScenarios + " failed scenarios");
                printWriter.println();
                generateFeatureIncludes(printWriter, failedScenarioFiles, "tag=scenario-failed");
            }
        }

        try (PrintWriter printWriter = PrintWriterUtil.getPrintWriter(new File(targetDir, "pendingScenarios.asciidoc"))) {
            printWriter.println("== Pending Scenarios");
            printWriter.println();
            if (pendingScenarioFiles.isEmpty()) {
                printWriter.println("There are no pending scenarios. Keep rocking!");
            } else {
                printWriter.println("There are " + completeReportModel.getTotalStatistics().numPendingScenarios + " pending scenarios");
                printWriter.println();
                generateFeatureIncludes(printWriter, pendingScenarioFiles, "tag=scenario-pending");
            }
        }

        try (PrintWriter printWriter = PrintWriterUtil.getPrintWriter(new File(targetDir, "totalStatistics.asciidoc"))) {

            Map<String, ReportStatistics> featureStatistics = completeReportModel.getAllReportModels().stream().collect(
                    Collectors.toMap(reportModelFile -> reportModelFile.model.getName(),
                            reportModelFile -> completeReportModel.getStatistics(reportModelFile)));

            printWriter.println(blockConverter.convertStatisticsBlock(featureStatistics, completeReportModel.getTotalStatistics()));
        }


        try (PrintWriter printWriter = PrintWriterUtil.getPrintWriter(new File(targetDir, "index.asciidoc"))) {
            convertIndex(printWriter, config.getTitle());
        }

    }

    private void writeFeatureToFile(final ReportModel model, final File file, final ReportStatistics statistics) {
        String featureFileName = Files.getNameWithoutExtension(file.getName()) + ".asciidoc";
        featureFiles.add(featureFileName);

        if (statistics.numFailedScenarios > 0) {
            failedScenarioFiles.add(featureFileName);
        }

        if (statistics.numPendingScenarios > 0) {
            pendingScenarioFiles.add(featureFileName);
        }

        try (PrintWriter printWriter = PrintWriterUtil.getPrintWriter(new File(featuresDir, featureFileName))) {

            AsciiDocReportModelVisitor visitor = new AsciiDocReportModelVisitor(blockConverter, statistics);
            model.accept(visitor);

            for (String block : visitor.getResult()) {
                printWriter.println(block);
                printWriter.println();
            }
        }
    }

    private void generateFeatureIncludes(final PrintWriter printWriter, final List<String> fileNames, final String tagSpec) {
        for (String fileName : fileNames) {
            printWriter.println("include::features/" + fileName + "[" + tagSpec + "]\n");
        }
    }

    private static void convertIndex(PrintWriter printWriter, final String title) {
        printWriter.print("= ");
        printWriter.println(title);

        printWriter.println(":toc: left");
        printWriter.println(":toclevels: 5");
        printWriter.println();
        printWriter.println("include::totalStatistics.asciidoc[]");
        printWriter.println();

        printWriter.println("include::allScenarios.asciidoc[]");
        printWriter.println();

        printWriter.println("include::failedScenarios.asciidoc[leveloffset=-1]");
        printWriter.println();

        printWriter.println("include::pendingScenarios.asciidoc[leveloffset=-1]");
        printWriter.println();
    }

}
