package com.tngtech.jgiven.gradle;

import org.gradle.api.reporting.ReportContainer;
import org.gradle.api.tasks.Nested;

public interface JGivenReportsContainer extends ReportContainer<JGivenReport> {
    @Nested
    JGivenReport getHtml();
    @Nested
    JGivenReport getText();
    @Nested
    JGivenReport getAsciiDoc();
}
