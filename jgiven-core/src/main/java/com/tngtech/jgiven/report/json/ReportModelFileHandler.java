package com.tngtech.jgiven.report.json;

import java.io.File;

import com.tngtech.jgiven.report.model.ReportModel;

public interface ReportModelFileHandler {

    void handleReportModel( ReportModel model, File file );

}
