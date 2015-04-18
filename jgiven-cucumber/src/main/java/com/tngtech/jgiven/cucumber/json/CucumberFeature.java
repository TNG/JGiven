package com.tngtech.jgiven.cucumber.json;

import java.util.List;

import com.google.common.collect.Lists;

public class CucumberFeature extends CucumberElement {
    public List<CucumberScenario> elements = Lists.newArrayList();
    public String uri;

    public CucumberFeature() {
        keyword = "Feature";
    }

    public List<CucumberScenario> getScenarios() {
        return elements;
    }

}
