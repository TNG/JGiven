package com.tngtech.jgiven.cucumber.json;

import java.util.List;

public class CucumberScenario extends CucumberElement {
    public List<CucumberTag> tags;
    public List<CucumberStep> steps;
    public List<CucumberExample> examples;

    public CucumberScenario() {
        keyword = "Scenario";
        type = "scenario";
    }
}
