package com.tngtech.jgiven.gradle.internal;

import com.tngtech.jgiven.gradle.JGivenReport;
import com.tngtech.jgiven.report.ReportGenerator;
import org.gradle.api.Task;

public abstract class JGivenTextReportImpl extends AbstractJGivenReportImpl implements JGivenReport {
    static final String NAME = "text";

    public JGivenTextReportImpl( Task task ) {
        super( NAME, task, null );
    }

    @Override public ReportGenerator.Format getFormat() {
        return ReportGenerator.Format.TEXT;
    }
}
