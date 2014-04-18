package com.tngtech.jgiven.report.html;

import java.io.File;

import org.assertj.core.api.Assertions;
import org.junit.Test;

public class HtmlReportGeneratorArgumentTest {
    @Test
    public void testArgumentParsing() {
        HtmlReportGenerator generator = new HtmlReportGenerator();
        HtmlReportGenerator.parseArgs( generator,
            new String[] {
                "--dir=source/dir",
                "--todir=target/dir",
                "--frames",
                "--customcss=my/custom/file.css" }
            );
        Assertions.assertThat( generator.sourceDir ).isEqualTo( new File( "source/dir" ) );
        Assertions.assertThat( generator.toDir ).isEqualTo( new File( "target/dir" ) );
        Assertions.assertThat( generator.customCssFile ).isEqualTo( new File( "my/custom/file.css" ) );
        Assertions.assertThat( generator.frames ).isEqualTo( true );
    }
}
