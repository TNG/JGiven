package com.tngtech.jgiven.report;

import java.io.File;

import org.assertj.core.api.Assertions;
import org.junit.Test;

import com.tngtech.jgiven.report.ReportGenerator;

public class ReportGeneratorArgumentTest {
    @Test
    public void testArgumentParsing() {
        ReportGenerator generator = new ReportGenerator();
        ReportGenerator.parseArgs( generator,
            new String[] {
                "--dir=source/dir",
                "--todir=target/dir",
                "--customcss=my/custom/file.css" }
            );
        Assertions.assertThat( generator.getSourceDir() ).isEqualTo( new File( "source/dir" ) );
        Assertions.assertThat( generator.getToDir() ).isEqualTo( new File( "target/dir" ) );
        Assertions.assertThat( generator.getCustomCssFile() ).isEqualTo( new File( "my/custom/file.css" ) );
    }
}
