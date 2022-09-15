package com.tngtech.jgiven.report.json;

import com.google.common.io.Files;

import java.io.File;
import java.util.stream.StreamSupport;

public class JsonModelTraverser {

    /**
     * Reads all JSON files from {@code sourceDirectory} and invokes for each found file
     * the {@link ReportModelFileHandler#handleReportModel} method of the given {@code handler}.
     *
     * @param sourceDirectory the directory that contains the JSON files
     * @param handler         the handler to be invoked for each file
     */
    public void traverseModels(File sourceDirectory, ReportModelFileHandler handler) {
        StreamSupport.stream(Files.fileTraverser().breadthFirst(sourceDirectory).spliterator(), false)
                .filter(input -> input.getName().endsWith(".json"))
                .map(new ReportModelFileReader())
                .forEach(handler::handleReportModel);
    }
}
