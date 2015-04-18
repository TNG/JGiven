package com.tngtech.jgiven.cucumber;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

import com.tngtech.jgiven.cucumber.json.CucumberFeature;
import com.tngtech.jgiven.cucumber.json.CucumberJsonReport;
import com.tngtech.jgiven.cucumber.json.CucumberScenario;

public class ConverterTest {
    String RESOURCE_DIR = "src/test/resources";

    File resourceDir = new File( RESOURCE_DIR ).exists() ? new File( RESOURCE_DIR ) : new File( "jgiven-cucumber", RESOURCE_DIR );

    @Test
    public void testConversion() throws IOException {
        CucumberJsonReport report = CucumberJsonReport.fromFile( new File( resourceDir, "cucumber-report.json" ) );
        assertThat( report ).isNotNull();
        assertThat( report.features ).hasSize( 1 );

        CucumberFeature feature = report.features.get( 0 );
        assertThat( feature.keyword ).isEqualTo( "Feature" );
        assertThat( feature.id ).isEqualTo( "cucumber-jgiven-integration" );
        assertThat( feature.name ).isEqualTo( "Cucumber JGiven integration" );
        assertThat( feature.uri ).isEqualTo( "com/tngtech/jgiven/cucumber/someexample.feature" );

        assertThat( feature.elements ).hasSize( 1 );
        CucumberScenario scenario = feature.elements.get( 0 );
        assertThat( scenario.keyword ).isEqualTo( "Scenario" );
        assertThat( scenario.name ).isEqualTo( "Just a failing scenario" );
    }

}
