package com.tngtech.jgiven.report.asciidoc;

import com.tngtech.jgiven.report.AbstractReportConfig;
import java.io.File;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AsciiDocReportGeneratorTest {

    private final AsciiDocReportGenerator reportGenerator = new AsciiDocReportGenerator();

    @Test
    void createReportConfig() {
        // when
        final var reportConfig = reportGenerator.createReportConfig();

        // then
        assertThat(reportConfig.getTitle()).isEqualTo("JGiven Report");
        assertThat(reportConfig.getSourceDir()).isNotNull().isDirectory();
        assertThat(reportConfig.getTargetDir()).isNotNull().isDirectory();
        assertThat(reportConfig.getExcludeEmptyScenarios()).isFalse();

        assertThat(reportConfig.getReportModel()).isNotNull();
    }

    @Test
    void generatingReportWithoutConfigWillFail() {
        // when
        assertThatThrownBy(reportGenerator::generate)
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("AsciiDocReporter must be configured");
    }

    @Test
    void generatingReportWithImplicitConfig(@TempDir File temporaryFolder) {
        // given
        final AbstractReportConfig config = new AsciiDocReportConfig();
        config.setTargetDir(temporaryFolder);

        reportGenerator.setConfig(config);

        // when
        assertThatNoException().isThrownBy(reportGenerator::generate);
    }

    @Test
    void generatingReportWithExplicitConfig(@TempDir File temporaryFolder) {
        // given
        final AbstractReportConfig config = new AsciiDocReportConfig();
        config.setTargetDir(temporaryFolder);

        // when
        assertThatNoException().isThrownBy(() -> reportGenerator.generateWithConfig(config));
    }


    @Test
    void reporterHandlesBadConfigGracefully() {
        // given
        final AbstractReportConfig config = new AsciiDocReportConfig();
        config.setSourceDir(null);
        config.setTargetDir(null);

        reportGenerator.setConfig(config);

        // when
        assertThatNoException().isThrownBy(reportGenerator::generate);
    }
}
