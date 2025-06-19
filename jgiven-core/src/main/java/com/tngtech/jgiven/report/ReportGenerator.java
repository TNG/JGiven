package com.tngtech.jgiven.report;

import com.tngtech.jgiven.exception.JGivenInstallationException;
import com.tngtech.jgiven.exception.JGivenInternalDefectException;
import com.tngtech.jgiven.report.config.ConfigOptionParser;
import com.tngtech.jgiven.report.text.PlainTextReportGenerator;
import java.util.Arrays;

/**
 *  This is an interface to create a report based on command line flags
 */
public class ReportGenerator {
    private static final String ASCIIDOC_GENERATOR_FQCN = "com.tngtech.jgiven.report.asciidoc.AsciiDocReportGenerator";
	private static final String HTML5_REPORT_GENERATOR_FQCN = "com.tngtech.jgiven.report.html5.Html5ReportGenerator";

	/**
     * to create a custom report, extend this enum with the name of your choice
     */
    public enum Format {
      ASCIIDOC( "asciidoc" ),
      HTML( "html" ),
      HTML5( "html5" ),
      TEXT( "text" );

      private final String formatName;

        Format( String formatName) {
            this.formatName = formatName;
        }

        public static Format fromStringOrNull( String value ) {
            return Arrays.stream(Format.values())
                    .filter(format -> format.formatName.equalsIgnoreCase((value)))
                    .findFirst()
                    .orElse(null);
        }

      public String formatName() {
        return formatName;
      }
    }

    /**
     * Starts the respective report (default is HTML5)
     */
    public void generate( String... args ) {
        var format = ConfigOptionParser.getFormat( args );
        switch( format ) {
            case ASCIIDOC:
                loadReportGenerator(ASCIIDOC_GENERATOR_FQCN).generateFromCommandLine(args);
                break;
            case TEXT:
                new PlainTextReportGenerator().generateFromCommandLine( args );
                break;
            case HTML, HTML5:
            default:
                ReportGenerator.loadReportGenerator(HTML5_REPORT_GENERATOR_FQCN).generateFromCommandLine(args);
                break;
        }
    }

    /**
	 * Searches the Html5ReportGenerator in Java path and instantiates it
	 */
	public static AbstractReportGenerator loadHtml5ReportGenerator() {
		return loadReportGenerator( HTML5_REPORT_GENERATOR_FQCN );
	}

	private static AbstractReportGenerator loadReportGenerator(String fqcn) {
		try {
			Class<?> aClass = ReportGenerator.class.getClassLoader().loadClass(fqcn);
            return (AbstractReportGenerator) aClass.getDeclaredConstructor().newInstance();
        } catch( ClassNotFoundException e ) {
            throw new JGivenInstallationException( "The JGiven HTML5 Report Generator seems not to be on the classpath.\n"
                    + "Ensure that you have a dependency to jgiven-html5-report." );
        } catch( Exception e ) {
            throw new JGivenInternalDefectException( "The HTML5 Report Generator could not be instantiated.", e );
        }
	}

    public static void main( String... args ) {
        new ReportGenerator().generate( args );
    }
}
