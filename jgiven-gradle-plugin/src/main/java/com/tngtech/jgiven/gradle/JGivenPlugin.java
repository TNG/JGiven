package com.tngtech.jgiven.gradle;

import com.tngtech.jgiven.gradle.internal.JGivenHtmlReportImpl;
import com.tngtech.jgiven.impl.Config;
import com.tngtech.jgiven.impl.util.WordUtil;
import org.gradle.api.*;
import org.gradle.api.file.Directory;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.plugins.ReportingBasePlugin;
import org.gradle.api.provider.Provider;
import org.gradle.api.reporting.Report;
import org.gradle.api.reporting.ReportingExtension;
import org.gradle.api.tasks.testing.Test;

import javax.inject.Inject;
import java.util.Objects;

@SuppressWarnings("unused")
@NonNullApi
public abstract class JGivenPlugin implements Plugin<Project> {

    @Inject
    protected abstract ObjectFactory getObjects();

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
        final Project project = test.getProject();
        final JGivenTaskExtension extension = getObjects().newInstance(JGivenTaskExtension.class);
        test.getExtensions().add("jgiven", extension);
        extension.getResultsDir().convention(project.getLayout().getBuildDirectory().dir("jgiven-results/" + testName));
        Provider<Directory> resultsDir = extension.getResultsDir();
        test.getOutputs().dir(resultsDir).withPropertyName("jgiven.resultsDir");

        /* Java lambda classes are created at runtime with a non-deterministic classname.
         * Therefore, the class name does not identify the implementation of the lambda,
         * and changes between different Gradle runs.
         * See: https://docs.gradle.org/current/userguide/more_about_tasks.html#sec:how_does_it_work
         */
        //noinspection Convert2Lambda
        test.doFirst(new Action<>() {
            @Override
            public void execute(Task task) {
                ((Test) task).systemProperty(Config.JGIVEN_REPORT_DIR, extension.getResultsDir().get().getAsFile().getAbsolutePath());
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

        Provider<Directory> getResultsDirectory = test.getExtensions()
                .getByType(JGivenTaskExtension.class)
                .getResultsDir();

        reportTask.getResults().convention(getResultsDirectory); //this line somehow forces the test task to run.

        reportTask.getReports().configureEach(report -> {
            String relativeFilePath = "jgiven" + "/" + test.getName() + "/" + report.getName();
            report.getOutputLocation().convention(reportingExtension.getBaseDirectory().dir(relativeFilePath));
        });
    }
}
