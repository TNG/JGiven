package com.tngtech.jgiven.impl;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Optional;

/**
 * Helper class to access all system properties to configure JGiven.
 */
public class Config {
    private static final Logger log = LoggerFactory.getLogger( Config.class );
    private static final Config INSTANCE = new Config();

    private static final String TRUE = "true";
    private static final String FALSE = "false";
    private static final String JGIVEN_REPORT_ENABLED = "jgiven.report.enabled";
    private static final String JGIVEN_REPORT_DIR = "jgiven.report.dir";
    private static final String JGIVEN_REPORT_TEXT = "jgiven.report.text";
    private static final String JGIVEN_REPORT_TEXT_COLOR = "jgiven.report.text.color";

    public static Config config() {
        return INSTANCE;
    }

    public boolean isReportEnabled() {
        return TRUE.equalsIgnoreCase( System.getProperty( JGIVEN_REPORT_ENABLED, TRUE ) );
    }

    public void setReportEnabled( boolean enabled ) {
        System.setProperty( JGIVEN_REPORT_ENABLED, "" + enabled );
    }

    public Optional<File> getReportDir() {
        String reportDirName = System.getProperty( JGIVEN_REPORT_DIR );
        if( reportDirName == null ) {
            if( System.getProperty( "surefire.test.class.path" ) != null ) {
                reportDirName = "target/jgiven-reports/json";
                log.info( JGIVEN_REPORT_DIR + " not set, but detected surefire plugin, generating reports to " + reportDirName );
            } else {
                reportDirName = "jgiven-reports";
                log.info( JGIVEN_REPORT_DIR + " not set, using default value jgiven-reports" );
            }
        }

        File reportDir = new File( reportDirName );
        if( reportDir.exists() && !reportDir.isDirectory() ) {
            log.warn( reportDirName + " exists but is not a directory. Will not generate JGiven reports." );
            return Optional.absent();
        }

        log.info( "Using folder " + reportDirName + " to store JGiven reports" );

        return Optional.of( reportDir );
    }

    public boolean textColorEnabled() {
        return TRUE.equalsIgnoreCase( System.getProperty( JGIVEN_REPORT_TEXT_COLOR, FALSE ) );
    }

    public boolean textReport() {
        return TRUE.equalsIgnoreCase( System.getProperty( JGIVEN_REPORT_TEXT, TRUE ) );
    }

    public void setTextReport( boolean b ) {
        System.setProperty( JGIVEN_REPORT_TEXT, "" + b );
    }

}
