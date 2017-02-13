package com.tngtech.jgiven.report;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.tngtech.jgiven.report.model.CompleteReportModel;
import com.tngtech.jgiven.report.json.ReportModelReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This abstract class is the basic layout that includes the minimal functionality for reading/writing a report
 *
 * There are five predefined flags with their respective setter and getter + help
 * <ul>
 *   <li> --format= </li>
 *   <li> --sourceDir= /--dir= </li>
 *   <li> --targetDir= /--todir= </li>
 *   <li> --title= </li>
 *   <li> --exclude-empty-scenarios=&lt;boolean&gt; </li>
 *   <li> --help / -h </li>
 * </ul>
 * Everything except the {@link ReportGenerator.Format} has a default value.
 *
 * The functionality is piped together for an easier and extendable interface to create a custom report.
 *
 */
public abstract class AbstractReport {
    private static final Logger log = LoggerFactory.getLogger( AbstractReport.class );

    protected CompleteReportModel completeReportModel;

    private List<String> flags = new ArrayList<String>(
            Arrays.asList( "-h", "--help", "--format=", "--sourceDir=", "--targetDir=", "--title=", "--exclude-empty-scenarios=", "--dir",
                    "--todir" ) );
    private Map<String, String> flagMap;
    private String format;
    private String title;
    private File sourceDir;
    private File targetDir;
    private Boolean excludeEmptyScenarios;

    public String getFormat() {
        return format;
    }

    private void setFormat( String format ) {
        this.format = format;
    }

    public String getTitle() {
        return title;
    }

    private void setTitle( String title ) {
        this.title = title;
    }

    public File getSourceDir() {
        return sourceDir;
    }

    public void setSourceDir( String pathname ) {
        this.sourceDir = new File( pathname );
    }

    public File getTargetDir() {
        return targetDir;
    }

    public void setTargetDir( String pathname ) {
        this.targetDir = new File( pathname );
    }

    public Boolean getExcludeEmptyScenarios() {
        return excludeEmptyScenarios;
    }

    private void setExcludeEmptyScenarios( Boolean excludeEmptyScenarios ) {
        this.excludeEmptyScenarios = excludeEmptyScenarios;
    }

    public void addFlags( String... flags ) {
        for( String flag : flags ) {
            this.flags.add( flag );
        }
    }

    public void parseFlags() {
        if( flagMap.containsKey( "-h" ) || flagMap.containsKey( "--help" ) ) {
            printUsageAndExit();
        }
        if( !flagMap.containsKey( "--format=" ) ) {
            System.err.println( "No argument provided for --format. Terminating." );
            printUsageAndExit();
        } else {
            setFormat( flagMap.get( "--format=" ) );
        }
        setTitle( getOrDefault( flagMap, "--title=", "JGiven Report" ) );
        setSourceDir( getOrDefault( flagMap, "--sourceDir=", "." ) );
        setTargetDir( getOrDefault( flagMap, "--targetDir=", "." ) );

        if( flagMap.containsKey( "--dir=" ) ) {
            setSourceDir( flagMap.get( "--dir=" ) );
            System.err.println( "DEPRECATION WARNING: --dir is deprecated, please use --sourceDir instead" );
        }
        if( flagMap.containsKey( "--todir=" ) ) {
            setTargetDir( flagMap.get( "--todir=" ) );
            System.err.println( "DEPRECATION WARNING: --todir is deprecated, please use --targetDir instead" );
        }

        setExcludeEmptyScenarios( Boolean.parseBoolean( getOrDefault( flagMap, "--exclude-empty-scenarios=", "false" ) ) );
        parseFlags( flagMap );
    }

    public void printUsageAndExit() {
        System.err
                .println( "Options: \n"
                        + "  --format=<format>             the format of the report. Either html or text (required) \n"
                        + "  --sourceDir=<dir>             the source directory where the JGiven JSON files are located (default: .)\n"
                        + "  --targetDir=<dir>             the directory to generate the report to (default: .)\n"
                        + "  --title=<title>               the title of the report (default: JGiven Report)\n"
                        + "  --exclude-empty-scenarios=<b> (default: false)\n"
                        + "  --help / -h                   print this help message"
                );
        printAddedUsage();
        System.exit( 1 );
    }

    public void generate( AbstractCommandLineParser cmdParser, List<String> args ) {
        addFlags();
        parseFlagsWith( cmdParser, args );
        this.completeReportModel = readReportModel();
        try {
            generate();
        } catch( Exception e ) {
            System.err.println( "Error: JGivenReport has encountered the following exception: " + e + "\n" );
            printUsageAndExit();
        }
    }

    public void parseFlagsWith( AbstractCommandLineParser cmdParser, List<String> args ) {
        this.flagMap = cmdParser.parseToFlagMap( flags, args );
        parseFlags();
    }

    public CompleteReportModel readReportModel() {
        return new ReportModelReader( this ).readDirectory();
    }

    protected static <K, V> V getOrDefault( Map<K, V> map, K key, V defaultValue ) {
        return map.containsKey( key ) ? map.get( key ) : defaultValue;
    }

    /**
     * This method is called in first place to update the flag-list which is given
     * to the command line parser
     */
    public abstract void addFlags();

    /**
     * This is called after the cmd-parser filled the flagMap with values for
     * additional flag parsing
     *
     * If an additional flag should be parsed, addFlags() has to update the flag-list
     */
    public abstract void parseFlags( Map<String, String> flagMap );

    /**
     * This implements the main functionality of the report generator, utilizing the information
     * from the command line arguments
     */
    public abstract void generate() throws Exception;

    /**
     * This is called if the help flag is accessed or an unexpected termination of the program
     * Use System.err to print the additional information to flags (align to message to 25 chars)
     */
    public abstract void printAddedUsage();

}
