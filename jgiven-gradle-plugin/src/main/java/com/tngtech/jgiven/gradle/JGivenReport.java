package com.tngtech.jgiven.gradle;

import com.tngtech.jgiven.report.AbstractReportGenerator;
import org.gradle.api.reporting.DirectoryReport;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.InputFile;
import org.gradle.api.tasks.Optional;
import org.gradle.api.tasks.PathSensitive;
import org.gradle.api.tasks.PathSensitivity;

import java.io.File;

public interface JGivenReport extends DirectoryReport {
    AbstractReportGenerator createGenerator();

    @Optional
    @InputFile
    @PathSensitive( PathSensitivity.NONE )
    File getCustomCssFile();

    void setCustomCssFile( File customCssFile );

    @Optional
    @InputFile
    @PathSensitive( PathSensitivity.NONE )
    File getCustomJsFile();

    void setCustomJsFile( File customJsFile );

    @Input
    @Optional
    String getTitle();

    void setTitle( String title );

    @Input
    boolean isExcludeEmptyScenarios();

    void setExcludeEmptyScenarios( boolean excludeEmptyScenarios );

    @Input
    @Optional
    boolean thumbnailsAreShown();

    void setThumbnailsAreShown( boolean showThumbnails );
}
