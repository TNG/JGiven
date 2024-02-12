package com.tngtech.jgiven.gradle;

import com.tngtech.jgiven.gradle.internal.JGivenHtmlReportImpl;
import com.tngtech.jgiven.impl.Config;
import com.tngtech.jgiven.impl.util.WordUtil;
import org.gradle.api.*;
import org.gradle.api.internal.ConventionMapping;
import org.gradle.api.internal.IConventionAware;
import org.gradle.api.plugins.ReportingBasePlugin;
import org.gradle.api.reporting.Report;
import org.gradle.api.reporting.ReportingExtension;
import org.gradle.api.tasks.testing.Test;

import java.io.File;
import java.util.Objects;
import java.util.concurrent.Callable;

@NonNullApi
public class JGivenPlugin implements Plugin<Project> {
    @Override
    public void apply(final Project project) {
        project.getPluginManager().apply(ReportingBasePlugin.class);

        addTaskExtension(project);
        addDefaultReports(project);
        configureJGivenReportDefaults(project);
    }

    private void addTaskExtension(Project project) {
        project.getTasks().withType(Test.class).configureEach(this::applyTo);
    }

    private void applyTo(Test test) {
        final String testName = test.getName();
        final JGivenTaskExtension extension = test.getExtensions().create("jgiven", JGivenTaskExtension.class);
        final Project project = test.getProject();
        ((IConventionAware) extension).getConventionMapping().map("resultsDir",
            (Callable<File>) () -> project.file(project.getBuildDir() + "/jgiven-results/" + testName));

        File resultsDir = extension.getResultsDir().get();
        test.getOutputs().dir(resultsDir).withPropertyName("jgiven.resultsDir");

        /* Java lambda classes are created at runtime with a non-deterministic classname.
         * Therefore, the class name does not identify the implementation of the lambda,
         * and changes between different Gradle runs.
         * See: https://docs.gradle.org/current/userguide/more_about_tasks.html#sec:how_does_it_work
         */
        //noinspection Convert2Lambda
        test.prependParallelSafeAction(new Action<>() {
            @Override
            public void execute(Task task) {
                ((Test) task).systemProperty(Config.JGIVEN_REPORT_DIR, extension.getResultsDir().get().getAbsolutePath());
            }
        });
    }

    private void configureJGivenReportDefaults(Project project) {
        project.getTasks()
            .withType(JGivenReportTask.class).forEach(reportTask ->
            reportTask.getReports().all((Action<Report>) report ->
                report.getRequired().convention(report.getName().equals(JGivenHtmlReportImpl.NAME))
            ));
    }

    private void addDefaultReports(final Project project) {
        final ReportingExtension reportingExtension = Objects.requireNonNull(
            project.getExtensions().findByType(ReportingExtension.class));

        project.getTasks().withType(Test.class).forEach(test -> project.getTasks()
                .register("jgiven" + WordUtil.capitalize(test.getName()) + "Report", JGivenReportTask.class)
                .configure(reportTask -> configureDefaultReportTask(test, reportTask, reportingExtension))
        );
    }

    private void configureDefaultReportTask(final Test test, JGivenReportTask reportTask,
                                            final ReportingExtension reportingExtension) {
        reportTask.mustRunAfter(test);

        ConventionMapping mapping = ((IConventionAware) reportTask).getConventionMapping();
        Callable<File> getResultsDirectory = () -> test.getExtensions()
            .getByType(JGivenTaskExtension.class)
            .getResultsDir().get();
        mapping.map("results", getResultsDirectory);

        Objects.requireNonNull(mapping.getConventionValue(reportTask.getReports(), "reports", false))
            .all(report -> {
                ConventionMapping reportMapping = ((IConventionAware) report).getConventionMapping();
                String relativeFilePath = "jgiven" + "/" + test.getName() + "/" + report.getName();
                Callable<File> getDestination = () -> reportingExtension.file(relativeFilePath);
                reportMapping.map("destination", getDestination);
            });
    }
}
