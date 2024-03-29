package com.tngtech.jgiven.impl;

import com.tngtech.jgiven.config.ConfigValue;
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Helper class to access all system properties to configure JGiven.
 */
public class Config {
    private static final Logger log = LoggerFactory.getLogger(Config.class);
    private static final Config INSTANCE = new Config();

    private static final String TRUE = "true";
    private static final String FALSE = "false";
    private static final String AUTO = "auto";
    private static final String JGIVEN_REPORT_ENABLED = "jgiven.report.enabled";
    public static final String JGIVEN_REPORT_DIR = "jgiven.report.dir";
    private static final String JGIVEN_REPORT_TEXT = "jgiven.report.text";
    private static final String JGIVEN_REPORT_TEXT_COLOR = "jgiven.report.text.color";
    private static final String JGIVEN_FILTER_STACK_TRACE = "jgiven.report.filterStackTrace";
    private static final String JGIVEN_REPORT_DRY_RUN = "jgiven.report.dry-run";
    private static final String JGIVEN_CONFIG_PATH = "jgiven.config.path";
    private static final String JGIVEN_CONFIG_CHARSET = "jgiven.config.charset";

    private final Properties configFileProperties = loadConfigFileProperties();

    public static Config config() {
        return INSTANCE;
    }

    static {
        logDryRunEnabled();
        logReportEnabled();
    }

    static void logDryRunEnabled() {
        if (INSTANCE.dryRun()) {
            log.info("Dry Run enabled.");
        }
    }

    static void logReportEnabled() {
        if (!INSTANCE.isReportEnabled()) {
            log.info("Please note that the report generation is turned off.");
        }
    }

    private Config() {
    }

    private static Properties loadConfigFileProperties() {
        String path = System.getProperty(JGIVEN_CONFIG_PATH, "jgiven.properties");
        String charset = System.getProperty(JGIVEN_CONFIG_CHARSET, "UTF-8");
        Properties properties = new Properties();
        try (Reader reader = Files.newBufferedReader(Paths.get(path), Charset.forName(charset))) {
            properties.load(reader);
        } catch (IOException e) {
            log.debug("config file " + path + " not loaded: " + e.getMessage());
        }
        return properties;
    }

    private String resolveProperty(String name) {
        return resolveProperty(name, null);
    }

    private String resolveProperty(String name, String defaultValue) {
        return System.getProperty(name, configFileProperties.getProperty(name, defaultValue));
    }

    /**
     * Returns the directory set either via a configuration file or a system property.
     * If no value is specified and the surefire test classpath is set, the default maven directory will be used,
     * otherwise a default is returned.
     */
    public Optional<File> getReportDir() {
        String reportDirName = resolveProperty(JGIVEN_REPORT_DIR);
        if (reportDirName == null) {
            if (resolveProperty("surefire.test.class.path") != null) {
                reportDirName = "target/jgiven-reports/json";
                log.info(JGIVEN_REPORT_DIR + " not set, but detected surefire plugin, generating reports to "
                    + reportDirName);
            } else {
                reportDirName = "jgiven-reports";
                log.debug(JGIVEN_REPORT_DIR + " not set, using default value jgiven-reports");
            }
        }

        File reportDir = new File(reportDirName);
        if (reportDir.exists() && !reportDir.isDirectory()) {
            log.warn(reportDirName + " exists but is not a directory. Will not generate JGiven reports.");
            return Optional.empty();
        }

        log.debug("Using folder " + reportDirName + " to store JGiven reports");

        return Optional.of(reportDir);
    }

    public boolean isReportEnabled() {
        return TRUE.equalsIgnoreCase(resolveProperty(JGIVEN_REPORT_ENABLED, TRUE));
    }

    public void setReportEnabled(boolean enabled) {
        System.setProperty(JGIVEN_REPORT_ENABLED, "" + enabled);
    }

    public ConfigValue textColorEnabled() {
        return ConfigValue.fromString(resolveProperty(JGIVEN_REPORT_TEXT_COLOR, AUTO));
    }

    public boolean textReport() {
        return TRUE.equalsIgnoreCase(resolveProperty(JGIVEN_REPORT_TEXT, TRUE));
    }

    public void setTextReport(boolean b) {
        System.setProperty(JGIVEN_REPORT_TEXT, "" + b);
    }

    public boolean filterStackTrace() {
        return TRUE.equalsIgnoreCase(resolveProperty(JGIVEN_FILTER_STACK_TRACE, TRUE));
    }

    public void setReportDir(File reportDir) {
        System.setProperty(JGIVEN_REPORT_DIR, reportDir.getAbsolutePath());
    }

    public boolean dryRun() {
        return TRUE.equals(System.getProperty(JGIVEN_REPORT_DRY_RUN, FALSE));
    }
}
