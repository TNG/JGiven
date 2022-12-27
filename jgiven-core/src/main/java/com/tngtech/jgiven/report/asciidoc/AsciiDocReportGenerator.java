package com.tngtech.jgiven.report.asciidoc;

import com.google.common.collect.Lists;
import com.google.common.io.Files;
import com.tngtech.jgiven.impl.util.PrintWriterUtil;
import com.tngtech.jgiven.report.AbstractReportConfig;
import com.tngtech.jgiven.report.AbstractReportGenerator;
import com.tngtech.jgiven.report.model.ReportModel;
import com.tngtech.jgiven.report.model.ReportModelFile;
import com.tngtech.jgiven.report.model.ReportStatistics;
import java.io.File;
import java.io.PrintWriter;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AsciiDocReportGenerator extends AbstractReportGenerator {

    private static final Logger log = LoggerFactory.getLogger(AsciiDocReportGenerator.class);

    private final List<String> features = Lists.newArrayList();
    private final AsciiDocReportBlockConverter blockConverter;

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

        for (ReportModelFile reportModelFile : completeReportModel.getAllReportModels()) {
            writeFeatureToFile(reportModelFile.model, reportModelFile.file,
                completeReportModel.getStatistics(reportModelFile));
        }

        generateIndexFile();
    }

    private void writeFeatureToFile(final ReportModel model, final File file, final ReportStatistics statistics) {
        String featureFileName = Files.getNameWithoutExtension(file.getName()) + ".asciidoc";
        features.add(featureFileName);

        File targetFile = new File(config.getTargetDir(), featureFileName);
        try (PrintWriter printWriter = PrintWriterUtil.getPrintWriter(targetFile)) {
            AsciiDocReportModelVisitor visitor = new AsciiDocReportModelVisitor(blockConverter, statistics);
            model.accept(visitor);

            String fileContent = String.join("\n\n", visitor.getResult());

            printWriter.println(fileContent);
        }
    }

    private void generateIndexFile() {
        try (PrintWriter printWriter = PrintWriterUtil.getPrintWriter(
            new File(config.getTargetDir(), "index.asciidoc"))) {
            convertIndex(printWriter);
        }

        try (PrintWriter writer = PrintWriterUtil.getPrintWriter(
            new File(config.getTargetDir(), "totalStatistics.asciidoc"))) {
            generateStatistics(writer);
        }

        try (PrintWriter printWriter = PrintWriterUtil.getPrintWriter(
            new File(config.getTargetDir(), "allClasses.asciidoc"))) {
            generateIncludes(printWriter);
        }
    }

    private void generateStatistics(PrintWriter writer) {
        writer.println();
        writer.println(".Total Statistics");
        writer.println("[options=\"header,footer\"]");
        writer.println("|===");

        writer.print("| feature ");
        writer.print("| total classes ");
        writer.print("| successful scenarios ");
        writer.print("| failed scenarios ");
        writer.print("| pending scenarios ");
        writer.print("| total scenarios ");
        writer.print("| failed cases ");
        writer.print("| total cases ");
        writer.print("| total steps ");
        writer.println("| duration");


        completeReportModel.getAllReportModels().forEach(reportModelFile -> {
            ReportStatistics statistics = completeReportModel.getStatistics(reportModelFile);
            extracted(writer, reportModelFile.model.getName(), statistics);
        });

        extracted(writer, "sum", completeReportModel.getTotalStatistics());

        writer.println("|===");
    }

    private void generateIncludes(PrintWriter printWriter) {
        for (String fileName : features) {
            printWriter.println("include::" + fileName + "[]\n");
        }
    }

    private static void convertIndex(PrintWriter printWriter) {
        printWriter.println("= JGiven Documentation");
        printWriter.println(":toc: left");
        printWriter.println(":toclevels: 5");
        printWriter.println("");
        printWriter.println("include::totalStatistics.asciidoc[]");
        printWriter.println("");
        printWriter.println("== All Scenarios");
        printWriter.println("");

        printWriter.println("include::allClasses.asciidoc[]");
    }

    private static void extracted(PrintWriter writer, String name, ReportStatistics statistics) {
        writer.print("| " + name);
        writer.print(" | " + statistics.numClasses);
        writer.print(" | " + statistics.numSuccessfulScenarios);
        writer.print(" | " + statistics.numFailedScenarios);
        writer.print(" | " + statistics.numPendingScenarios);
        writer.print(" | " + statistics.numScenarios);
        writer.print(" | " + statistics.numFailedCases);
        writer.print(" | " + statistics.numCases);
        writer.print(" | " + statistics.numSteps);
        writer.println(" | " + statistics.durationInNanos);
    }
}
