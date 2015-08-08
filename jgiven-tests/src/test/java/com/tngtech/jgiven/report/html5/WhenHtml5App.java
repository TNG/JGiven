package com.tngtech.jgiven.report.html5;

import java.io.File;
import java.net.MalformedURLException;
import java.util.List;

import org.openqa.selenium.By;

import com.tngtech.jgiven.annotation.AfterStage;
import com.tngtech.jgiven.annotation.ExpectedScenarioState;
import com.tngtech.jgiven.impl.util.WordUtil;
import com.tngtech.jgiven.report.model.ReportModel;
import com.tngtech.jgiven.report.model.ScenarioModel;

public class WhenHtml5App<SELF extends WhenHtml5App<?>> extends Html5AppStage<SELF> {

    @ExpectedScenarioState
    protected List<ReportModel> reportModels;

    public SELF the_index_page_is_opened() throws MalformedURLException {
        url_$_is_opened( "" );
        return self();
    }

    public SELF the_All_Scenarios_page_is_opened() throws MalformedURLException {
        url_$_is_opened( "#/all" );
        return self();
    }

    private SELF url_$_is_opened( String s ) throws MalformedURLException {
        File file = new File( targetReportDir, "index.html" );
        String url = file.toURI().toURL().toString() + s;
        System.out.println( url );
        webDriver.get( url );
        return self();
    }

    public SELF the_tag_with_name_$_is_clicked( String tagName ) {
        findTagWithName( tagName ).click();
        return self();
    }

    public SELF scenario_$_is_expanded( int scenarioNr ) {
        ScenarioModel scenarioModel = getScenarioModel( scenarioNr );
        webDriver.findElement( By.xpath( "//h4[contains(text(),'" +
                WordUtil.capitalize( scenarioModel.getDescription() ) + "')]" ) )
            .click();
        return self();
    }

    private ScenarioModel getScenarioModel( int scenarioNr ) {
        return reportModels.get( 0 ).getScenarios().get( scenarioNr - 1 );
    }

    public SELF the_page_of_scenario_$_is_opened( int scenarioNr ) throws MalformedURLException {

        ScenarioModel scenarioModel = getScenarioModel( scenarioNr );
        url_$_is_opened( "#/scenario/"
                + scenarioModel.getClassName()
                + "/" + scenarioModel.getTestMethodName() );
        return self();
    }

    @AfterStage
    public void takeScreenshotAfterStage() {
        takeScreenshot();

    }
}
