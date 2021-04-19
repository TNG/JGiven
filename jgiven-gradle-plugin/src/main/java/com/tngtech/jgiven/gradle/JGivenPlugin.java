package com.tngtech.jgiven.gradle;

import com.tngtech.jgiven.gradle.internal.JGivenHtmlReportImpl;
import com.tngtech.jgiven.impl.Config;
import com.tngtech.jgiven.impl.util.WordUtil;
import java.io.File;
import java.util.Objects;
import java.util.concurrent.Callable;
import org.gradle.api.Action;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.internal.ConventionMapping;
import org.gradle.api.internal.IConventionAware;
import org.gradle.api.plugins.ReportingBasePlugin;
import org.gradle.api.provider.Provider;
import org.gradle.api.reporting.Report;
import org.gradle.api.reporting.ReportingExtension;
import org.gradle.api.tasks.testing.Test;

public class JGivenPlugin implements Plugin<Project> {
    @Override
    public void apply( final Project project ){
        project.getPluginManager().apply( ReportingBasePlugin.class );

        addTaskExtension( project );
        addDefaultReports( project );
        configureJGivenReportDefaults( project );
    }

    private void addTaskExtension( Project project ){
        project.getTasks().withType( Test.class, this::applyTo );
    }

    private void applyTo( Test test ){
        final JGivenTaskExtension extension = test.getExtensions().create( "jgiven", JGivenTaskExtension.class );
        final Project project = test.getProject();
        final Provider<String> testName = project.provider(() ->test.getName());
        ( (IConventionAware) extension ).getConventionMapping().map( "resultsDir",
                (Callable<Provider<File>>) () ->  testName.map(name -> project.file( project.getBuildDir() + "/jgiven-results/" + name)));

        Provider<File> resultsDir = extension.getResultsDir();
        if( resultsDir != null ) {
            test.getOutputs().dir( resultsDir ).withPropertyName( "jgiven.resultsDir" );
        }

        // Java lambda classes are created at runtime with a non-deterministic classname.
        // Therefore, the class name does not identify the implementation of the lambda and changes between different Gradle runs.
        // https://docs.gradle.org/current/userguide/more_about_tasks.html#sec:how_does_it_work
        //noinspection Convert2Lambda
        test.prependParallelSafeAction( new Action<Task>() {
            @Override
            public void execute( Task task ){
                ((Test) task).systemProperty( Config.JGIVEN_REPORT_DIR, extension.getResultsDir().get().getAbsolutePath() );
            }
        } );
    }

    private void configureJGivenReportDefaults( Project project ){
        project.getTasks().withType( JGivenReportTask.class, reportTask -> reportTask.getReports().all( (Action<Report>) report -> {
            ConventionMapping mapping = ( (IConventionAware) report ).getConventionMapping();
            mapping.map( "enabled", (Callable<Boolean>) () -> report.getName().equals( JGivenHtmlReportImpl.NAME ) );
        } ) );
    }

    private void addDefaultReports( final Project project ){
        final ReportingExtension reportingExtension = project.getExtensions().findByType( ReportingExtension.class );
        project.getTasks().withType( Test.class, test -> {
            final JGivenReportTask reportTask = project.getTasks()
                    .create( "jgiven" + WordUtil.capitalize( test.getName() ) + "Report", JGivenReportTask.class );
            configureDefaultReportTask( test, reportTask, reportingExtension );
        } );
    }

    private void configureDefaultReportTask( final Test test, JGivenReportTask reportTask,
            final ReportingExtension reportingExtension ){
        ConventionMapping mapping = ( (IConventionAware) reportTask ).getConventionMapping();
        mapping.map( "results", (Callable<Provider<File>>) () -> test.getExtensions().getByType( JGivenTaskExtension.class ).getResultsDir() );
        Objects.requireNonNull(mapping.getConventionValue(reportTask.getReports(), "reports", false))
                .all( (Action<Report>) report -> {
                    ConventionMapping reportMapping = ( (IConventionAware) report ).getConventionMapping();
                    reportMapping.map( "destination",
                            (Callable<File>) () -> reportingExtension.file( "jgiven" + "/" + test.getName() + "/" + report.getName() ) );
                } );
    }
}
