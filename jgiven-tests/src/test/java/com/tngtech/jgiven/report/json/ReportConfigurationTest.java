package com.tngtech.jgiven.report.json;

import static org.assertj.core.api.Assertions.assertThat;

import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.annotation.AfterScenario;
import com.tngtech.jgiven.annotation.BeforeScenario;
import com.tngtech.jgiven.junit.SimpleScenarioTest;
import com.tngtech.jgiven.tests.TestScenarioRepository;
import com.tngtech.jgiven.tests.TestScenarios;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.JUnitCore;
import org.junit.runner.Request;

public class ReportConfigurationTest extends SimpleScenarioTest<ReportConfigurationTest.ReportConfigurationTestStage> {

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Test
    public void jgiven_report_is_disabled_by_a_system_property() throws IOException {
        File reportFolder = temporaryFolder.newFolder();
        given().a_set_system_property("jgiven.report.dir", getWindowsCompatiblePath(reportFolder))
            .and().a_set_system_property("jgiven.report.enabled", "false")
            .and().a_Test_scenario();

        when().the_test_is_executed_with_junit();

        then().the_report_is_not_written_to(reportFolder);
    }

    @Test
    public void jgiven_report_directory_is_set_via_a_system_property() throws IOException {
        File reportFolder = temporaryFolder.newFolder();
        given().a_set_system_property("jgiven.report.dir", getWindowsCompatiblePath(reportFolder))
            .and().a_set_system_property("jgiven.report.enabled", "true")
            .and().a_Test_scenario();

        when().the_test_is_executed_with_junit();

        then().the_report_is_written_to(reportFolder);
    }

    private String getWindowsCompatiblePath(File file) {
        return file.getAbsolutePath().replace("\\", "/");
    }

    static class ReportConfigurationTestStage extends Stage<ReportConfigurationTestStage> {

        private TestScenarioRepository.TestScenario testScenario;

        private final Map<String, String> systemPropertiesBackup = new HashMap<>();

        @BeforeScenario
        private void createConfigurationFile() {
            a_set_system_property("jgiven.report.dir", null);
        }

        ReportConfigurationTestStage a_set_system_property(String key, String value) {
            String originalValue = System.getProperty(key);
            if (!systemPropertiesBackup.containsKey(key)) {
                systemPropertiesBackup.put(key, originalValue);
            }
            if (value == null) {
                System.clearProperty(key);
            } else {
                System.setProperty(key, value);
            }
            return self();
        }

        ReportConfigurationTestStage a_Test_scenario() {
            testScenario = new TestScenarioRepository.TestScenario(TestScenarios.class, "test_with_tag_annotation");
            return self();
        }

        ReportConfigurationTestStage the_test_is_executed_with_junit() {
            assertThat(testScenario).as("No matching test scenario found").isNotNull();

            JUnitCore junitCore = new JUnitCore();
            junitCore.run(Request.method(testScenario.testClass, testScenario.testMethod));
            return self();
        }

        ReportConfigurationTestStage the_report_is_not_written_to(File file) {
            assertThat(file).isEmptyDirectory();
            return self();
        }

        ReportConfigurationTestStage the_report_is_written_to(File file) {
            assertThat(file).isNotEmptyDirectory();
            return self();
        }

        @AfterScenario
        private void clearSystemProperties() {
            systemPropertiesBackup.entrySet()
                .stream()
                .peek(entry -> System.clearProperty(entry.getKey()))
                .filter(entry -> entry.getValue() != null)
                .forEach(entry -> System.setProperty(entry.getKey(), entry.getValue()));
        }
    }
}
