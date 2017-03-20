package com.tngtech.jgiven.report;

import com.tngtech.jgiven.report.config.*;
import com.tngtech.jgiven.report.config.converter.*;
import com.tngtech.jgiven.report.json.ReportModelReader;
import com.tngtech.jgiven.report.model.CompleteReportModel;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import java.util.*;
import java.io.File;

/**
 * Basic configuration for a report with an extendable interface
 * The configMap should always be in a valid state and have all possible flags, except the optional ones without a default (like --help)
 * For examples see {@link com.tngtech.jgiven.report.asciidoc.AsciiDocReportConfig}
 */
public abstract class AbstractReportConfig {

    private static final Logger log = LoggerFactory.getLogger( AbstractReportConfig.class );
    private List<ConfigOption> configOptions = createConfigOptions();

    private String title;
    private File sourceDir;
    private File targetDir;
    private Boolean excludeEmptyScenarios;

    public AbstractReportConfig( String... args ) {
        Map<String, Object> configMap = new ConfigOptionParser().generate( configOptions, args );
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

    private List<ConfigOption> createConfigOptions() {
        List<ConfigOption> configOptions = new ArrayList<ConfigOption>();

        ConfigOption sourceDir = new ConfigOptionBuilder( "sourceDir" )
                .setCommandLineOptionWithArgument(
                        new CommandLineOptionBuilder( "--sourceDir" ).setArgumentDelimiter( "=" ).setShortPrefix( "--dir" )
                                .setVisualPlaceholder( "path" ).build(),
                        new ToFile() )
                .setDescription( "the source directory where the JGiven JSON files are located (default: .)" )
                .setDefaultWith( new File( "." ) )
                .build();

        ConfigOption targetDir = new ConfigOptionBuilder( "targetDir" )
                .setCommandLineOptionWithArgument(
                        new CommandLineOptionBuilder( "--targetDir" ).setArgumentDelimiter( "=" ).setShortPrefix( "--todir" )
                                .setVisualPlaceholder( "path" ).build(),
                        new ToFile() )
                .setDescription( "the directory to generate the report to (default: .)" )
                .setDefaultWith( new File( "." ) )
                .build();

        ConfigOption title = new ConfigOptionBuilder( "title" )
                .setCommandLineOptionWithArgument(
                        new CommandLineOptionBuilder( "--title" ).setArgumentDelimiter( "=" ).setVisualPlaceholder( "string" ).build(),
                        new ToString() )
                .setDescription( "the title of the report (default: JGiven Report)" )
                .setDefaultWith( "JGiven Report" )
                .build();

        ConfigOption excludeEmptyScenarios = new ConfigOptionBuilder( "excludeEmptyScenarios" )
                .setCommandLineOptionWithArgument(
                        new CommandLineOptionBuilder( "--exclude-empty-scenarios" ).setArgumentDelimiter( "=" )
                                .setVisualPlaceholder( "boolean" ).build(),
                        new ToBoolean() )
                .setDescription( "(default: false)" )
                .setDefaultWith( false )
                .build();

        configOptions.addAll( Arrays.asList( sourceDir, targetDir, title, excludeEmptyScenarios ) );
        additionalConfigOptions( configOptions );
        return configOptions;
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

    public void printUsageAndExit() {
        new ConfigOptionParser().printUsageAndExit( configOptions );
    }

    /**
     *
     * Every flag should be defined except the optional ones without a default (like --help)
     *
     * @param configMap the config map with a mapping of Strings to castable objects
     */
    public abstract void useConfigMap( Map<String, Object> configMap );

    /**
     *
     * This is used to create new {@link ConfigOption} for the {@link AbstractReportConfig} by appending them to the list
     *
     * @param configOptions config options list, add new options here
     */
    public abstract void additionalConfigOptions( List<ConfigOption> configOptions );
}
