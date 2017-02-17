package com.tngtech.jgiven.maven;

import java.io.File;

import com.tngtech.jgiven.report.AbstractReportConfig;
import com.tngtech.jgiven.report.AbstractReportGenerator;
import com.tngtech.jgiven.report.asciidoc.AsciiDocReportConfig;
import com.tngtech.jgiven.report.asciidoc.AsciiDocReportGenerator;
import com.tngtech.jgiven.report.text.PlainTextReportConfig;
import com.tngtech.jgiven.report.text.PlainTextReportGenerator;
import com.tngtech.jgiven.report.html5.Html5ReportConfig;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;

import com.tngtech.jgiven.report.ReportGenerator;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

@Mojo( name = "report", defaultPhase = LifecyclePhase.VERIFY )
public class JGivenReportMojo extends AbstractMojo {

    /**
     * Directory where the reports are generated to
     */
    @Parameter( defaultValue = "${project.build.directory}/jgiven-reports/html" )
    private File outputDirectory;

    /**
     * Directory to read the JSON report files from
     */
    @Parameter( defaultValue = "${project.build.directory}/jgiven-reports/json" )
    private File sourceDirectory;

    /**
     * Custom CSS file to customize the HTML report
     */
    @Parameter( defaultValue = "src/test/resources/jgiven/custom.css" )
    private File customCssFile;

    /**
     * Custom JS file to customize the HTML report
     */
    @Parameter( defaultValue = "src/test/resources/jgiven/custom.js" )
    private File customJsFile;

    /**
     * The format of the generated report. Can be html or text
     */
    @Parameter( defaultValue = "html" )
    private String format;

    /**
     * The title of the generated report.
     */
    @Parameter( defaultValue = "JGiven Report" )
    private String title;

    /**
     * Whether or not to exclude empty scenarios, i.e. scenarios without any steps,
     * from the report
     */
    @Parameter( defaultValue = "false" )
    boolean excludeEmptyScenarios;

    /**
     * Whether to show thumbnails for image attachments, otherwise a clip-icon is shown
     */
    @Parameter( defaultValue = "true" )
    boolean thumbnailsAreShown;

    @Override
    public void execute() throws MojoExecutionException {
        try {
            if( !outputDirectory.exists() && !outputDirectory.mkdirs() ) {
                throw new MojoExecutionException( "Error while trying to create output directory " + outputDirectory );
            }
            getLog().info( "JGiven HTML report source directory: " + sourceDirectory );
            getLog().info( "JGiven HTML report output directory: " + outputDirectory );
            if( customCssFile != null && customCssFile.exists() ) {
                getLog().info( "JGiven HTML report custom CSS file: " + customCssFile );
            }
            if( customJsFile != null && customJsFile.exists() ) {
                getLog().info( "JGiven HTML report custom JS file: " + customJsFile );
            }
            getLog().info( "Generating HTML reports to " + outputDirectory + "..." );

            ReportGenerator.Format parsedFormat = ReportGenerator.Format.fromStringOrNull( format );
            AbstractReportConfig config;
            AbstractReportGenerator generator;

            switch( parsedFormat ) {
                case ASCIIDOC:
                    config = new AsciiDocReportConfig();
                    generator = new AsciiDocReportGenerator();
                    break;
                case TEXT:
                    config = new PlainTextReportConfig();
                    generator = new PlainTextReportGenerator();
                    break;
                case HTML:
                case HTML5:
                default:
                    Html5ReportConfig customConf = new Html5ReportConfig();
                    customConf.setShowThumbnails( thumbnailsAreShown );
                    customConf.setCustomCss( customCssFile );
                    customConf.setCustomJs( customJsFile );
                    config = customConf;
                    generator = ReportGenerator.generateHtml5Report();
                    break;
            }
            config.setTitle( title );
            config.setSourceDir( sourceDirectory );
            config.setTargetDir( outputDirectory );
            config.setExcludeEmptyScenarios( excludeEmptyScenarios );
            generator.generateWithConfig( config );
            getLog().info( "-------------------------------------------------------------------" );
            getLog().info( "Generated JGiven HTML reports to directory " + outputDirectory );
            getLog().info( "-------------------------------------------------------------------" );
        } catch( Exception e ) {
            throw new MojoExecutionException( "Error while trying to generate HTML reports", e );
        }
    }
}
