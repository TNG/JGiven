package com.tngtech.jgiven.report;

import static com.tngtech.jgiven.report.ReportGenerator.Format.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tngtech.jgiven.exception.JGivenInstallationException;
import com.tngtech.jgiven.exception.JGivenInternalDefectException;
import com.tngtech.jgiven.report.asciidoc.AsciiDocReportGenerator;
import com.tngtech.jgiven.report.text.PlainTextReportGenerator;

/**
 *  This class defines an interface to create a chosen report based on command line flags
 *  It's also possible to run a report with custom added flags, see {@link #addFlag(String)}
 */
public class ReportGenerator {

    private static final Logger log = LoggerFactory.getLogger( ReportGenerator.class );
    private List<String> flagList = new ArrayList<String>();

    /**
     * to create a custom parser, extend this enum with the name of your choice
     */
    public enum Format {
        HTML( "html" ),
        TEXT( "text" ),
        ASCIIDOC( "asciidoc" ),
        HTML5( "html5" );

        private final String text;

        Format( String text ) {
            this.text = text;
        }

        public static Format fromStringOrNull( String value ) {
            for( Format format : values() ) {
                if( format.text.equalsIgnoreCase( value ) ) {
                    return format;
                }
            }
            return null;
        }
    }

    /**
     * Starts the respective report (default is HTML5)
     */
    public void generate( List<String> args ) {
        AbstractReport report = createReport( args );
        report.generate( new SimpleCommandLineParser(), args );
    }

    /**
     * Parses the format and returns the respective report
     */
    public AbstractReport createReport( List<String> args ) {
        Format format = fromStringOrNull( AbstractCommandLineParser.getFormat( args ) );
        if( format == null || format == HTML || format == HTML5 ) {
            return this.generateHtml5Report();
        } else if( format == TEXT ) {
            return new PlainTextReportGenerator();
        } else {
            return new AsciiDocReportGenerator();
        }
    }

    /**
     *  Interface to start the ReportGenerator for non-command line use (e.g. gradle-plugin)
     */
    public void generate() {
        generate( flagList );
    }

    /**
     *  Interface to access the created report based on the custom set flags for test purposes
     */
    public AbstractReport createInternalReport( Format type ) {
        flagList.add( "--format=" + type );
        AbstractReport report = createReport( flagList );
        report.parseFlagsWith( new SimpleCommandLineParser(), flagList );
        return report;
    }

    /**
     * Searches the Html5ReportGenerator in Java path and instantiates the report
     */
    private AbstractReport generateHtml5Report() {
        AbstractReport report;
        try {
            Class<?> aClass = this.getClass().getClassLoader().loadClass( "com.tngtech.jgiven.report.html5.Html5ReportGenerator" );
            report = (AbstractReport) aClass.newInstance();
        } catch( ClassNotFoundException e ) {
            throw new JGivenInstallationException( "The JGiven HTML5 Report Generator seems not to be on the classpath.\n"
                    + "Ensure that you have a dependency to jgiven-html5-report." );
        } catch( Exception e ) {
            throw new JGivenInternalDefectException( "The HTML5 Report Generator could not be instantiated.", e );
        }

        return report;
    }

    /**
     * Interface to append new flags for non-command line use (e.g. gradle-plugin)
     */
    public void addFlag( String flag ) {
        flagList.add( flag );
    }

    public static void main( String... args ) throws Exception {
        new ReportGenerator().generate( Arrays.asList( args ) );
    }

}
