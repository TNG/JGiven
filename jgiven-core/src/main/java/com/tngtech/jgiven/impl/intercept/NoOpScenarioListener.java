package com.tngtech.jgiven.impl.intercept;

import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.List;

public class NoOpScenarioListener implements ScenarioListener {

    @Override
    public void scenarioFailed( Throwable e ) {}

    @Override
    public void scenarioStarted( String string ) {}

    @Override
    public void scenarioStarted( Method method, LinkedHashMap<String, ?> arguments ) {}

    @Override
    public void stepMethodInvoked( Method paramMethod, List<Object> arguments, InvocationMode mode ) {}

    @Override
    public void introWordAdded( String word ) {}
}
