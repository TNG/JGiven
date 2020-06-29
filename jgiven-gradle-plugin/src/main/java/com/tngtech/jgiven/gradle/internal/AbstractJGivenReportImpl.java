package com.tngtech.jgiven.gradle.internal;

import com.tngtech.jgiven.gradle.JGivenReport;
import com.tngtech.jgiven.report.AbstractReportConfig;
import com.tngtech.jgiven.report.AbstractReportGenerator;
import com.tngtech.jgiven.report.ReportGenerator;
import com.tngtech.jgiven.report.asciidoc.AsciiDocReportConfig;
import com.tngtech.jgiven.report.asciidoc.AsciiDocReportGenerator;
import com.tngtech.jgiven.report.html5.Html5ReportConfig;
import com.tngtech.jgiven.report.text.PlainTextReportConfig;
import com.tngtech.jgiven.report.text.PlainTextReportGenerator;
import org.gradle.api.Task;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.file.FileSystemLocation;
import org.gradle.api.file.FileSystemLocationProperty;
import org.gradle.api.internal.provider.DefaultProperty;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.provider.Property;
import org.gradle.api.reporting.internal.TaskGeneratedSingleDirectoryReport;
import org.gradle.api.tasks.Internal;

import java.io.File;

public abstract class AbstractJGivenReportImpl extends TaskGeneratedSingleDirectoryReport implements JGivenReport {

    private File customCssFile;
    private File customJsFile;
    private String title;
    private final Property<Boolean> isRequired;
    private final DirectoryProperty outputLocation;
    private boolean excludeEmptyScenarios = false;
    private boolean thumbnailsAreShown = true;

    public AbstractJGivenReportImpl(String name, Task task, String relativeEntryPath) {
        super( name, task, relativeEntryPath );
        System.out.println("DEBUG 3:");
        System.out.println(task.getProject().getObjects().toString());
        System.out.println("END DEBUG 3:");
        isRequired = task.getProject().getObjects().property(Boolean.class);
        isRequired.convention(true);
        outputLocation = task.getProject().getObjects().directoryProperty();


    }

    public AbstractReportGenerator createGenerator() {
        AbstractReportConfig conf;
        AbstractReportGenerator generator;
        switch( getFormat() ) {
            case ASCIIDOC:
                conf = new AsciiDocReportConfig();
                generator = new AsciiDocReportGenerator();
                break;
            case TEXT:
                conf = new PlainTextReportConfig();
                generator = new PlainTextReportGenerator();
                break;
            case HTML:
            case HTML5:
            default:
                Html5ReportConfig customConf = new Html5ReportConfig();
                customConf.setShowThumbnails( isThumbnailsAreShown() );
                if (getCustomCssFile() != null) {
                    customConf.setCustomCss( getCustomCssFile() );
                }
                if (getCustomJsFile() != null) {
                    customConf.setCustomJs( getCustomJsFile() );
                }
                conf = customConf;
                generator = ReportGenerator.generateHtml5Report();
                break;
        }
        if( getTitle() != null ) {
            conf.setTitle( getTitle() );
        }
        conf.setTargetDir( getDestination() );
        conf.setExcludeEmptyScenarios( isExcludeEmptyScenarios() );
        generator.setConfig( conf );
        return generator;
    }

    @Internal
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

    @Override public boolean isThumbnailsAreShown() {
        return thumbnailsAreShown;
    }

    @Override public void setThumbnailsAreShown( boolean thumbnailsAreShown ) {
        this.thumbnailsAreShown = thumbnailsAreShown;
    }

    @Override
    public Property<Boolean> getRequired() {
        return isRequired;
    }

    @Override
    public DirectoryProperty getOutputLocation() {
        return outputLocation;
    }
}

