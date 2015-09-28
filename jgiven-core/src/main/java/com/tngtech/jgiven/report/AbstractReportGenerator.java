package com.tngtech.jgiven.report;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tngtech.jgiven.report.model.CompleteReportModel;

public abstract class AbstractReportGenerator {
    private static final Logger log = LoggerFactory.getLogger( AbstractReportGenerator.class );

    protected CompleteReportModel completeReportModel;
    protected File targetDirectory;
    protected ReportGenerator.Config config;

    public void generate( CompleteReportModel completeReportModel, File targetDirectory, ReportGenerator.Config config ) {
        this.completeReportModel = completeReportModel;
        this.targetDirectory = targetDirectory;
        this.config = config;
        generate();
    }

    public abstract void generate();
}
