package com.tngtech.jgiven.maven;

import java.io.File;
import java.io.IOException;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;

import com.tngtech.jgiven.report.html.HtmlReportGenerator;

/**
 * @goal report
 * @phase test
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
            HtmlReportGenerator generator = new HtmlReportGenerator();
            generator.toDir = outputDirectory;
            generator.sourceDir = sourceDirectory;
            generator.frames = true;
            generator.customCssFile = customCssFile;
            generator.generate();
            getLog().info( "-------------------------------------------------------------------" );
            getLog().info( "Generated JGiven HTML reports to directory " + outputDirectory );
            getLog().info( "-------------------------------------------------------------------" );
        } catch( IOException e ) {
            throw new MojoExecutionException( "Error while trying to generate HTML reports", e );
        }
    }
}
