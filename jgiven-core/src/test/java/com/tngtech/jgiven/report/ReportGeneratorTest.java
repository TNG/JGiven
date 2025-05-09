package com.tngtech.jgiven.report;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.tngtech.jgiven.exception.JGivenWrongUsageException;
import com.tngtech.jgiven.report.config.ConfigOption;
import java.io.File;
import java.util.List;
import java.util.Map;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class ReportGeneratorTest {

    @Rule
    public final TemporaryFolder tmpFolder = new TemporaryFolder();

    @Test( expected = JGivenWrongUsageException.class )
    public void wrong_json_files_are_handled_gracefully() throws Exception {
        var folder = tmpFolder.newFolder();

        Files.asCharSink( new File( folder, "wrong.json" ), Charsets.UTF_8 ).write( "no json");

        var reportGenerator = new AbstractReportGenerator() {

            @Override
            public void generate() throws Exception {
            }

            @Override
            public AbstractReportConfig createReportConfig(String... args) {
                return null;
            }
        };

        var config = new AbstractReportConfig() {

            @Override
            public void useConfigMap(Map<String, Object> configMap) {
            }

            @Override
            public void additionalConfigOptions(List<ConfigOption> configOptions) {
            }
        };
        config.setSourceDir( tmpFolder.getRoot() );
        config.setTargetDir( tmpFolder.getRoot() );

        reportGenerator.setConfig(config);
        reportGenerator.loadReportModel();
    }
}
