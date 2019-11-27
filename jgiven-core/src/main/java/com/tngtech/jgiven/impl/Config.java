package com.tngtech.jgiven.impl;

import com.tngtech.jgiven.config.ConfigValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Optional;

/**
 * Helper class to access all system properties to configure JGiven.
 */
public class Config {
    private static final Logger log = LoggerFactory.getLogger( Config.class );
    private static final Config INSTANCE = new Config();

    private static final String TRUE = "true";
    private static final String FALSE = "false";
    private static final String AUTO = "auto";
    private static final String JGIVEN_REPORT_ENABLED = "jgiven.report.enabled";
    public static final String JGIVEN_REPORT_DIR = "jgiven.report.dir";
    private static final String JGIVEN_REPORT_TEXT = "jgiven.report.text";
    private static final String JGIVEN_REPORT_TEXT_COLOR = "jgiven.report.text.color";
    private static final String JGIVEN_FILTER_STACK_TRACE = "jgiven.report.filterStackTrace";
    private static final String JGIVEN_REPORT_DRY_RUN = "jgiven.report.dry-run";

    public static Config config(){
        return INSTANCE;
    }

    static{
        if( INSTANCE.dryRun() ) {
            log.info( "Dry Run enabled." );
        }
    }

    public Optional<File> getReportDir(){
        String reportDirName = System.getProperty( JGIVEN_REPORT_DIR );
        if( reportDirName == null ) {
            if( System.getProperty( "surefire.test.class.path" ) != null ) {
                reportDirName = "target/jgiven-reports/json";
                log.info( JGIVEN_REPORT_DIR + " not set, but detected surefire plugin, generating reports to " + reportDirName );
            } else {
                reportDirName = "jgiven-reports";
                log.debug( JGIVEN_REPORT_DIR + " not set, using default value jgiven-reports" );
            }
        }

        File reportDir = new File( reportDirName );
        if( reportDir.exists() && !reportDir.isDirectory() ) {
            log.warn( reportDirName + " exists but is not a directory. Will not generate JGiven reports." );
            return Optional.empty();
        }

        log.debug( "Using folder " + reportDirName + " to store JGiven reports" );

        return Optional.of( reportDir );
    }

    public boolean isReportEnabled(){
        return TRUE.equalsIgnoreCase( System.getProperty( JGIVEN_REPORT_ENABLED, TRUE ) );
    }

    public void setReportEnabled( boolean enabled ){
        System.setProperty( JGIVEN_REPORT_ENABLED, "" + enabled );
    }

    public ConfigValue textColorEnabled(){
        return ConfigValue.fromString( System.getProperty( JGIVEN_REPORT_TEXT_COLOR, AUTO ) );
    }

    public boolean textReport(){
        return TRUE.equalsIgnoreCase( System.getProperty( JGIVEN_REPORT_TEXT, TRUE ) );
    }

    public void setTextReport( boolean b ){
        System.setProperty( JGIVEN_REPORT_TEXT, "" + b );
    }

    public boolean filterStackTrace(){
        return TRUE.equalsIgnoreCase( System.getProperty( JGIVEN_FILTER_STACK_TRACE, TRUE ) );
    }

    public void setReportDir( File reportDir ){
        System.setProperty( JGIVEN_REPORT_DIR, reportDir.getAbsolutePath() );
    }

    public boolean dryRun(){
        return TRUE.equals( System.getProperty( JGIVEN_REPORT_DRY_RUN, FALSE ) );
    }
}
