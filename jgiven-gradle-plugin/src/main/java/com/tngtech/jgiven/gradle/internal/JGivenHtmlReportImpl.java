package com.tngtech.jgiven.gradle.internal;

import com.tngtech.jgiven.gradle.JGivenReport;
import com.tngtech.jgiven.report.ReportGenerator;
import org.gradle.api.Task;

public class JGivenHtmlReportImpl extends AbstractJGivenReportImpl implements JGivenReport {
    public static final String NAME = "html";

    public JGivenHtmlReportImpl( Task task ) {
        super( NAME, task, "index.html" );
    }

    @Override public ReportGenerator.Format getFormat() {
        return ReportGenerator.Format.HTML;
    }
}
