package com.tngtech.jgiven.testng;

import com.tngtech.jgiven.impl.ScenarioBase;

public class ScenarioHolder {
    private final ThreadLocal<ScenarioBase> scenario = new ThreadLocal<ScenarioBase>();

    private static final ScenarioHolder INSTANCE = new ScenarioHolder();

    public static ScenarioHolder get() {
        return INSTANCE;
    }

    public ScenarioBase getScenarioOfCurrentThread() {
        return scenario.get();
    }

    public void setScenarioOfCurrentThread(ScenarioBase scenarioBase) {
        scenario.set(scenarioBase);
    }

    public void removeScenarioOfCurrentThread() {
        scenario.remove();
    }

}
