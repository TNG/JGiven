package com.tngtech.jgiven.report.html5;

import java.io.File;
import java.net.MalformedURLException;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.phantomjs.PhantomJSDriver;

import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.annotation.AfterScenario;
import com.tngtech.jgiven.annotation.ProvidedScenarioState;

public class WhenHtml5Report<SELF extends WhenHtml5Report<?>> extends Stage<SELF> {

    @ProvidedScenarioState
    protected WebDriver webDriver = new PhantomJSDriver();

    @ProvidedScenarioState
    protected File targetReportDir;

    public SELF the_index_page_is_opened() throws MalformedURLException {
        url_$_is_opened( "" );
        return self();
    }

    @AfterScenario
    protected void closeWebDriver() {
        webDriver.close();
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
        List<WebElement> tags = webDriver.findElements( By.className( "tag" ) );
        for( WebElement element : tags ) {
            WebElement a = element.findElement( By.linkText( tagName ) );
            if( a != null ) {
                a.click();
                break;
            }
        }
        return self();
    }
}
