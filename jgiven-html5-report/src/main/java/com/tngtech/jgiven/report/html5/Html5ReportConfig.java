package com.tngtech.jgiven.report.html5;

import com.tngtech.jgiven.report.AbstractReportConfig;
import com.tngtech.jgiven.report.config.CommandLineOptionBuilder;
import com.tngtech.jgiven.report.config.ConfigOption;
import com.tngtech.jgiven.report.config.ConfigOptionBuilder;
import com.tngtech.jgiven.report.config.converter.ToBoolean;
import com.tngtech.jgiven.report.config.converter.ToFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.io.File;

public class Html5ReportConfig extends AbstractReportConfig {
    private static final Logger log = LoggerFactory.getLogger( Html5ReportConfig.class );

    private File customCss;
    private File customJs;
    private boolean showThumbnails;

    Html5ReportConfig( String... args ) {
        super( args );
    }

    public Html5ReportConfig() {
        super();
        setShowThumbnails( true );
    }

    public void additionalConfigOptions( List<ConfigOption> configOptions ) {
        ConfigOption customCss = new ConfigOptionBuilder( "customcss" )
                .setCommandLineOptionWithArgument(
                        new CommandLineOptionBuilder( "--customcss" ).setArgumentDelimiter( "=" ).setVisualPlaceholder( "path" ).build(),
                        new ToFile() )
                .setOptional()
                .setDescription( "path to file" )
                .build();

        ConfigOption customJs = new ConfigOptionBuilder( "customjs" )
                .setCommandLineOptionWithArgument(
                        new CommandLineOptionBuilder( "--customjs" ).setArgumentDelimiter( "=" ).setVisualPlaceholder( "path" ).build(),
                        new ToFile() )
                .setOptional()
                .setDescription( "path to file" )
                .build();

        ConfigOption showThumbnails = new ConfigOptionBuilder( "showThumbnails" )
                .setCommandLineOptionWithArgument(
                        new CommandLineOptionBuilder( "--show-thumbnails" ).setArgumentDelimiter( "=" ).setVisualPlaceholder( "boolean" )
                                .build(),
                        new ToBoolean() )
                .setDefaultWith( true )
                .setDescription( "(default: true)" )
                .build();

        configOptions.addAll( Arrays.asList( customCss, customJs, showThumbnails ) );
    }

    public void useConfigMap( Map<String, Object> configMap ) {
        if( configMap.containsKey( "customcss" ) ) {
            setCustomCss( (File) configMap.get( "customcss" ) );
        }

        if( configMap.containsKey( "customjs" ) ) {
            setCustomJs( (File) configMap.get( "customjs" ) );
        }

        setShowThumbnails( (Boolean) configMap.get( "showThumbnails" ) );
    }

    public File getCustomCss() {
        return customCss;
    }

    public void setCustomCss( File customCss ) {
        this.customCss = customCss;
    }

    public File getCustomJs() {
        return customJs;
    }

    public void setCustomJs( File customJs ) {
        this.customJs = customJs;
    }

    public boolean getShowThumbnails() {
        return showThumbnails;
    }

    public void setShowThumbnails( boolean showThumbnails ) {
        this.showThumbnails = showThumbnails;
    }

}
