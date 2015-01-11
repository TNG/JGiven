package com.tngtech.jgiven.maven;

import java.io.File;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;

import com.tngtech.jgiven.report.ReportGenerator;

/**
 * @goal report
 * @phase verify
 */
public class JGivenReportMojo extends AbstractMojo {

    /**
     * Directory where the reports are generated to
     * @parameter expression="${project.build.directory}/jgiven-reports/html"
     */
    private File outputDirectory;

    /**
     * Directory to read the JSON report files from
     * @parameter expression="${project.build.directory}/jgiven-reports/json"
     */
    private File sourceDirectory;

    /**
     * Custom CSS file
     * @parameter expression="src/test/resources/jgiven/custom.css"
     */
    private File customCssFile;

    /**
     * The format of the generated report. Can be one of html, html5, and text
     * @parameter expression="html"
     */
    private String format;

    @Override
    public void execute() throws MojoExecutionException {
        try {
            if( !outputDirectory.exists() && !outputDirectory.mkdirs() ) {
                throw new MojoExecutionException( "Error while trying to create output directory " + outputDirectory );
            }
            getLog().info( "JGiven HTML report source directory: " + sourceDirectory );
            getLog().info( "JGiven HTML report output directory: " + outputDirectory );
            if( customCssFile != null ) {
                getLog().info( "JGiven HTML report custom CSS file: " + customCssFile );
            }
            getLog().info( "Generating HTML reports to " + outputDirectory + "..." );
            ReportGenerator generator = new ReportGenerator();
            generator.setTargetDirectory(outputDirectory);
            generator.setSourceDirectory(sourceDirectory);
            generator.setFormat( ReportGenerator.Format.fromStringOrNull( format ) );
            generator.setCustomCssFile( customCssFile );
            generator.generate();
            getLog().info( "-------------------------------------------------------------------" );
            getLog().info( "Generated JGiven HTML reports to directory " + outputDirectory );
            getLog().info( "-------------------------------------------------------------------" );
        } catch( Exception e ) {
            throw new MojoExecutionException( "Error while trying to generate HTML reports", e );
        }
    }
}
