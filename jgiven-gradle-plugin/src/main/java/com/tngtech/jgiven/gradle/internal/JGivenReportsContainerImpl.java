package com.tngtech.jgiven.gradle.internal;

import com.tngtech.jgiven.gradle.JGivenReport;
import com.tngtech.jgiven.gradle.JGivenReportsContainer;
import org.gradle.api.Task;
import org.gradle.api.reporting.internal.TaskReportContainer;

import javax.inject.Inject;

import static org.gradle.api.internal.CollectionCallbackActionDecorator.NOOP;

public class JGivenReportsContainerImpl extends TaskReportContainer<JGivenReport> implements JGivenReportsContainer {

    @Inject
    public JGivenReportsContainerImpl( Task task ) {
        super( JGivenReport.class, task, NOOP);
        add(JGivenHtmlReportImpl.class, task);
        add(JGivenTextReportImpl.class, task);
        add(JGivenAsciiDocReportImpl.class, task);
    }

    @Override public JGivenHtmlReportImpl getHtml() {
        return (JGivenHtmlReportImpl) getByName( JGivenHtmlReportImpl.NAME );
    }

    @Override public JGivenTextReportImpl getText() {
        return (JGivenTextReportImpl) getByName( JGivenTextReportImpl.NAME );
    }

    @Override
    public JGivenAsciiDocReportImpl getAsciiDoc() {
      return (JGivenAsciiDocReportImpl) getByName( JGivenAsciiDocReportImpl.NAME );
    }
}
