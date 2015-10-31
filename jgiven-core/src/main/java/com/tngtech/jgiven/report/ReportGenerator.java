package com.tngtech.jgiven.report;

import static com.tngtech.jgiven.report.ReportGenerator.Format.*;

import java.io.File;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tngtech.jgiven.exception.JGivenInstallationException;
import com.tngtech.jgiven.exception.JGivenInternalDefectException;
import com.tngtech.jgiven.report.asciidoc.AsciiDocReportGenerator;
import com.tngtech.jgiven.report.json.ReportModelReader;
import com.tngtech.jgiven.report.model.CompleteReportModel;
import com.tngtech.jgiven.report.text.PlainTextReportGenerator;

public class ReportGenerator {

    private static final Logger log = LoggerFactory.getLogger( ReportGenerator.class );

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

    private File sourceDirectory = new File( "." );
    private File targetDirectory = new File( "." );
    private Format format = HTML;
    private Config config = new Config();

    public static class Config {
        String title = "JGiven Report";
        File customCssFile;
        File customJsFile;

        /**
         * Whether or not to exclude empty scenarios, i.e. scenarios without any steps,
         * from the report
         */
        boolean excludeEmptyScenarios;

        public void setTitle( String title ) {
            this.title = title;
        }

        public String getTitle() {
            return title;
        }

        public File getCustomCssFile() {
            return customCssFile;
        }

        public void setCustomCssFile( File customCssFile ) {
            this.customCssFile = customCssFile;
        }

        public void setCustomJsFile( File customJsFile ) {
            this.customJsFile = customJsFile;
        }

        public File getCustomJsFile() {
            return customJsFile;
        }

        public void setExcludeEmptyScenarios( boolean excludeEmptyScenarios ) {
            this.excludeEmptyScenarios = excludeEmptyScenarios;
        }

        public boolean getExcludeEmptyScenarios() {
            return excludeEmptyScenarios;
        }
    }

    public static void main( String... args ) throws Exception {
        ReportGenerator generator = new ReportGenerator();
        parseArgs( generator, args );
        generator.generate();
    }

    static void parseArgs( ReportGenerator generator, String... args ) {
        for( String arg : args ) {
            if( arg.equals( "-h" ) || arg.equals( "--help" ) ) {
                printUsageAndExit();
            } else if( arg.startsWith( "--dir=" ) || arg.startsWith( "--sourceDir=" ) ) {
                generator.setSourceDirectory( new File( arg.split( "=" )[1] ) );
                if( arg.startsWith( "--dir=" ) ) {
                    System.err.println( "DEPRECATION WARNING: --dir is deprecated, please use --sourceDir instead" );
                }
            } else if( arg.startsWith( "--todir=" ) || arg.startsWith( "--targetDir=" ) ) {
                generator.setTargetDirectory( new File( arg.split( "=" )[1] ) );
                if( arg.startsWith( "--todir=" ) ) {
                    System.err.println( "DEPRECATION WARNING: --todir is deprecated, please use--targetDir instead" );
                }
            } else if( arg.startsWith( "--customcss=" ) ) {
                generator.config.customCssFile = new File( arg.split( "=" )[1] );
            } else if( arg.startsWith( "--customjs=" ) ) {
                generator.config.customJsFile = new File( arg.split( "=" )[1] );
            } else if( arg.startsWith( "--title=" ) ) {
                generator.config.title = arg.split( "=" )[1];
            } else if( arg.startsWith( "--exclude-empty-scenarios=" ) ) {
                generator.config.excludeEmptyScenarios = Boolean.parseBoolean( arg.split( "=" )[1] );
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
        if( !getTargetDirectory().exists() && !getTargetDirectory().mkdirs() ) {
            log.error( "Could not create target directory " + getTargetDirectory() );
            return;
        }

        CompleteReportModel reportModel = readReportModel();

        if( format == HTML5 || format == HTML ) {
            generateHtml5Report( reportModel );
        } else if( format == TEXT ) {
            new PlainTextReportGenerator().generate( reportModel, getTargetDirectory(), config );
        } else if( format == ASCIIDOC ) {
            new AsciiDocReportGenerator().generate( reportModel, getTargetDirectory(), config );
        }

    }

    CompleteReportModel readReportModel() {
        return new ReportModelReader( config ).readDirectory( getSourceDirectory() );
    }

    private void generateHtml5Report( CompleteReportModel reportModel ) throws IOException {
        AbstractReportGenerator reportGenerator;
        try {
            Class<?> aClass = this.getClass().getClassLoader().loadClass( "com.tngtech.jgiven.report.html5.Html5ReportGenerator" );
            reportGenerator = (AbstractReportGenerator) aClass.newInstance();
        } catch( ClassNotFoundException e ) {
            throw new JGivenInstallationException( "The JGiven HTML5 Report Generator seems not to be on the classpath.\n"
                    + "Ensure that you have a dependency to jgiven-html5-report." );
        } catch( Exception e ) {
            throw new JGivenInternalDefectException( "The HTML5 Report Generator could not be instantiated.", e );
        }

        reportGenerator.generate( reportModel, getTargetDirectory(), config );
    }

    private static void printUsageAndExit() {
        System.err
            .println( "Options: \n"
                    + "  --format=<format>      the format of the report. Either html or text\n"
                    + "  --sourceDir=<dir>      the source directory where the JGiven JSON files are located\n"
                    + "  --targetDir=<dir>      the directory to generate the report to\n"
                    + "  --title=<title>        the title of the report\n"
                    + "  --customcss=<cssfile>  a custom CSS file to customize the HTML report\n"
                    + "  --customjs=<jsfile>    a custom JS file to customize the HTML report\n"
                    + "  --exclude-empty-scenarios=<b>"
                    + "                         exclude scenarios without steps from the report. "
                    + "                         <b> in {true,false}. Default is false.\n"
            ); // NOSONAR
        System.exit( 1 );
    }

    public File getSourceDirectory() {
        return sourceDirectory;
    }

    public void setSourceDirectory( File sourceDirectory ) {
        this.sourceDirectory = sourceDirectory;
    }

    public File getTargetDirectory() {
        return targetDirectory;
    }

    public void setTargetDirectory( File targetDirectory ) {
        this.targetDirectory = targetDirectory;
    }

    public File getCustomCssFile() {
        return config.getCustomCssFile();
    }

    public void setConfig( Config config ) {
        this.config = config;
    }

    public Config getConfig() {
        return this.config;
    }

}
