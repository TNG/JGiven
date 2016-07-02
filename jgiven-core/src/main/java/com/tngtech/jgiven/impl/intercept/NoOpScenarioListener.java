package com.tngtech.jgiven.impl.intercept;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;

import com.tngtech.jgiven.attachment.Attachment;
import com.tngtech.jgiven.report.model.InvocationMode;
import com.tngtech.jgiven.report.model.NamedArgument;

public class NoOpScenarioListener implements ScenarioListener {

    @Override
    public void scenarioFailed( Throwable e ) {}

    @Override
    public void scenarioStarted( String string ) {}

    @Override
    public void scenarioStarted( Class<?> testClass, Method method, List<NamedArgument> arguments ) {}

    @Override
    public void stepMethodInvoked( Method method, List<NamedArgument> arguments, InvocationMode mode, boolean hasNestedSteps ) {}

    @Override
    public void introWordAdded( String introWord ) {}

    @Override
    public void stepCommentAdded( List<NamedArgument> arguments ) {}

    @Override
    public void stepMethodFailed( Throwable t ) {}

    @Override
    public void stepMethodFinished( long durationInNanos, boolean hasNestedSteps ) {}

    @Override
    public void scenarioFinished() {}

    @Override
    public void attachmentAdded( Attachment attachment ) {}

    @Override
    public void extendedDescriptionUpdated( String extendedDescription ) {}

    @Override
    public void sectionAdded( String sectionTitle ) {}

    @Override
    public void tagAdded( Class<? extends Annotation> annotationClass, String... values ) {}
}
