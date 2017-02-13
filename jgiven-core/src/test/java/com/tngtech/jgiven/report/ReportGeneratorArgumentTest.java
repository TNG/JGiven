package com.tngtech.jgiven.report;

import java.io.File;

import org.assertj.core.api.Assertions;
import org.junit.Test;

public class ReportGeneratorArgumentTest {
    @Test
    public void testArgumentParsing() {

        ReportGenerator generator = new ReportGenerator();
        generator.addFlag( "--sourceDir=source/dir" );
        generator.addFlag( "--targetDir=target/dir" );

        AbstractReport report = generator.createInternalReport( ReportGenerator.Format.TEXT );

        Assertions.assertThat( report.getSourceDir() ).isEqualTo( new File( "source/dir" ) );
        Assertions.assertThat( report.getTargetDir() ).isEqualTo( new File( "target/dir" ) );
    }
}
