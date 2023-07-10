package com.tngtech.jgiven.report.model;

import java.io.File;

public class ReportModelFile {
    private final File file;
    private final ReportModel model;

    public ReportModelFile(final File file, final ReportModel model) {
        this.file = file;
        this.model = model;
    }

    public File file() {
        return file;
    }

    public ReportModel model() {
        return model;
    }
}
