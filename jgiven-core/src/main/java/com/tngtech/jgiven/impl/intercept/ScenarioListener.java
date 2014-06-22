package com.tngtech.jgiven.impl.intercept;

import java.lang.reflect.Method;
import java.util.List;

import com.tngtech.jgiven.impl.NamedArgument;

public interface ScenarioListener {

    void scenarioFailed( Throwable e );

    void scenarioStarted( String string );

    void scenarioStarted( Method method, List<NamedArgument> arguments );

    void stepMethodInvoked( Method paramMethod, List<Object> arguments, InvocationMode mode );

    void introWordAdded( String word );

}
