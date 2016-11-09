package com.tngtech.jgiven.gradle;

import com.tngtech.jgiven.relocated.guava.base.Charsets;
import com.tngtech.jgiven.relocated.guava.io.Files;
import com.tngtech.jgiven.relocated.guava.io.Resources;
import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.annotation.BeforeStage;
import com.tngtech.jgiven.annotation.ExpectedScenarioState;
import com.tngtech.jgiven.annotation.ProvidedScenarioState;
import com.tngtech.jgiven.annotation.Quoted;
import com.tngtech.jgiven.annotation.ScenarioState;
import com.tngtech.jgiven.junit.ScenarioTest;
import org.gradle.testkit.runner.BuildResult;
import org.gradle.testkit.runner.GradleRunner;
import org.gradle.testkit.runner.TaskOutcome;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class JGivenPluginTest extends ScenarioTest<JGivenPluginTest.Given, JGivenPluginTest.When, JGivenPluginTest.Then> {
    @Rule
    @ScenarioState
    public final TemporaryFolder testProjectDir = new TemporaryFolder();

    @Test
    public void plugin_does_not_force_creation_of_buildDir_during_configuration() throws Exception {
        given().the_plugin_is_applied();

        when().a_build().with().the_task( "tasks" ).is_successful();

        then().the_build_directory_has_not_been_created();
    }

    @Test
    public void plugin_can_build_with_an_empty_project() throws Exception {
        given().the_plugin_is_applied();

        when().a_build().with().the_task( "build" ).is_successful();

        then().all_tasks_have_been_executed();
    }

    @Test
    public void jgiven_test_results_are_written_to_the_buildDir() throws IOException {
        given()
                .the_plugin_is_applied()
                .and().there_are_JGiven_tests();

        when()
                .a_build().with().the_task( "test" ).is_successful();

        then()
                .the_JGiven_results_are_written_to( "build/jgiven-results/test" );

    }

    @Test
    public void jgiven_test_results_can_be_written_to_custom_directory() throws IOException {
        String customResultsDir = "build/jgiven-jsons";
        given()
                .the_plugin_is_applied()
                .and().the_results_dir_is_set_to( customResultsDir )
                .and().there_are_JGiven_tests();

        when()
                .a_build().with().the_task( "test" ).is_successful();

        then()
                .the_JGiven_results_are_written_to( customResultsDir );

    }

    @Test
    public void jgiven_html_report_is_generated() throws IOException {
        given()
                .the_plugin_is_applied()
                .and().there_are_JGiven_tests();

        when()
                .a_build().with()
                .the_task( "test" ).and()
                .the_task( "jgivenTestReport" ).is_successful();

        then()
                .the_JGiven_html_reports_are_written_to( "build/reports/jgiven/test/html" );

    }

    @Test
    public void configure_reports() throws IOException {
        given()
                .the_plugin_is_applied()
                .and().there_are_JGiven_tests()
                .and().the_jgiven_report_is_configured_by("reports {\n"
                + "  html {\n"
                + "    enabled = false\n"
                + "  }\n"
                + "  text {\n"
                + "    enabled = true\n"
                + "  }\n"
                + "}\n")
        ;

        when()
                .a_build().with()
                .the_task( "test" ).and()
                .the_task( "jgivenTestReport" ).is_successful();

        then()
                .the_JGiven_reports_are_not_written_to( "build/reports/jgiven/test/html" )
                .and().the_JGiven_text_reports_are_written_to( "build/reports/jgiven/test/text" );

    }

    @Test
    public void configure_html_report() throws IOException {
        given()
                .the_plugin_is_applied()
                .and().there_are_JGiven_tests()
                .and().the_jgiven_report_is_configured_by("reports {\n"
                + "  html {\n"
                + "    enabled = true\n"
                + "    title = 'JGiven Gradle Plugin'\n"
                + "  }\n"
                + "}\n")
        ;

        when()
                .a_build().with()
                .the_task( "test" ).and()
                .the_task( "jgivenTestReport" ).is_successful();

        then()
                .the_JGiven_html_reports_are_written_to( "build/reports/jgiven/test/html" );

    }

    @Test
    public void no_jgiven_results_no_report() throws IOException {

        given()
                .the_plugin_is_applied()
                .and().there_are_JGiven_tests();

        when()
                .a_build().with()
                .the_task( "jgivenTestReport" ).is_successful();

        then()
                .the_JGiven_reports_are_not_written_to( "build/reports/jgiven/test/html" );

    }

    public static class Given extends Stage<Given> {
        @ExpectedScenarioState
        private TemporaryFolder testProjectDir;

        File buildFile;

        @BeforeStage
        private void setup() throws IOException {
            buildFile = testProjectDir.newFile( "build.gradle" );
        }

        public Given the_plugin_is_applied() throws IOException {
            Files.append( "plugins { id 'java'; id 'com.tngtech.jgiven.gradle-plugin' }\n", buildFile, Charsets.UTF_8 );
            Files.append( "repositories { mavenCentral() }\n", buildFile, Charsets.UTF_8 );
            Files.append( "dependencies { testCompile 'com.tngtech.jgiven:jgiven-junit:0.12.1' }\n", buildFile, Charsets.UTF_8 );
            Files.append( "dependencies { testCompile 'junit:junit:4.12' }\n", buildFile, Charsets.UTF_8 );
            return self();
        }

        public Given there_are_JGiven_tests() throws IOException {
            testProjectDir.newFolder( "src", "test", "java" );
            File scenario = testProjectDir.newFile( "src/test/java/SimpleScenario.java" );

            Files.write( Resources.toByteArray( Resources.getResource( "SimpleScenario.java" ) ), scenario );

            return self();
        }

        public Given the_results_dir_is_set_to( String dir ) throws IOException {
            Files.append( "test { jgiven { resultsDir = file('" + dir + "') } }\n", buildFile, Charsets.UTF_8 );
            return self();
        }

        public Given the_jgiven_report_is_configured_by( String configuration ) throws IOException {
            Files.append("jgivenTestReport { " + configuration + " } ", buildFile, Charsets.UTF_8);
            return self();
        }
    }

    public static class When extends Stage<When> {
        @ExpectedScenarioState
        private TemporaryFolder testProjectDir;
        @ProvidedScenarioState
        private BuildResult result;
        @ProvidedScenarioState
        List<String> tasks = new ArrayList<String>();

        public When the_task( @Quoted String task ) {
            tasks.add( task );
            return self();
        }

        public When a_build() {
            return self();
        }

        public When is_successful() {
            result = GradleRunner.create()
                    .withProjectDir( testProjectDir.getRoot() )
                    .withArguments( tasks )
                    .withPluginClasspath()
                    .build();
            return self();
        }
    }

    public static class Then extends Stage<Then> {
        @ExpectedScenarioState
        private BuildResult result;
        @ExpectedScenarioState
        private TemporaryFolder testProjectDir;
        @ScenarioState
        List<String> tasks;

        public Then the_build_directory_has_not_been_created() {
            assertThat( new File( testProjectDir.getRoot(), "build" ) ).doesNotExist();
            return self();
        }

        public Then all_tasks_have_been_executed() {
            for( String task : tasks ) {
                assertThat( result.task( ":" + task ).getOutcome() ).isEqualTo( TaskOutcome.SUCCESS );
            }
            return self();
        }

        public Then the_JGiven_results_are_written_to( String destination ) {
            assertDirectoryContainsFilesOfType( destination, "json" );
            return self();
        }

        public Then the_JGiven_html_reports_are_written_to( String reportDirectory ) {
            assertDirectoryContainsFilesOfType( reportDirectory, "html" );
            return self();
        }

        private void assertDirectoryContainsFilesOfType( String destination, final String extension ) {
            File destinationDir = new File( testProjectDir.getRoot(), destination );
            assertThat( destinationDir ).isDirectory();
            assertThat( destinationDir.listFiles( new FilenameFilter() {
                @Override
                public boolean accept( File dir, String name ) {
                    return name.endsWith( "." + extension );
                }
            } ) ).isNotEmpty();
        }

        public Then the_JGiven_reports_are_not_written_to( String destination ) {
            File destinationDir = new File( testProjectDir.getRoot(), destination );
            if( destinationDir.exists() ) {
                assertThat( destinationDir.listFiles() ).isEmpty();
            }
            return self();
        }

        public Then the_JGiven_text_reports_are_written_to( String destination ) {
            assertDirectoryContainsFilesOfType( destination, "feature" );
            return self();
        }
    }
}