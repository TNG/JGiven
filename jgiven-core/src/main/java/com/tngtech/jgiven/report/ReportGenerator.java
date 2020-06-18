package com.tngtech.jgiven.report;

import com.tngtech.jgiven.report.config.ConfigOptionParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tngtech.jgiven.exception.JGivenInstallationException;
import com.tngtech.jgiven.exception.JGivenInternalDefectException;
import com.tngtech.jgiven.report.asciidoc.AsciiDocReportGenerator;
import com.tngtech.jgiven.report.text.PlainTextReportGenerator;

import java.util.Arrays;

/**
 *  This is an interface to create a report based on command line flags
 */
public class ReportGenerator {

    private static final Logger log = LoggerFactory.getLogger( ReportGenerator.class );

    /**
     * to create a custom report, extend this enum with the name of your choice
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
            return Arrays.stream(Format.values())
                    .filter(format -> format.text.equalsIgnoreCase((value)))
                    .findFirst()
                    .orElse(null);
        }
    }

    /**
     * Starts the respective report (default is HTML5)
     */
    public void generate( String... args ) {
        Format format = ConfigOptionParser.getFormat( args );
        switch( format ) {
            case ASCIIDOC:
                new AsciiDocReportGenerator().generateFromCommandLine( args );
                break;
            case TEXT:
                new PlainTextReportGenerator().generateFromCommandLine( args );
                break;
            case HTML:
            case HTML5:
            default:
                ReportGenerator.generateHtml5Report().generateFromCommandLine( args );
                break;
        }
    }

    /**
     * Searches the Html5ReportGenerator in Java path and instantiates the report
     */
    public static AbstractReportGenerator generateHtml5Report() {
        AbstractReportGenerator report;
        try {
            Class<?> aClass = ReportGenerator.class.getClassLoader()
                    .loadClass( "com.tngtech.jgiven.report.html5.Html5ReportGenerator" );
            report = (AbstractReportGenerator) aClass.getDeclaredConstructor().newInstance();
        } catch( ClassNotFoundException e ) {
            throw new JGivenInstallationException( "The JGiven HTML5 Report Generator seems not to be on the classpath.\n"
                    + "Ensure that you have a dependency to jgiven-html5-report." );
        } catch( Exception e ) {
            throw new JGivenInternalDefectException( "The HTML5 Report Generator could not be instantiated.", e );
        }
        return report;
    }

    public static void main( String... args ) {
        new ReportGenerator().generate( args );
    }
}
