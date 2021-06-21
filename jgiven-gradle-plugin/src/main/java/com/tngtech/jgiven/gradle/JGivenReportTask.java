package com.tngtech.jgiven.gradle;

import com.tngtech.jgiven.gradle.internal.JGivenReportsContainerImpl;
import com.tngtech.jgiven.report.AbstractReportGenerator;
import groovy.lang.Closure;
import java.io.File;
import javax.inject.Inject;
import org.gradle.api.Action;
import org.gradle.api.DefaultTask;
import org.gradle.api.NonNullApi;
import org.gradle.api.reporting.Reporting;
import org.gradle.api.tasks.CacheableTask;
import org.gradle.api.tasks.InputDirectory;
import org.gradle.api.tasks.Nested;
import org.gradle.api.tasks.PathSensitive;
import org.gradle.api.tasks.PathSensitivity;
import org.gradle.api.tasks.SkipWhenEmpty;
import org.gradle.api.tasks.TaskAction;
import org.gradle.internal.reflect.Instantiator;

@CacheableTask
@NonNullApi
public class JGivenReportTask extends DefaultTask implements Reporting<JGivenReportsContainer> {
    private final JGivenReportsContainer reports;
    private File results;

    public JGivenReportTask() {
        reports = getInstantiator().newInstance(JGivenReportsContainerImpl.class, this);
    }

    @Inject
    protected Instantiator getInstantiator() {
        throw new UnsupportedOperationException();
    }

    @InputDirectory
    @SkipWhenEmpty
    @PathSensitive(PathSensitivity.NONE)
    public File getResults() {
        return results;
    }

    public void setResults(File results) {
        this.results = results;
    }

    @TaskAction
    public void generate() {
        getReports().stream()
            .filter(report -> report.getRequired().get())
            .forEach(this::generateReport);
    }

    private void generateReport(JGivenReport report) {
        AbstractReportGenerator generator = report.createGenerator();
        generator.config.setSourceDir(getResults());
        generator.loadReportModel();
        try {
            generator.generate();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Nested
    @Override
    public JGivenReportsContainer getReports() {
        return reports;
    }

    @Override
    public JGivenReportsContainer reports(Closure closure) {
        return (JGivenReportsContainer) reports.configure(closure);
    }

    @Override
    public JGivenReportsContainer reports(Action<? super JGivenReportsContainer> configureAction) {
        configureAction.execute(reports);
        return reports;
    }
}
