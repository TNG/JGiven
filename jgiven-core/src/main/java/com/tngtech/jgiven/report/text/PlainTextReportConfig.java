package com.tngtech.jgiven.report.text;

import com.tngtech.jgiven.report.AbstractReportConfig;
import com.tngtech.jgiven.report.config.ConfigOption;

import java.util.List;
import java.util.Map;

public class PlainTextReportConfig extends AbstractReportConfig {

    public PlainTextReportConfig(String... args ) {
        super( args );
    }

    public PlainTextReportConfig() {
        super();
    }

    public void useConfigMap( Map<String, Object> configMap ) {

    }

    public void additionalConfigOptions( List<ConfigOption> configOptions ) {

    }


}

