package com.tngtech.jgiven.report.html5;

import com.tngtech.jgiven.report.AbstractReportConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.io.File;

public class Html5ReportConfig extends AbstractReportConfig {
    private static final Logger log = LoggerFactory.getLogger( Html5ReportConfig.class );

    File customCss;
    File customJs;
    boolean showThumbnails;

    Html5ReportConfig( Map<String, Object> configMap ) {
        super( configMap );
    }

    public Html5ReportConfig() {
        super();
        setShowThumbnails( true );
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
