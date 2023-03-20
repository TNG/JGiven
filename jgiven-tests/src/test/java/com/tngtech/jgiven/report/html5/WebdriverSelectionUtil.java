package com.tngtech.jgiven.report.html5;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.chromium.ChromiumOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

public class WebdriverSelectionUtil {
    private final Logger log = LoggerFactory.getLogger(WebdriverSelectionUtil.class);
    private final List<Supplier<WebDriver>> configuredProvider = Arrays.asList( this::setupFirefoxDriver,
            this::setupChromeDriver, this::setupEdgeDriver);

    public WebDriver setupAnyWebDriver() {
        WebDriver driver = tryAllWebdrivers();
        log.atInfo().addArgument(driver.getClass().getSimpleName()).log("Using web driver '{}'");
        driver.manage().window().setSize(new Dimension(1280, 768));
        return driver;
    }

    private WebDriver tryAllWebdrivers(){
        IllegalStateException noViableDriverException = new IllegalStateException("No viable driver found");
        for( var driverProvider : configuredProvider ) {
            try{
                return driverProvider.get();
            }catch ( Exception e ){
                noViableDriverException.addSuppressed(e);
            }
        }
        throw noViableDriverException;
    }
    private FirefoxDriver setupFirefoxDriver(){
        WebDriverManager.firefoxdriver().setup();
        FirefoxOptions options = new FirefoxOptions();
        options.addArguments("--headless");
        options.addArguments("--disable-gpu");
        options.addArguments("window-size=1280x768");
        return new FirefoxDriver(options);
    }

    private ChromeDriver setupChromeDriver(){
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        setChromiumArguments(options);
        return new ChromeDriver(options);
    }

    private EdgeDriver setupEdgeDriver(){
        WebDriverManager.edgedriver().setup();
        EdgeOptions options = new EdgeOptions();
        setChromiumArguments(options);
        return new EdgeDriver(options);
    }

    private void setChromiumArguments(ChromiumOptions<?> options){
        options.addArguments("--headless");
        options.addArguments("--disable-gpu");
        options.addArguments("window-size=1280x768");
    }

}
