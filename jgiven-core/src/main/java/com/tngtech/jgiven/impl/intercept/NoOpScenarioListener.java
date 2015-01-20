package com.tngtech.jgiven.impl.intercept;

import java.lang.reflect.Method;
import java.util.List;

import com.tngtech.jgiven.attachment.Attachment;
import com.tngtech.jgiven.report.model.NamedArgument;

public class NoOpScenarioListener implements ScenarioListener {

    @Override
    public void scenarioFailed( Throwable e ) {}

    @Override
    public void scenarioStarted( String string ) {}

    @Override
    public void scenarioStarted( Method method, List<NamedArgument> arguments ) {}

    @Override
    public void stepMethodInvoked( Method method, List<NamedArgument> arguments, InvocationMode mode ) {}

    @Override
    public void introWordAdded( String word ) {}

    @Override
    public void stepMethodFailed( Throwable t ) {}

    @Override
    public void stepMethodFinished( long durationInNanos ) {}

    @Override
    public void scenarioFinished() {}

    @Override
    public void attachmentAdded( Attachment attachment ) {}
}
