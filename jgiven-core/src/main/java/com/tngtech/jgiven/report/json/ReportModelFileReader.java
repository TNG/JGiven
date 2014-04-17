package com.tngtech.jgiven.report.json;

import java.io.File;

import com.google.common.base.Function;
import com.tngtech.jgiven.report.model.ReportModelFile;

public class ReportModelFileReader implements Function<File, ReportModelFile> {
    @Override
    public ReportModelFile apply( File file ) {
        ReportModelFile result = new ReportModelFile();
        result.file = file;
        result.model = new ScenarioJsonReader().apply( file );
        return result;
    }
}