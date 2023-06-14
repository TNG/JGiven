package com.tngtech.jgiven.report.asciidoc;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

public class AsciiDocReportConfigTest {

    @Test
    public void defaultConfigHasSensibleDefaults() {
        // when
        final AsciiDocReportConfig reportConfig = new AsciiDocReportConfig();

        // then
        assertThat(reportConfig.getTitle()).isEqualTo("JGiven Report");
        assertThat(reportConfig.getSourceDir()).isNotNull().isDirectory();
        assertThat(reportConfig.getTargetDir()).isNotNull().isDirectory();
        assertThat(reportConfig.getExcludeEmptyScenarios()).isFalse();

        assertThat(reportConfig.getReportModel()).isNotNull();
    }

    @Test
    public void badlyInitializedConfigHasSensibleDefaults() {
        // when
        final AsciiDocReportConfig reportConfig = new AsciiDocReportConfig("foobar=fizzbuzz");

        // then
        assertThat(reportConfig.getTitle()).isEqualTo("JGiven Report");
        assertThat(reportConfig.getSourceDir()).isNotNull().isDirectory();
        assertThat(reportConfig.getTargetDir()).isNotNull().isDirectory();
        assertThat(reportConfig.getExcludeEmptyScenarios()).isFalse();

        assertThat(reportConfig.getReportModel()).isNotNull();
    }

}
