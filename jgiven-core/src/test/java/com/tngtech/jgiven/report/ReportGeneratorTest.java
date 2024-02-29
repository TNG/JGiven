package com.tngtech.jgiven.report;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.tngtech.jgiven.exception.JGivenWrongUsageException;
import com.tngtech.jgiven.report.asciidoc.AsciiDocReportConfig;
import com.tngtech.jgiven.report.asciidoc.AsciiDocReportGenerator;
import org.assertj.core.api.Assertions;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class ReportGeneratorTest {

    @Rule
    public final TemporaryFolder tmpFolder = new TemporaryFolder();

    @Test( expected = JGivenWrongUsageException.class )
    public void wrong_json_files_are_handled_gracefully() throws Exception {
        File folder = tmpFolder.newFolder();

        Files.asCharSink( new File( folder, "wrong.json" ), Charsets.UTF_8 ).write( "no json");

        AsciiDocReportGenerator asciiReport = new AsciiDocReportGenerator();

        AsciiDocReportConfig config = new AsciiDocReportConfig();
        config.setSourceDir( tmpFolder.getRoot() );
        config.setTargetDir( tmpFolder.getRoot() );

        asciiReport.setConfig( config );
        asciiReport.loadReportModel();
        asciiReport.generate();
    }
}
