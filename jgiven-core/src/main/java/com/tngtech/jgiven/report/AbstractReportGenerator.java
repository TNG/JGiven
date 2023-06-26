package com.tngtech.jgiven.report;

import com.tngtech.jgiven.report.model.CompleteReportModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This abstract class is the basic layout that includes the minimal functionality for reading/writing a report
 *
 * The following flags are predefined in {@link AbstractReportConfig#createConfigOptions()}:
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

    protected CompleteReportModel completeReportModel;
    public AbstractReportConfig config;

    public void setConfig( AbstractReportConfig config ) {
        this.config = config;
    }

    protected void generateFromCommandLine( String... args ) {
        setConfig( createReportConfig( args ) );
        generateReport();
    }

    public void generateWithConfig( AbstractReportConfig config ) {
        setConfig( config );
        generateReport();
    }

    private void generateReport() {
        loadReportModel();
        try {
            generate();
        } catch( Exception e ) {
            log.error("JGivenReport has encountered the following exception: " + e + "\n" );
            printUsageAndExit();
        }
    }

    private void printUsageAndExit() {
        config.printUsageAndExit();
    }

    public void loadReportModel() {
        this.completeReportModel = config.getReportModel();
    }

    /**
     *
     * @param args these are the command line arguments
     * @return an {@link AbstractReportConfig} where any option may be accessible via setter and getter
     */
    public abstract AbstractReportConfig createReportConfig( String... args );

    /**
     * This implements the main functionality of the report generator, utilizing the information
     * from the specialized {@link AbstractReportConfig}
     */
    public abstract void generate() throws Exception;

}
