package com.tngtech.jgiven.report;

import com.tngtech.jgiven.report.config.ConfigOption;
import java.io.File;
import java.util.List;
import java.util.Map;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ReportConfigArgumentTest {

    @Test
    public void testArgumentParsing() {
        var testConfig = new AbstractReportConfig("--sourceDir=source/dir", "--targetDir=target/dir") {

            @Override
            public void useConfigMap(Map<String, Object> configMap) {
            }

            @Override
            public void additionalConfigOptions(List<ConfigOption> configOptions) {
            }
        };

        assertThat(testConfig.getSourceDir()).isEqualTo(new File("source/dir"));
        assertThat(testConfig.getTargetDir()).isEqualTo(new File("target/dir"));
    }
}
