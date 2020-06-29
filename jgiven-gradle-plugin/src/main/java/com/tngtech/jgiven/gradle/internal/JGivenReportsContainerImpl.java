package com.tngtech.jgiven.gradle.internal;

import com.tngtech.jgiven.gradle.JGivenReport;
import com.tngtech.jgiven.gradle.JGivenReportsContainer;
import org.gradle.api.InvalidUserDataException;
import org.gradle.api.Task;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.reporting.Report;
import org.gradle.api.reporting.internal.TaskReportContainer;

import static org.gradle.api.internal.CollectionCallbackActionDecorator.NOOP;

public class JGivenReportsContainerImpl extends TaskReportContainer<JGivenReport> implements JGivenReportsContainer {

    public JGivenReportsContainerImpl( Task task ) {
        super( JGivenReport.class, task, NOOP);
        System.out.println("DEBUG:");
        System.out.println(task.toString());
        System.out.println("DEBUG: inst=" + this.getInstantiator().toString());
        JGivenReport report = this.getInstantiator().newInstance(JGivenHtmlReportImpl.class, task);
        System.out.println("DEBUG: created report");
        String name = report.getName();
        System.out.println("DEBUG: got report name");
        if (name.equals("enabled")) {
            throw new InvalidUserDataException("Reports that are part of a ReportContainer cannot be named 'enabled'");
        } else {
            System.out.println("DEBUG: evaluated name");
            this.getStore().add(report);
            this.index();
        }
        //add(JGivenHtmlReportImpl.class, task);
        System.out.println("END DEBUG:");
        add(JGivenTextReportImpl.class, task);
    }

    @Override public JGivenHtmlReportImpl getHtml() {
        return (JGivenHtmlReportImpl) getByName( JGivenHtmlReportImpl.NAME );
    }

    @Override public JGivenTextReportImpl getText() {
        return (JGivenTextReportImpl) getByName( JGivenTextReportImpl.NAME );
    }
}
