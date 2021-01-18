package com.tngtech.jgiven.example.selenium;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import com.tngtech.jgiven.annotation.ProvidedScenarioState;
import com.tngtech.jgiven.junit.SimpleScenarioTest;

public class IndexHtmlTest extends SimpleScenarioTest<IndexStage> {

    @ProvidedScenarioState
    static WebDriver webDriver;

    @BeforeClass
    public static void createWebDriver() {
        WebDriverManager.chromedriver().setup();

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        options.addArguments("--disable-gpu");
        options.addArguments("window-size=1280x768");
        webDriver = new ChromeDriver(); }

    @AfterClass
    public static void closeWebDriver() {
        webDriver.close();
    }

    @Test
    public void initial_title() {
        given().the_initial_page();
        then().the_title_is( "Hello World" );
    }

    @Test
    public void title_can_change_by_pressing_a_button() {
        given().the_initial_page();
        when().clicking_the_ClickMe_button();
        then().the_title_is( "Hello JGiven!" );
    }
}