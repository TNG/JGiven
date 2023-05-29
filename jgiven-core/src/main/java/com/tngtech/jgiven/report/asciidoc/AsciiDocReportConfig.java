package com.tngtech.jgiven.report.asciidoc;

import com.tngtech.jgiven.report.AbstractReportConfig;
import com.tngtech.jgiven.report.config.ConfigOption;
import java.util.List;
import java.util.Map;

/**
 * Configuration for the AsciiDoc report.
 * The configMap should always be in a valid state and have all possible flags,
 * except the optional ones without a default (like --help).
 */
public class AsciiDocReportConfig extends AbstractReportConfig {

    public AsciiDocReportConfig(String... args) {
        super(args);
    }

    public AsciiDocReportConfig() {
        super();
    }

    public void useConfigMap(Map<String, Object> configMap) {

    }

    public void additionalConfigOptions(List<ConfigOption> configOptions) {

    }

}
