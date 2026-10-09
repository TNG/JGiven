package com.tngtech.jgiven.report.asciidoc;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AsciiDocReportConfigTest {

    @Test
    void defaultConfigHasSensibleDefaults() {
        // when
        final var reportConfig = new AsciiDocReportConfig();

        // then
        assertThat(reportConfig.getTitle()).isEqualTo("JGiven Report");
        assertThat(reportConfig.getSourceDir()).isNotNull().isDirectory();
        assertThat(reportConfig.getTargetDir()).isNotNull().isDirectory();
        assertThat(reportConfig.getExcludeEmptyScenarios()).isFalse();

        assertThat(reportConfig.getReportModel()).isNotNull();
    }

    @Test
    void badlyInitializedConfigHasSensibleDefaults() {
        // when
        final var reportConfig = new AsciiDocReportConfig("foobar=fizzbuzz");

        // then
        assertThat(reportConfig.getTitle()).isEqualTo("JGiven Report");
        assertThat(reportConfig.getSourceDir()).isNotNull().isDirectory();
        assertThat(reportConfig.getTargetDir()).isNotNull().isDirectory();
        assertThat(reportConfig.getExcludeEmptyScenarios()).isFalse();

        assertThat(reportConfig.getReportModel()).isNotNull();
    }

}
