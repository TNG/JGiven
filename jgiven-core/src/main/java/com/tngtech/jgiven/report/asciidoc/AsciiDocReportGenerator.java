package com.tngtech.jgiven.report.asciidoc;

import java.io.File;
import java.io.PrintWriter;
import java.util.List;

import com.google.common.collect.Lists;
import com.google.common.io.Files;
import com.tngtech.jgiven.impl.util.PrintWriterUtil;
import com.tngtech.jgiven.impl.util.ResourceUtil;
import com.tngtech.jgiven.report.*;
import com.tngtech.jgiven.report.model.ReportModel;
import com.tngtech.jgiven.report.model.ReportModelFile;
import com.tngtech.jgiven.report.model.ReportStatistics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AsciiDocReportGenerator extends AbstractReportGenerator {

    private static final Logger log = LoggerFactory.getLogger(AsciiDocReportGenerator.class);

    private final List<String> features = Lists.newArrayList();

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
            writeReportModelToFile(
                    reportModelFile.model, reportModelFile.file, completeReportModel.getStatistics(reportModelFile));
        }

        generateIndexFile();
    }

    private void writeReportModelToFile(ReportModel model, File file, ReportStatistics statistics) {
        String featureFileName = Files.getNameWithoutExtension(file.getName()) + ".asciidoc";

        features.add(featureFileName);
        File targetFile = new File(config.getTargetDir(), featureFileName);
        PrintWriter printWriter = PrintWriterUtil.getPrintWriter(targetFile);

        try {
            AsciiDocReportBlockConverter blockConverter = new AsciiDocReportBlockConverter(printWriter);
            AsciiDocReportModelVisitor visitor = new AsciiDocReportModelVisitor(blockConverter, statistics);
            model.accept(visitor);
        } finally {
            ResourceUtil.close(printWriter);
        }
    }

    private void generateIndexFile() {
        try (PrintWriter printWriter = PrintWriterUtil.getPrintWriter(new File(config.getTargetDir(), "index.asciidoc"))) {
            convertIndex(printWriter);
        }

        try (PrintWriter writer = PrintWriterUtil.getPrintWriter(new File(config.getTargetDir(), "totalStatistics.asciidoc"))) {
            generateStatistics(writer);
        }

        try (PrintWriter printWriter = PrintWriterUtil.getPrintWriter(new File(config.getTargetDir(), "allClasses.asciidoc"))) {
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
