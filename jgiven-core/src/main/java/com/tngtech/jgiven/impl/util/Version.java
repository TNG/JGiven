package com.tngtech.jgiven.impl.util;

import java.io.InputStream;
import java.util.Properties;

import com.tngtech.jgiven.exception.JGivenInstallationException;

public class Version {
    public static final String JGIVEN_VERSION_PROPERTIES = "com/tngtech/jgiven/jgiven-version.properties";
    public static final Version VERSION = loadVersion();

    private final String versionString;
    private final String commitHash;

    public Version( String versionString, String commitHash ) {
        this.versionString = versionString;
        this.commitHash = commitHash;
    }

    @Override
    public String toString() {
        return versionString + "-" + commitHash;
    }

    private static Version loadVersion() {
        Properties properties = new Properties();
        InputStream resourceAsStream = Version.class.getClassLoader().getResourceAsStream( JGIVEN_VERSION_PROPERTIES );
        try {
            properties.load( resourceAsStream );
            return Version.fromProperties( properties );
        } catch( Exception e ) {
            throw new JGivenInstallationException( "Could not load the JGiven version file " + JGIVEN_VERSION_PROPERTIES + ". "
                    + e.getMessage(), e );
        } finally {
            ResourceUtil.close( resourceAsStream );
        }
    }

    private static Version fromProperties( Properties properties ) {
        String versionString = properties.getProperty( "jgiven.version", "Unknown Version" );
        String commitHash = properties.getProperty( "jgiven.buildNumber", "Unknown Build Number" );

        return new Version( versionString, commitHash );
    }
}
