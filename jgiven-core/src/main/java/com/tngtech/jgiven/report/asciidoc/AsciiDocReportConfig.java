package com.tngtech.jgiven.report.asciidoc;

import com.tngtech.jgiven.report.AbstractReportConfig;
import com.tngtech.jgiven.report.config.ConfigOption;

import java.util.List;
import java.util.Map;

public class AsciiDocReportConfig extends AbstractReportConfig {

    public AsciiDocReportConfig( String... args ) {
        super( args );
    }

    public AsciiDocReportConfig() {
        super();
    }

    public void useConfigMap( Map<String, Object> configMap ) {

    }

    public void additionalConfigOptions( List<ConfigOption> configOptions ) {

    }

}
