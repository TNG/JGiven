package com.tngtech.jgiven.cucumber.json;

import java.util.List;

public class CucumberFeature extends CucumberElement {
    public List<CucumberScenario> elements;
    public String uri;

    public CucumberFeature() {
        keyword = "Feature";
    }

}
