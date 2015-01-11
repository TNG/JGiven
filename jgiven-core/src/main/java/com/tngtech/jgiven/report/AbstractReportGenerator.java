package com.tngtech.jgiven.report;

import java.io.File;

import com.tngtech.jgiven.report.json.JsonModelTraverser;
import com.tngtech.jgiven.report.json.ReportModelFileHandler;

public abstract class AbstractReportGenerator implements ReportModelFileHandler {

    /**
     * The directory where the resulting report should be stored in.
     */
    protected File targetDirectory;

    public void generate( File sourceDir, File targetDirectory ) {
        this.targetDirectory = targetDirectory;
        new JsonModelTraverser().traverseModels( sourceDir, this );
        generationFinished( targetDirectory );
    }

    public abstract void generationFinished( File targetDirectory );
}
