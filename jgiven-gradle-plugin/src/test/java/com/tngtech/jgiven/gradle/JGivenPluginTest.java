package com.tngtech.jgiven.gradle;

import static org.assertj.core.api.Assertions.assertThat;

import com.google.common.base.Charsets;
import com.google.common.io.CharSink;
import com.google.common.io.FileWriteMode;
import com.google.common.io.Files;
import com.google.common.io.Resources;
import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.annotation.BeforeStage;
import com.tngtech.jgiven.annotation.ExpectedScenarioState;
import com.tngtech.jgiven.annotation.ProvidedScenarioState;
import com.tngtech.jgiven.annotation.Quoted;
import com.tngtech.jgiven.annotation.ScenarioState;
import com.tngtech.jgiven.junit5.JGivenExtension;
import com.tngtech.jgiven.junit5.ScenarioTest;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.gradle.testkit.runner.BuildResult;
import org.gradle.testkit.runner.BuildTask;
import org.gradle.testkit.runner.GradleRunner;
import org.gradle.testkit.runner.TaskOutcome;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;

@ExtendWith(JGivenExtension.class)
public class JGivenPluginTest extends
    ScenarioTest<JGivenPluginTest.Given, JGivenPluginTest.When, JGivenPluginTest.Then> {

    @ScenarioState
    public final FileWrapper testProjectDir = new FileWrapper();

    @BeforeEach
    public void createTempFolder(@TempDir File testProjectDir) {
        this.testProjectDir.set(testProjectDir);
    }

    @Test
    public void plugin_does_not_force_creation_of_buildDir_during_configuration() throws Exception {
        given().the_plugin_is_applied();

        when().a_build().with().the_task("tasks").is_successful();

        then().the_build_directory_has_not_been_created();
    }

    @Test
    public void plugin_can_build_with_an_empty_project() throws Exception {
        given().the_plugin_is_applied();

        when().a_build().with().the_task("build").is_successful();

        then().all_tasks_have_been_executed();
    }

    @Test
    public void jgiven_test_results_are_written_to_the_buildDir() throws IOException {
        given()
            .the_plugin_is_applied()
            .and().there_are_JGiven_tests();

        when()
            .a_build().with().the_task("test").is_successful();

        then()
            .the_JGiven_results_are_written_to("build/jgiven-results/test");

    }

    @Test
    public void jgiven_test_results_can_be_written_to_custom_directory() throws IOException {
        String customResultsDir = "build/jgiven-jsons";
        given()
            .the_plugin_is_applied()
            .and().the_results_dir_is_set_to(customResultsDir)
            .and().there_are_JGiven_tests();

        when()
            .a_build().with().the_task("test").is_successful();

        then()
            .the_JGiven_results_are_written_to(customResultsDir);

    }

    @Test
    public void jgiven_html_report_is_generated() throws IOException {
        given()
            .the_plugin_is_applied()
            .and().there_are_JGiven_tests();

        when()
            .a_build().with()
            .the_task("test").and()
            .the_task("jgivenTestReport").is_successful();

        then()
            .the_JGiven_html_reports_are_written_to("build/reports/jgiven/test/html");

    }

    @Test
    public void configure_reports() throws IOException {
        given()
            .the_plugin_is_applied()
            .and().there_are_JGiven_tests()
            .and().the_jgiven_report_is_configured_by("reports {\n"
                + "  html {\n"
                + "    required = false\n"
                + "  }\n"
                + "  text {\n"
                + "    required = true\n"
                + "  }\n"
                + "}\n")
        ;

        when()
            .a_build().with()
            .the_task("test").and()
            .the_task("jgivenTestReport").is_successful();

        then()
            .the_JGiven_reports_are_not_written_to("build/reports/jgiven/test/html")
            .and().the_JGiven_text_reports_are_written_to("build/reports/jgiven/test/text");

    }

    @Test
    public void configure_html_report() throws IOException {
        given()
            .the_plugin_is_applied()
            .and().there_are_JGiven_tests()
            .and().the_jgiven_report_is_configured_by("reports {\n"
                + "  html {\n"
                + "    required = true\n"
                + "    title = 'JGiven Gradle Plugin'\n"
                + "  }\n"
                + "}\n")
        ;

        when()
            .a_build().with()
            .the_task("test").and()
            .the_task("jgivenTestReport").is_successful();

        then()
            .the_JGiven_html_reports_are_written_to("build/reports/jgiven/test/html");

    }

    @Test
    void no_jgiven_results_no_report() throws IOException {

        given()
            .the_plugin_is_applied()
            .and().there_are_JGiven_tests();

        when()
            .a_build().with()
            .the_task("jgivenTestReport").is_successful();

        then()
            .the_JGiven_reports_are_not_written_to("build/reports/jgiven/test/html");

    }

    @SuppressWarnings("UnusedReturnValue")
    static class Given extends Stage<Given> {
        @ExpectedScenarioState
        private FileWrapper testProjectDir;

        CharSink buildFile;

        @BeforeStage
        private void setup() {
            File actualBuildFile = new File(testProjectDir.file, "build.gradle");
            buildFile = Files.asCharSink(actualBuildFile, Charsets.UTF_8, FileWriteMode.APPEND);
        }

        Given the_plugin_is_applied() throws IOException {
            buildFile.write("plugins { id 'java'; id 'com.tngtech.jgiven.gradle-plugin' }\n");
            buildFile.write("repositories { mavenCentral() }\n");
            buildFile.write("dependencies { testImplementation 'com.tngtech.jgiven:jgiven-junit:1.2.5' }\n");
            buildFile.write("dependencies { testImplementation 'junit:junit:4.13.2' }\n");
            return self();
        }

        Given there_are_JGiven_tests() throws IOException {
            new File(testProjectDir.get(), "src/test/java").mkdirs();
            File scenario = new File(testProjectDir.get(), "src/test/java/SimpleScenario.java");

            Files.write(Resources.toByteArray(Resources.getResource("SimpleScenario.java")), scenario);

            return self();
        }

        Given the_results_dir_is_set_to(String dir) throws IOException {
            buildFile.write("test { jgiven { resultsDir = file('" + dir + "') } }\n");
            return self();
        }

        Given the_jgiven_report_is_configured_by(String configuration) throws IOException {
            buildFile.write("jgivenTestReport { " + configuration + " } ");
            return self();
        }
    }

    @SuppressWarnings("UnusedReturnValue")
    static class When extends Stage<When> {
        @ExpectedScenarioState
        private FileWrapper testProjectDir;
        @ProvidedScenarioState
        private BuildResult result;
        @ProvidedScenarioState
        List<String> tasks = new ArrayList<>();

        When the_task(@Quoted String task) {
            tasks.add(task);
            return self();
        }

        When a_build() {
            return self();
        }

        When is_successful() {
            result = GradleRunner.create()
                .withProjectDir(testProjectDir.get())
                .withArguments(tasks)
                .withPluginClasspath()
                .build();
            return self();
        }
    }

    @SuppressWarnings("UnusedReturnValue")
    static class Then extends Stage<Then> {
        @ExpectedScenarioState
        private BuildResult result;
        @ExpectedScenarioState
        private FileWrapper testProjectDir;
        @ScenarioState
        List<String> tasks;

        Then the_build_directory_has_not_been_created() {
            assertThat(new File(testProjectDir.get(), "build")).doesNotExist();
            return self();
        }

        Then all_tasks_have_been_executed() {
            for (String task : tasks) {
                TaskOutcome outcome = Optional.ofNullable(result.task(":" + task))
                    .map(BuildTask::getOutcome)
                    .orElse(TaskOutcome.FAILED);
                assertThat(outcome).isEqualTo(TaskOutcome.SUCCESS);
            }
            return self();
        }

        Then the_JGiven_results_are_written_to(String destination) {
            assertDirectoryContainsFilesOfType(destination, "json");
            return self();
        }

        Then the_JGiven_html_reports_are_written_to(String reportDirectory) {
            assertDirectoryContainsFilesOfType(reportDirectory, "html");
            return self();
        }

        private void assertDirectoryContainsFilesOfType(String destination, final String extension) {
            File destinationDir = new File(testProjectDir.get(), destination);
            assertThat(destinationDir).isDirectory();
            assertThat(destinationDir
                .listFiles((dir, name) -> name.endsWith("." + extension)))
                .isNotEmpty();
        }

        Then the_JGiven_reports_are_not_written_to(String destination) {
            File destinationDir = new File(testProjectDir.get(), destination);
            if (destinationDir.exists()) {
                assertThat(destinationDir.listFiles()).isEmpty();
            }
            return self();
        }

        Then the_JGiven_text_reports_are_written_to(String destination) {
            assertDirectoryContainsFilesOfType(destination, "feature");
            return self();
        }
    }

    private static class FileWrapper {
        private File file;

        public File get() {
            return file;
        }

        public void set(File file) {
            this.file = file;
        }
    }
}
