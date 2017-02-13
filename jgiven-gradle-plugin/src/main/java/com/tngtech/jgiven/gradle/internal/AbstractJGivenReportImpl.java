package com.tngtech.jgiven.gradle.internal;

import com.tngtech.jgiven.gradle.JGivenReport;
import com.tngtech.jgiven.report.ReportGenerator;
import org.gradle.api.Task;
import org.gradle.api.reporting.internal.TaskGeneratedSingleDirectoryReport;

import java.io.File;

public abstract class AbstractJGivenReportImpl extends TaskGeneratedSingleDirectoryReport implements JGivenReport {
    private File customCssFile;
    private File customJsFile;
    private String title;
    private boolean excludeEmptyScenarios;

    public AbstractJGivenReportImpl( String name, Task task, String relativeEntryPath ) {
        super( name, task, relativeEntryPath );
    }

    @Override public void configure( ReportGenerator generator ) {
        generator.addFlag( "--targetDir=" + getDestination() );
        generator.addFlag( "--format=" + getFormat() );
        generator.addFlag( "--customcss=" + getCustomCssFile() );
        generator.addFlag( "--customjs=" + getCustomJsFile() );
        generator.addFlag( "--exclude-empty-scenarios=" + isExcludeEmptyScenarios() );
        if( getTitle() != null ) {
            generator.addFlag( "--title=" + getTitle() );
        }
    }

    public abstract ReportGenerator.Format getFormat();

    @Override public File getCustomCssFile() {
        return customCssFile;
    }

    @Override public void setCustomCssFile( File customCssFile ) {
        this.customCssFile = customCssFile;
    }

    @Override public File getCustomJsFile() {
        return customJsFile;
    }

    @Override public void setCustomJsFile( File customJsFile ) {
        this.customJsFile = customJsFile;
    }

    @Override public String getTitle() {
        return title;
    }

    @Override public void setTitle( String title ) {
        this.title = title;
    }

    @Override public boolean isExcludeEmptyScenarios() {
        return excludeEmptyScenarios;
    }

    @Override public void setExcludeEmptyScenarios( boolean excludeEmptyScenarios ) {
        this.excludeEmptyScenarios = excludeEmptyScenarios;
    }
}

