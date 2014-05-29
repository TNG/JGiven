package com.tngtech.jgiven.impl;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Optional;

/**
 * Helper class to access all system properties to configure JGiven
 */
public class Config {
    private static final Logger log = LoggerFactory.getLogger( Config.class );
    private static final Config INSTANCE = new Config();

    public static Config config() {
        return INSTANCE;
    }

    public boolean isReportEnabled() {
        return System.getProperty( "jgiven.report.enabled", "true" ).equalsIgnoreCase( "true" );
    }

    public void setReportEnabled( boolean enabled ) {
        System.setProperty( "jgiven.report.enabled", "" + enabled );
    }

    public Optional<File> getReportDir() {
        String reportDirName = System.getProperty( "jgiven.report.dir" );
        if( reportDirName == null ) {
            if( System.getProperty( "surefire.test.class.path" ) != null ) {
                reportDirName = "target/jgiven-reports/json";
                log.info( "jgiven.report.dir not set, but detected surefire plugin, generating reports to " + reportDirName );
            } else {
                reportDirName = "jgiven-reports";
                log.info( "jgiven.report.dir not set, using default value jgiven-reports" );
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
        return System.getProperty( "jgiven.report.text.color", "false" ).equalsIgnoreCase( "true" );
    }

    public boolean textReport() {
        return System.getProperty( "jgiven.report.text", "true" ).equalsIgnoreCase( "true" );
    }

    public void setTextReport( boolean b ) {
        System.setProperty( "jgiven.report.text", "" + b );
    }

}
