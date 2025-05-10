package com.tngtech.jgiven.report.text;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

public class PlainTextReportGeneratorTest {
    @Rule
    public final TemporaryFolder tmpFolder = new TemporaryFolder();

    @Test
    public void can_create_an_output_folder() throws IOException {
        var outputDir = Path.of(tmpFolder.getRoot().getAbsolutePath(), "report", "jgiven", "text").toFile();
        var sourceFile = getClass().getClassLoader()
                .getResource("com.tngtech.jgiven.report.text.ResourceForPlainTextReportGeneratorTest.json")
                .getFile();
        var outputConfig = "--targetDir=" + outputDir.getAbsolutePath();
        var sourceConfig = "--sourceDir=" + new File(sourceFile).getParent();
        var underTest = new PlainTextReportGenerator();

        underTest.generateWithConfig(underTest.createReportConfig(sourceConfig, outputConfig));


        assertThat(outputDir).exists();
        assertThat(outputDir).isDirectory();
        assertThat(outputDir.list())
                .contains("com.tngtech.jgiven.report.text.ResourceForPlainTextReportGeneratorTest.feature");
    }
}
