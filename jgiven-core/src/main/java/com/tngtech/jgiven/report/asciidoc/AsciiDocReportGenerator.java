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

public class AsciiDocReportGenerator extends AbstractReportGenerator {

    private final List<String> allFiles = Lists.newArrayList();

    public AbstractReportConfig createReportConfig(String... args) {
        return new AsciiDocReportConfig(args);
    }

    public void generate() {
        for (ReportModelFile reportModelFile : completeReportModel.getAllReportModels()) {
            writeReportModelToFile(reportModelFile.model, reportModelFile.file);
        }
        generateIndexFile();
    }

    private void writeReportModelToFile(ReportModel model, File file) {
        String targetFileName = Files.getNameWithoutExtension(file.getName()) + ".asciidoc";

        allFiles.add(targetFileName);
        if (!config.getTargetDir().exists()) {
            config.getTargetDir().mkdirs();
        }
        File targetFile = new File(config.getTargetDir(), targetFileName);
        PrintWriter printWriter = PrintWriterUtil.getPrintWriter(targetFile);


        try {
            AsciiDocWriter visitor = new AsciiDocWriter(new AsciiDocReportModelHandler(printWriter));
            model.accept(visitor);
        } finally {
            ResourceUtil.close(printWriter);
        }
    }

    private void generateIndexFile() {
        PrintWriter printWriter = PrintWriterUtil.getPrintWriter(new File(config.getTargetDir(), "index.asciidoc"));
        try {
            printWriter.println("= JGiven Documentation");
            printWriter.println(":toc: left");
            printWriter.println(":toclevels: 5");
            printWriter.println("");
            printWriter.println("include::totalStatistics.asciidoc[]");
            printWriter.println("");
            printWriter.println("== All Scenarios");
            printWriter.println("");

            printWriter.println("include::allClasses.asciidoc[]");
        } finally {
            ResourceUtil.close(printWriter);
        }

        writeTotalStatistics();

        printWriter = PrintWriterUtil.getPrintWriter(new File(config.getTargetDir(), "allClasses.asciidoc"));
        try {
            for (String fileName : allFiles) {
                printWriter.println("include::" + fileName + "[]\n");
            }
        } finally {
            ResourceUtil.close(printWriter);
        }
    }

    private void writeTotalStatistics() {
        try (PrintWriter writer = PrintWriterUtil.getPrintWriter(new File(config.getTargetDir(), "totalStatistics.asciidoc"))) {

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
