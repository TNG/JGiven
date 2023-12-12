package com.tngtech.jgiven.maven;

import com.tngtech.jgiven.report.AbstractReportConfig;
import com.tngtech.jgiven.report.AbstractReportGenerator;
import com.tngtech.jgiven.report.ReportGenerator;
import com.tngtech.jgiven.report.asciidoc.AsciiDocReportConfig;
import com.tngtech.jgiven.report.asciidoc.AsciiDocReportGenerator;
import com.tngtech.jgiven.report.html5.Html5ReportConfig;
import com.tngtech.jgiven.report.text.PlainTextReportConfig;
import com.tngtech.jgiven.report.text.PlainTextReportGenerator;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;

@Mojo(name = "report", defaultPhase = LifecyclePhase.VERIFY, threadSafe = true)
public class JGivenReportMojo extends AbstractMojo {

    public static final String JGIVEN = "JGiven ";

    public JGivenReportMojo(){}

    public JGivenReportMojo(File buildDirectory, File outputDirectory, File sourceDirectory, File customCssFile, File customJsFile, String format, String title, boolean excludeEmptyScenarios, boolean thumbnailsAreShown) {
        this.buildDirectory = buildDirectory;
        this.outputDirectory = outputDirectory;
        this.sourceDirectory = sourceDirectory;
        this.customCssFile = customCssFile;
        this.customJsFile = customJsFile;
        this.format = format;
        this.title = title;
        this.excludeEmptyScenarios = excludeEmptyScenarios;
        this.thumbnailsAreShown = thumbnailsAreShown;
    }

    @Parameter(defaultValue = "${project.build.directory}", readonly = true, required = true)
    private File buildDirectory;
    /**
     * Directory where the reports are generated to
     */
    @Parameter
    private File outputDirectory;

    /**
     * Directory to read the JSON report files from
     */
    @Parameter(defaultValue = "${project.build.directory}/jgiven-reports/json")
    private File sourceDirectory;

    /**
     * Custom CSS file to customize the HTML report
     */
    @Parameter(defaultValue = "src/test/resources/jgiven/custom.css")
    private File customCssFile;

    /**
     * Custom JS file to customize the HTML report
     */
    @Parameter(defaultValue = "src/test/resources/jgiven/custom.js")
    private File customJsFile;

    /**
     * The format of the generated report. Can be html, asciidoc or text
     */
    @Parameter(defaultValue = "html")
    private String format;

    /**
     * The title of the generated report.
     */
    @Parameter(defaultValue = "JGiven Report")
    private String title;

    /**
     * Whether or not to exclude empty scenarios, i.e. scenarios without any steps,
     * from the report
     */
    @Parameter(defaultValue = "false")
    boolean excludeEmptyScenarios;

    /**
     * Whether to show thumbnails for image attachments, otherwise a clip-icon is shown
     */
    @Parameter(defaultValue = "true")
    boolean thumbnailsAreShown;

    @Override
    public void execute() throws MojoExecutionException {
        ReportGenerator.Format parsedFormat = ReportGenerator.Format.fromStringOrNull(format);
        selectOutputDirectory(parsedFormat);
        try {
            if (!outputDirectory.exists() && !outputDirectory.mkdirs()) {
                throw new MojoExecutionException("Error while trying to create output directory " + outputDirectory);
            }
            getLog().info(JGIVEN + parsedFormat + " report source directory: " + sourceDirectory);
            getLog().info(JGIVEN + parsedFormat + " report output directory: " + outputDirectory);
            if (customCssFile != null && customCssFile.exists()) {
                getLog().info(JGIVEN + parsedFormat + " report custom CSS file: " + customCssFile);
            }
            if (customJsFile != null && customJsFile.exists()) {
                getLog().info(JGIVEN + parsedFormat + " report custom JS file: " + customJsFile);
            }
            getLog().info("Generating " + parsedFormat + " reports to " + outputDirectory + "...");

            AbstractReportConfig config;
            AbstractReportGenerator generator;

            switch (parsedFormat) {
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
                    customConf.setShowThumbnails(thumbnailsAreShown);
                    customConf.setCustomCss(customCssFile);
                    customConf.setCustomJs(customJsFile);
                    config = customConf;
                    generator = ReportGenerator.generateHtml5Report();
                    break;
            }
            config.setTitle(title);
            config.setSourceDir(sourceDirectory);
            config.setTargetDir(outputDirectory);
            config.setExcludeEmptyScenarios(excludeEmptyScenarios);
            generator.generateWithConfig(config);
            getLog().info("-------------------------------------------------------------------");
            getLog().info("Generated JGiven " + parsedFormat + " reports to directory " + outputDirectory);
            getLog().info("-------------------------------------------------------------------");
        } catch (Exception e) {
            throw new MojoExecutionException("Error while trying to generate " + parsedFormat + " reports", e);
        }
    }

    private void selectOutputDirectory(ReportGenerator.Format parsedFormat) {
        if (this.outputDirectory != null) {
            return;
        }
        switch (parsedFormat) {
            case HTML:
            case HTML5:
                this.outputDirectory = new File(buildDirectory, "/jgiven-reports/html");
                break;
            case ASCIIDOC:
                this.outputDirectory = new File(buildDirectory, "/jgiven-reports/asciidoc");
                break;
            case TEXT:
                this.outputDirectory = new File(buildDirectory, "/jgiven-reports/text");
        }
    }

}
