package com.tngtech.jgiven.gradle;

import com.google.common.collect.ImmutableMap;
import com.tngtech.jgiven.impl.Config;
import com.tngtech.jgiven.impl.util.WordUtil;
import org.gradle.api.Action;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.internal.IConventionAware;
import org.gradle.api.plugins.ReportingBasePlugin;
import org.gradle.api.reporting.ReportingExtension;
import org.gradle.api.tasks.testing.Test;

import java.io.File;
import java.util.Map;
import java.util.concurrent.Callable;

public class JGivenPlugin implements Plugin<Project> {
    @Override
    public void apply( final Project project ) {
        project.getPluginManager().apply( ReportingBasePlugin.class );

        addTaskExtension( project );
        addReports( project );
    }

    private void addTaskExtension( Project project ) {
        project.getTasks().withType( Test.class, new Action<Test>() {
            @Override
            public void execute( Test test ) {
                applyTo( test );
            }
        } );
    }

    private void applyTo( Test test ) {
        final String testName = test.getName();
        final JGivenTaskExtension extension = test.getExtensions().create( "jgiven", JGivenTaskExtension.class );
        final Project project = test.getProject();
        ( (IConventionAware) extension ).getConventionMapping().map( "resultsDir", new Callable<File>() {
            @Override
            public File call() {
                return project.file( String.valueOf( project.getBuildDir() ) + "/jgiven-results/" + testName );
            }
        } );

        test.getOutputs().namedFiles( new Callable<Map<?, ?>>() {
            @Override
            public Map<String, File> call() throws Exception {
                ImmutableMap.Builder<String, File> builder = ImmutableMap.builder();
                File resultsDir = extension.getResultsDir();
                if( resultsDir != null ) {
                    builder.put( "jgiven.resultsDir", resultsDir );
                }
                return builder.build();
            }
        } );

        test.prependParallelSafeAction( new Action<Task>() {
            @Override
            public void execute( Task task ) {
                Test test = (Test) task;
                test.systemProperty( Config.JGIVEN_REPORT_DIR, extension.getResultsDir().getAbsolutePath() );
            }
        } );
    }

    private void addReports( final Project project ) {
        final ReportingExtension reportingExtension = project.getExtensions().findByType( ReportingExtension.class );
        project.getTasks().withType( Test.class, new Action<Test>() {
            @Override
            public void execute( final Test test ) {
                final JGivenReport reportTask = project.getTasks()
                        .create( "jgiven" + WordUtil.capitalize( test.getName() ) + "Report", JGivenReport.class );
                ( (IConventionAware) reportTask ).getConventionMapping().map( "results", new Callable<File>() {
                    @Override
                    public File call() {
                        return test.getExtensions().getByType( JGivenTaskExtension.class ).getResultsDir();
                    }
                } );
                ( (IConventionAware) reportTask ).getConventionMapping().map( "destination", new Callable<File>() {
                    @Override
                    public File call() {
                        return reportingExtension.file( "jgiven/" + test.getName() );
                    }
                } );
            }
        } );
    }
}
