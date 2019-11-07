package com.tngtech.jgiven.gradle.internal;

import com.tngtech.jgiven.gradle.JGivenReport;
import com.tngtech.jgiven.gradle.JGivenReportsContainer;
import org.gradle.api.Task;
import org.gradle.api.reporting.internal.TaskReportContainer;

import static org.gradle.api.internal.CollectionCallbackActionDecorator.NOOP;

public class JGivenReportsContainerImpl extends TaskReportContainer<JGivenReport> implements JGivenReportsContainer {

    public JGivenReportsContainerImpl( Task task ) {
        super( JGivenReport.class, task, NOOP);
        add(JGivenHtmlReportImpl.class, task);
        add(JGivenTextReportImpl.class, task);
    }

    @Override public JGivenHtmlReportImpl getHtml() {
        return (JGivenHtmlReportImpl) getByName( JGivenHtmlReportImpl.NAME );
    }

    @Override public JGivenTextReportImpl getText() {
        return (JGivenTextReportImpl) getByName( JGivenTextReportImpl.NAME );
    }
}
