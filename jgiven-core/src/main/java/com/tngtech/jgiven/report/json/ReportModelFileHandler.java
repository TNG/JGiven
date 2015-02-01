package com.tngtech.jgiven.report.json;

import java.io.File;

import com.tngtech.jgiven.report.model.ReportModel;
import com.tngtech.jgiven.report.model.ReportModelFile;

/**
 * Handles a single ReportModel that has been read from a single JSON file.
 * Use the {@link JsonModelTraverser} to read all files.
 */
public interface ReportModelFileHandler {

    void handleReportModel( ReportModelFile modelFile );

}
