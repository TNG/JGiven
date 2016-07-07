package com.tngtech.jgiven.impl.intercept;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;

import com.tngtech.jgiven.attachment.Attachment;
import com.tngtech.jgiven.report.model.InvocationMode;
import com.tngtech.jgiven.report.model.NamedArgument;

public interface ScenarioListener {

    void scenarioFailed( Throwable e );

    void scenarioStarted( String string );

    void scenarioStarted( Class<?> testClass, Method method, List<NamedArgument> arguments );

    void stepMethodInvoked( Method method, List<NamedArgument> arguments, InvocationMode mode, boolean hasNestedSteps );

    void introWordAdded( String introWord );

    void stepCommentAdded( List<NamedArgument> arguments );

    void stepMethodFailed( Throwable t );

    void stepMethodFinished( long durationInNanos, boolean hasNestedSteps );

    void scenarioFinished();

    void attachmentAdded( Attachment attachment );

    void extendedDescriptionUpdated( String extendedDescription );

    void sectionAdded( String sectionTitle );

    void tagAdded( Class<? extends Annotation> annotationClass, String... values );
}
