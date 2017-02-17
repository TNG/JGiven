package com.tngtech.jgiven.report;

import java.io.File;

import com.tngtech.jgiven.report.asciidoc.AsciiDocReportConfig;
import com.tngtech.jgiven.report.asciidoc.AsciiDocReportGenerator;
import org.assertj.core.api.Assertions;
import org.junit.Test;

public class ReportGeneratorArgumentTest {

    @Test
    public void testArgumentParsing() {

        AsciiDocReportGenerator asciiReport = new AsciiDocReportGenerator();

        asciiReport.parseToConfig( "--sourceDir=source/dir", "--targetDir=target/dir" );

        Assertions.assertThat( asciiReport.config.getSourceDir() ).isEqualTo( new File( "source/dir" ) );
        Assertions.assertThat( asciiReport.config.getTargetDir() ).isEqualTo( new File( "target/dir" ) );
    }
}
