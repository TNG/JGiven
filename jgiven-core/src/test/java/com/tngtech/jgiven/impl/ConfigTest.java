package com.tngtech.jgiven.impl;

import static org.assertj.core.api.Assertions.assertThat;

import com.google.common.base.Charsets;
import com.google.common.io.CharSink;
import com.google.common.io.FileWriteMode;
import com.google.common.io.Files;
import com.tngtech.jgiven.config.ConfigValue;
import java.io.File;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class ConfigTest {

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    private final Map<String, String> systemPropertiesBackup = new HashMap<>();
    private CharSink jgivenConfig;

    @Before
    public void setupPropertiesFile() throws Exception {
        File configFile = temporaryFolder.newFile();
        jgivenConfig = Files.asCharSink(configFile, Charsets.UTF_8, FileWriteMode.APPEND);
        setSystemProperty("jgiven.config.path", configFile.getAbsolutePath());
        setSystemProperty("jgiven.report.dir", null);
    }

    @Test
    public void configValuesHaveDefaults() throws Exception {
        Config underTest = createNewTestInstance();

        assertThat(underTest.isReportEnabled()).isTrue();
        assertThat(underTest.getReportDir()).get().extracting(File::getPath).isEqualTo("jgiven-reports");
        assertThat(underTest.textColorEnabled()).extracting(Enum::name).isEqualTo("AUTO");
        assertThat(underTest.filterStackTrace()).isTrue();
    }

    @Test
    public void configFileValuesAreRecognized() throws Exception {
        File reportPath = temporaryFolder.newFolder();
        jgivenConfig.write("jgiven.report.enabled=false\n");
        jgivenConfig.write("jgiven.report.dir="
            + reportPath.getAbsolutePath().replace("\\", "/") + "\n");
        jgivenConfig.write("jgiven.report.text=false\n");
        jgivenConfig.write("jgiven.report.text.color=true\n");
        jgivenConfig.write("jgiven.report.filterStackTrace=false\n");

        Config underTest = createNewTestInstance();

        assertThat(underTest.isReportEnabled()).isFalse();
        assertThat(underTest.getReportDir()).contains(reportPath);
        assertThat(underTest.textReport()).isFalse();
        assertThat(underTest.textColorEnabled()).isEqualTo(ConfigValue.TRUE);
        assertThat(underTest.filterStackTrace()).isFalse();
    }

    @Test
    public void testCommandLinePropertiesTakePrecedenceOverConfigFile() throws Exception {
        jgivenConfig.write("jgiven.report.enabled=false\n");
        setSystemProperty("jgiven.report.enabled", "true");

        Config underTest = createNewTestInstance();

        assertThat(underTest.isReportEnabled()).isTrue();
    }

    @After
    public void cleanupSystemProperties() {
        systemPropertiesBackup.entrySet()
            .stream()
            .peek(entry -> System.clearProperty(entry.getKey()))
            .filter(entry -> entry.getValue() != null)
            .forEach(entry -> System.setProperty(entry.getKey(), entry.getValue()));
    }

    private static Config createNewTestInstance() throws Exception {
        Constructor<Config> constructor = Config.class.getDeclaredConstructor();
        try {
            constructor.setAccessible(true);
            return constructor.newInstance();
        } finally {
            constructor.setAccessible(false);
        }
    }

    private void setSystemProperty(String key, String value) {
        String originalValue = System.getProperty(key);
        systemPropertiesBackup.put(key, originalValue);
        if (value == null) {
            System.clearProperty(key);
        } else {
            System.setProperty(key, value);
        }
    }
}
