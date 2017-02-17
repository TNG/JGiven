package com.tngtech.jgiven.report;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.io.File;

import com.tngtech.jgiven.report.config.*;
import com.tngtech.jgiven.report.config.converter.ToBoolean;
import com.tngtech.jgiven.report.config.converter.ToFile;
import com.tngtech.jgiven.report.config.converter.ToString;
import com.tngtech.jgiven.report.model.CompleteReportModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This abstract class is the basic layout that includes the minimal functionality for reading/writing a report
 *
 * The following flags are predefined in {@link #populateConfigOptions()}:
 * <ul>
 *   <li> --format= </li>
 *   <li> --sourceDir= /--dir= </li>
 *   <li> --targetDir= /--todir= </li>
 *   <li> --title= </li>
 *   <li> --exclude-empty-scenarios=&lt;boolean&gt; </li>
 *   <li> --help / -h </li>
 * </ul>
 *
 * Everything has a default value.
 *
 * The functionality is piped together for an easier and extendable interface to create a custom report
 * For examples see {@link com.tngtech.jgiven.report.asciidoc.AsciiDocReportGenerator}
 *
 */
public abstract class AbstractReportGenerator {
    private static final Logger log = LoggerFactory.getLogger( AbstractReportGenerator.class );
    private ConfigOptionParser configParser = new ConfigOptionParser();
    private List<ConfigOption> configOptions = new ArrayList<ConfigOption>();

    protected CompleteReportModel completeReportModel;
    public AbstractReportConfig config;

    private void addConfigOptions( ConfigOption... cos ) {
        configOptions.addAll( Arrays.asList( cos ) );
    }

    protected Map<String, Object> generateConfigMap( String... args ) {
        return configParser.generate( configOptions, args );
    }

    public void setConfig( AbstractReportConfig config ) {
        this.config = config;
    }

    protected void parseToConfig( String... args ) {
        populateConfigOptions();
        setConfig( createReportConfig( generateConfigMap( args ) ) );
    }

    protected void generateFromCommandLine( String... args ) {
        parseToConfig( args );
        generateWithoutParsing();
    }

    public void generateWithConfig( AbstractReportConfig config ) {
        setConfig( config );
        generateWithoutParsing();
    }

    public void generateWithoutParsing() {
        loadReportModel();
        try {
            generate();
        } catch( Exception e ) {
            System.err.println( "Error: JGivenReport has encountered the following exception: " + e + "\n" );
            printUsageAndExit();
        }
    }

    public void populateConfigOptions() {
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

        ConfigOption help = new ConfigOptionBuilder( "help" )
                .setCommandLineOptionWithoutArgument(
                        new CommandLineOptionBuilder( "--help" ).setShortPrefix( "-h" ).build(),
                        true )
                .setDescription( "print this help message" )
                .setOptional()
                .build();

        addConfigOptions( sourceDir, targetDir, title, excludeEmptyScenarios, help );
        additionalConfigOptions( configOptions );
    }

    private void printUsageAndExit() {
        configParser.printUsageAndExit( configOptions );
    }

    public void loadReportModel() {
        this.completeReportModel = config.getReportModel();
    }

    /**
     *
     * This is used to create new {@link ConfigOption} for the report by appending them to the list
     *
     * @param configOptions global config options list, add new options here
     */
    public abstract void additionalConfigOptions( List<ConfigOption> configOptions );

    /**
     *
     *
     * @param configMap this configuration map contains every config option you specified plus the 5 default ones
     * @return a {@link AbstractReportConfig} where any option may be accessible via setter and getter
     */
    public abstract AbstractReportConfig createReportConfig( Map<String, Object> configMap );

    /**
     * This implements the main functionality of the report generator, utilizing the information
     * from the specialized {@link AbstractReportConfig}
     */
    public abstract void generate() throws Exception;

}
