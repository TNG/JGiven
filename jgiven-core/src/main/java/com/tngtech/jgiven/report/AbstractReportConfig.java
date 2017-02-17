package com.tngtech.jgiven.report;

import com.tngtech.jgiven.report.json.ReportModelReader;
import com.tngtech.jgiven.report.model.CompleteReportModel;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import java.util.Map;
import java.io.File;

/**
 * Basic configuration for a report with an extendable interface
 * The configMap should always be in a valid state and have all possible flags, except the optional ones without a default (like --help)
 * For examples see {@link com.tngtech.jgiven.report.asciidoc.AsciiDocReportConfig}
 */
public abstract class AbstractReportConfig {

    private static final Logger log = LoggerFactory.getLogger( AbstractReportConfig.class );

    private String title;
    private File sourceDir;
    private File targetDir;
    private Boolean excludeEmptyScenarios;

    public AbstractReportConfig( Map<String, Object> configMap ) {
        setTitle( (String) configMap.get( "title" ) );
        setSourceDir( (File) configMap.get( "sourceDir" ) );
        setTargetDir( (File) configMap.get( "targetDir" ) );
        setExcludeEmptyScenarios( (Boolean) configMap.get( "excludeEmptyScenarios" ) );
        useConfigMap( configMap );
    }

    public AbstractReportConfig() {
        setTitle( "JGiven Report" );
        setSourceDir( new File( "." ) );
        setTargetDir( new File( "." ) );
        setExcludeEmptyScenarios( false );
    }

    public String getTitle() {
        return title;
    }

    public void setTitle( String title ) {
        this.title = title;
    }

    public File getSourceDir() {
        return sourceDir;
    }

    public void setSourceDir( File sourceDir ) {
        this.sourceDir = sourceDir;
    }

    public File getTargetDir() {
        return targetDir;
    }

    public void setTargetDir( File targetDir ) {
        this.targetDir = targetDir;
    }

    public Boolean getExcludeEmptyScenarios() {
        return excludeEmptyScenarios;
    }

    public void setExcludeEmptyScenarios( Boolean excludeEmptyScenarios ) {
        this.excludeEmptyScenarios = excludeEmptyScenarios;
    }

    public CompleteReportModel getReportModel() {
        return new ReportModelReader( this ).readDirectory();
    }

    /**
     *
     * Every flag should be defined except the optional ones without a default (like --help)
     *
     * @param configMap the config map with a mapping of Strings to castable objects
     */
    public abstract void useConfigMap( Map<String, Object> configMap );
}
