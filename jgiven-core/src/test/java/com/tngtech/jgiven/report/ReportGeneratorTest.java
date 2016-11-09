package com.tngtech.jgiven.report;

import java.io.File;
import java.io.IOException;

import com.tngtech.jgiven.relocated.guava.base.Charsets;
import com.tngtech.jgiven.relocated.guava.io.Files;
import com.tngtech.jgiven.exception.JGivenWrongUsageException;
import org.assertj.core.api.Assertions;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class ReportGeneratorTest {

    @Rule
    public final TemporaryFolder tmpFolder = new TemporaryFolder();


    @Test(expected = JGivenWrongUsageException.class)
    public void wrong_json_files_are_handled_gracefully() throws Exception {
        File folder = tmpFolder.newFolder();

        Files.write("no json", new File(folder,"wrong.json"), Charsets.UTF_8);

        ReportGenerator generator = new ReportGenerator();
        generator.setSourceDirectory(folder);
        generator.setTargetDirectory(folder);
        generator.setFormat(ReportGenerator.Format.TEXT);

        generator.generate();
    }
}
