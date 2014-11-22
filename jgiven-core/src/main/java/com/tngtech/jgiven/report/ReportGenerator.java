package com.tngtech.jgiven.report;

import static com.tngtech.jgiven.report.ReportGenerator.Format.*;

import java.io.File;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.Files;
import com.tngtech.jgiven.exception.JGivenInstallationException;
import com.tngtech.jgiven.exception.JGivenInternalDefectException;
import com.tngtech.jgiven.report.html.StaticHtmlReportGenerator;
import com.tngtech.jgiven.report.impl.FileGenerator;
import com.tngtech.jgiven.report.text.PlainTextReportGenerator;

public class ReportGenerator {

    private static final Logger log = LoggerFactory.getLogger( ReportGenerator.class );

    public enum Format {
        HTML( "html" ),
        TEXT( "text" ),
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

    private File sourceDir = new File( "." );
    private File toDir = new File( "." );
    private File customCssFile = null;
    private Format format = HTML;

    public static void main( String... args ) throws Exception {
        ReportGenerator generator = new ReportGenerator();
        parseArgs( generator, args );
        generator.generate();
    }

    static void parseArgs( ReportGenerator generator, String... args ) {
        for( String arg : args ) {
            if( arg.equals( "-h" ) || arg.equals( "--help" ) ) {
                printUsageAndExit();
            } else if( arg.startsWith( "--dir=" ) ) {
                generator.setSourceDir( new File( arg.split( "=" )[1] ) );
            } else if( arg.startsWith( "--todir=" ) ) {
                generator.setToDir( new File( arg.split( "=" )[1] ) );
            } else if( arg.startsWith( "--customcss=" ) ) {
                generator.setCustomCssFile( new File( arg.split( "=" )[1] ) );
            } else if( arg.startsWith( "--format=" ) ) {
                String formatArg = arg.split( "=" )[1];
                Format format = Format.fromStringOrNull( formatArg );
                if( format == null ) {
                    System.err.println( "Illegal argument for --format: " + formatArg );
                    printUsageAndExit();
                }
                generator.setFormat( format );
            } else {
                printUsageAndExit();
            }
        }
    }

    public void setFormat( Format format ) {
        this.format = format;
    }

    public void generate() throws Exception {
        if( !getToDir().exists() && !getToDir().mkdirs() ) {
            log.error( "Could not create target directory " + getToDir() );
            return;
        }

        if( format == HTML ) {
            generateStaticHtmlReport();
        } else if( format == HTML5 ) {
            generateHtml5Report();
        } else if( format == TEXT ) {
            new PlainTextReportGenerator().generate( getToDir(), getSourceDir() );
        }

    }

    private void generateStaticHtmlReport() throws IOException {
        new StaticHtmlReportGenerator().generate( getToDir(), getSourceDir() );
        if( getCustomCssFile() != null ) {
            if( !getCustomCssFile().canRead() ) {
                log.info( "Cannot read customCssFile " + getCustomCssFile() + " skipping" );
            } else {
                Files.copy( getCustomCssFile(), new File( getToDir(), "custom.css" ) );
            }
        }
    }

    private void generateHtml5Report() throws IOException {
        FileGenerator fileGenerator;
        try {
            Class<?> aClass = this.getClass().getClassLoader().loadClass( "com.tngtech.jgiven.report.html5.Html5ReportGenerator" );
            fileGenerator = (FileGenerator) aClass.newInstance();
        } catch( ClassNotFoundException e ) {
            throw new JGivenInstallationException( "The JGiven HTML5 Report Generator seems not to be on the classpath.\n" +
                    "Ensure that you have a dependency to jgiven-html5-report." );
        } catch( Exception e ) {
            throw new JGivenInternalDefectException( "The HTML5 Report Generator could not be instantiated.", e );
        }

        fileGenerator.generate( getToDir(), getSourceDir() );
    }

    private static void printUsageAndExit() {
        System.err.println( "Options: [--format=<format>] [--dir=<dir>] [--todir=<dir>] [--customcss=<cssfile>]" ); // NOSONAR
        System.err.println( "  <format> = html, html5, or text, default is html" );
        System.exit( 1 );
    }

    public File getSourceDir() {
        return sourceDir;
    }

    public void setSourceDir( File sourceDir ) {
        this.sourceDir = sourceDir;
    }

    public File getToDir() {
        return toDir;
    }

    public void setToDir( File toDir ) {
        this.toDir = toDir;
    }

    public File getCustomCssFile() {
        return customCssFile;
    }

    public void setCustomCssFile( File customCssFile ) {
        this.customCssFile = customCssFile;
    }

}
