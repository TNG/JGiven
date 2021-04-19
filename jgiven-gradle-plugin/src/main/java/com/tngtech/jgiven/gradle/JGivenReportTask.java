package com.tngtech.jgiven.gradle;

import com.tngtech.jgiven.gradle.internal.JGivenReportsContainerImpl;
import com.tngtech.jgiven.report.AbstractReportGenerator;
import groovy.lang.Closure;
import java.io.File;
import javax.inject.Inject;
import org.gradle.api.Action;
import org.gradle.api.DefaultTask;
import org.gradle.api.provider.Provider;
import org.gradle.api.reporting.Reporting;
import org.gradle.api.tasks.CacheableTask;
import org.gradle.api.tasks.InputDirectory;
import org.gradle.api.tasks.Nested;
import org.gradle.api.tasks.PathSensitive;
import org.gradle.api.tasks.PathSensitivity;
import org.gradle.api.tasks.SkipWhenEmpty;
import org.gradle.api.tasks.TaskAction;
import org.gradle.internal.reflect.Instantiator;
import org.gradle.util.ConfigureUtil;

@CacheableTask
public class JGivenReportTask extends DefaultTask implements Reporting<JGivenReportsContainer> {
    private final JGivenReportsContainer reports;
    private Provider<File> results;

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
    public Provider<File> getResults() {
        return results;
    }

    public void setResults(Provider<File> results) {
        this.results = results;
    }

    @TaskAction
    public void generate() throws Exception {
        for (JGivenReport report : getReports().getEnabled()) {
            AbstractReportGenerator generator = report.createGenerator();
            generator.config.setSourceDir(getResults().get());
            generator.loadReportModel();
            generator.generate();
        }
    }

    @Nested
    @Override
    public JGivenReportsContainer getReports() {
        return reports;
    }

    @Override
    public JGivenReportsContainer reports(Closure closure) {
        return reports(ConfigureUtil.configureUsing(closure));
    }

    @Override
    public JGivenReportsContainer reports(Action<? super JGivenReportsContainer> configureAction) {
        configureAction.execute(reports);
        return reports;
    }
}
