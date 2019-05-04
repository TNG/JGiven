package com.tngtech.jgiven.gradle;

import com.google.common.util.concurrent.Callables;
import com.tngtech.jgiven.gradle.internal.JGivenHtmlReportImpl;
import com.tngtech.jgiven.impl.Config;
import com.tngtech.jgiven.impl.util.WordUtil;
import org.gradle.api.Action;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.internal.ConventionMapping;
import org.gradle.api.internal.IConventionAware;
import org.gradle.api.plugins.ReportingBasePlugin;
import org.gradle.api.reporting.Report;
import org.gradle.api.reporting.ReportingExtension;
import org.gradle.api.tasks.testing.Test;

import java.io.File;
import java.util.concurrent.Callable;

public class JGivenPlugin implements Plugin<Project> {
    @Override
    public void apply( final Project project ) {
        project.getPluginManager().apply( ReportingBasePlugin.class );

        addTaskExtension( project );
        addDefaultReports( project );
        configureJGivenReportDefaults( project );
    }

    private void addTaskExtension( Project project ) {
        project.getTasks().withType( Test.class, this::applyTo );
    }

    private void applyTo( Test test ) {
        final String testName = test.getName();
        final JGivenTaskExtension extension = test.getExtensions().create( "jgiven", JGivenTaskExtension.class );
        final Project project = test.getProject();
        ( (IConventionAware) extension ).getConventionMapping().map( "resultsDir",
                (Callable<File>) () -> project.file( project.getBuildDir() + "/jgiven-results/" + testName ) );

        File resultsDir = extension.getResultsDir();
        if( resultsDir != null ) {
            test.getOutputs().dir( resultsDir ).withPropertyName( "jgiven.resultsDir" );
        }

        test.prependParallelSafeAction( task ->
                ( (Test) task ).systemProperty( Config.JGIVEN_REPORT_DIR, extension.getResultsDir().getAbsolutePath() ) );
    }

    private void configureJGivenReportDefaults( Project project ) {
        project.getTasks().withType( JGivenReportTask.class,
                reportTask -> reportTask.getReports().all( (Action<Report>) report -> {
                    ConventionMapping mapping = ( (IConventionAware) report ).getConventionMapping();
                    mapping.map( "enabled", Callables.returning( report.getName().equals( JGivenHtmlReportImpl.NAME ) ) );
                } ) );
    }

    private void addDefaultReports( final Project project ) {
        final ReportingExtension reportingExtension = project.getExtensions().findByType( ReportingExtension.class );
        project.getTasks().withType( Test.class,
                test -> {
                    JGivenReportTask reportTask = project.getTasks()
                            .create( "jgiven" + WordUtil.capitalize( test.getName() ) + "Report", JGivenReportTask.class );
                    configureDefaultReportTask( test, reportTask, reportingExtension );
                } );
    }

    private void configureDefaultReportTask( final Test test, JGivenReportTask reportTask,
            final ReportingExtension reportingExtension ) {
        ConventionMapping mapping = ( (IConventionAware) reportTask ).getConventionMapping();
        mapping.map( "results",
                (Callable<File>) () -> test.getExtensions().getByType( JGivenTaskExtension.class ).getResultsDir() );
        mapping.getConventionValue( reportTask.getReports(), "reports", false ).all( new Action<Report>() {
            @Override
            public void execute( final Report report ) {
                ConventionMapping reportMapping = ( (IConventionAware) report ).getConventionMapping();
                reportMapping.map( "destination",
                        (Callable<File>) () -> reportingExtension.file( "jgiven" + "/" + test.getName() + "/" + report.getName() ) );
            }
        } );
    }
}
