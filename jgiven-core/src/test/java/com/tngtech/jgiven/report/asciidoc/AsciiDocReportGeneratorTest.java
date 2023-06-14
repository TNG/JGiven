package com.tngtech.jgiven.report.asciidoc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.tngtech.jgiven.report.AbstractReportConfig;
import java.io.IOException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class AsciiDocReportGeneratorTest {
    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    private final AsciiDocReportGenerator reportGenerator = new AsciiDocReportGenerator();

    @Test
    public void createReportConfig() {
        // when
        final AsciiDocReportConfig reportConfig = reportGenerator.createReportConfig();

        // then
        assertThat(reportConfig.getTitle()).isEqualTo("JGiven Report");
        assertThat(reportConfig.getSourceDir()).isNotNull().isDirectory();
        assertThat(reportConfig.getTargetDir()).isNotNull().isDirectory();
        assertThat(reportConfig.getExcludeEmptyScenarios()).isFalse();

        assertThat(reportConfig.getReportModel()).isNotNull();
    }

    @Test
    public void generatingReportWithoutConfigWillFail() {
        // when
        assertThatThrownBy(reportGenerator::generate)
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("AsciiDocReporter must be configured");
    }

    @Test
    public void generatingReportWithImplicitConfig() throws IOException {
        // given
        final AbstractReportConfig config = new AsciiDocReportConfig();
        config.setTargetDir(temporaryFolder.newFolder());

        reportGenerator.setConfig(config);

        // when
        assertThatNoException().isThrownBy(reportGenerator::generate);
    }

    @Test
    public void generatingReportWithExplicitConfig() throws IOException {
        // given
        final AbstractReportConfig config = new AsciiDocReportConfig();
        config.setTargetDir(temporaryFolder.newFolder());

        // when
        assertThatNoException().isThrownBy(() -> reportGenerator.generateWithConfig(config));
    }
}
