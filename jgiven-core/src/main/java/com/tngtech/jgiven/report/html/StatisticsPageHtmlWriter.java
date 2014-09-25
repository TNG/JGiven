package com.tngtech.jgiven.report.html;

import java.io.File;
import java.io.PrintWriter;

import com.tngtech.jgiven.impl.util.DurationFormatter;
import com.tngtech.jgiven.impl.util.ResourceUtil;
import com.tngtech.jgiven.report.impl.CommonReportHelper;
import com.tngtech.jgiven.report.model.ReportModel;
import com.tngtech.jgiven.report.model.ReportStatistics;

public class StatisticsPageHtmlWriter {

    private final HtmlTocWriter tocWriter;
    private final ReportStatistics statistics;
    private PrintWriter printWriter;
    private HtmlWriterUtils utils;

    public StatisticsPageHtmlWriter( HtmlTocWriter tocWriter, ReportStatistics statistics ) {
        this.tocWriter = tocWriter;
        this.statistics = statistics;
    }

    public void write( File toDir ) {
        writeIndexFile( toDir );
    }

    private void writeIndexFile( File toDir ) {
        File file = new File( toDir, "index.html" );
        printWriter = CommonReportHelper.getPrintWriter( file );
        utils = new HtmlWriterUtils( printWriter );
        try {
            ReportModelHtmlWriter htmlWriter = new ReportModelHtmlWriter( printWriter );
            htmlWriter.writeHtmlHeader( "Scenarios" );

            ReportModel reportModel = new ReportModel();
            reportModel.setClassName( ".Scenarios" );

            tocWriter.writeToc( printWriter );
            htmlWriter.visit( reportModel );

            writeStatistics();

            htmlWriter.visitEnd( reportModel );
            htmlWriter.writeHtmlFooter();
        } finally {
            ResourceUtil.close( printWriter );
        }
    }

    private void writeStatistics() {
        printWriter.write( "Number of Classes: " + statistics.numClasses + "<br>" );
        printWriter.write( "Number of Scenarios: " + statistics.numScenarios + "<br>" );
        printWriter.write( "Number of Cases: " + statistics.numCases + "<br>" );
        printWriter.write( "Number of Failed Cases: " + statistics.numFailedCases + "<br>" );
        printWriter.write( "Number of Steps: " + statistics.numSteps + "<br>" );
        printWriter.write( "Total Time: " + DurationFormatter.format( statistics.durationInNanos ) + "<br>" );

        long averageNanos = statistics.numCases != 0 ? statistics.durationInNanos / statistics.numCases : 0;
        printWriter.write( "Average duration per Case: " + DurationFormatter.format( averageNanos ) );
    }
}
