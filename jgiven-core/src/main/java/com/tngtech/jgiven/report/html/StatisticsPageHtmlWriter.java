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
            htmlWriter.writeHtmlHeader( "Summary" );

            ReportModel reportModel = new ReportModel();
            reportModel.setClassName( ".Summary" );

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
        printWriter.write( "<div class='statistics-line'>" );
        writeStatisticNumber( statistics.numClasses, "classes" );
        writeStatisticNumber( statistics.numScenarios, "scenarios" );
        writeStatisticNumber( statistics.numCases, "cases" );
        writeStatisticNumber( statistics.numSteps, "steps" );

        String failedClass = statistics.numFailedCases > 0 ? "failed" : "";
        printWriter.write( "<div class='statistics-number " + failedClass + "'><a href='failed.html'><i>"
                + statistics.numFailedCases + "</i><br/><b>failed cases</b></a></div>" );

        writeStatisticNumber( DurationFormatter.format( statistics.durationInNanos ), "total time" );
        long averageNanos = statistics.numCases != 0 ? statistics.durationInNanos / statistics.numCases : 0;
        writeStatisticNumber( DurationFormatter.format( averageNanos ), "time / case" );
        printWriter.println( "</div>" );
    }

    private void writeStatisticNumber( int number, String name ) {
        writeStatisticNumber( number + "", name );
    }

    private void writeStatisticNumber( String number, String name ) {
        printWriter.write( "<div class='statistics-number'><i>" + number + "</i><br/><b>" + name + "</b></div>" );
    }
}
