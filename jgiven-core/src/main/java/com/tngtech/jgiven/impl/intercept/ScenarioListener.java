package com.tngtech.jgiven.impl.intercept;

import java.lang.reflect.Method;
import java.util.List;

public interface ScenarioListener {

    void scenarioFailed( Throwable e );

    void scenarioSucceeded();

    void scenarioStarted( String string );

    void scenarioStarted( Method method, List<?> arguments );

    void stepMethodInvoked( Method paramMethod, List<Object> arguments );

    void introWordAdded( String word );

}
