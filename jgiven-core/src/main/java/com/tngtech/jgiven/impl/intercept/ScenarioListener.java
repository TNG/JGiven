package com.tngtech.jgiven.impl.intercept;

import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.List;

public interface ScenarioListener {

    void scenarioFailed( Throwable e );

    void scenarioStarted( String string );

    void scenarioStarted( Method method, LinkedHashMap<String, ?> arguments );

    void stepMethodInvoked( Method paramMethod, List<Object> arguments, InvocationMode mode );

    void introWordAdded( String word );

}
