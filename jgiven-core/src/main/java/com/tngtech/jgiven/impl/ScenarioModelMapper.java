package com.tngtech.jgiven.impl;

import com.tngtech.jgiven.annotation.FillerWord;
import com.tngtech.jgiven.annotation.IntroWord;
import com.tngtech.jgiven.annotation.StepComment;
import com.tngtech.jgiven.attachment.Attachment;
import com.tngtech.jgiven.impl.intercept.ScenarioListener;
import com.tngtech.jgiven.report.model.InvocationMode;
import com.tngtech.jgiven.report.model.NamedArgument;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;

/**
 * Translates between the step concepts in the executor and the step concept in the scenario model.
 */
public class ScenarioModelMapper implements ScenarioListener {

    private final ScenarioModelBuilder modelBuilder;

    public ScenarioModelMapper(ScenarioModelBuilder scenarioModelBuilder) {
        modelBuilder = scenarioModelBuilder;
    }

    @Override
    public void scenarioFailed(Throwable e) {
        modelBuilder.scenarioFailed(e);

    }

    @Override
    public void scenarioStarted(String string) {
        modelBuilder.scenarioStarted(string);
    }

    @Override
    public void scenarioStarted(Class<?> testClass, Method method, List<NamedArgument> arguments) {
        modelBuilder.scenarioStarted(testClass, method, arguments);
    }

    @Override
    public void stepMethodInvoked(Method method, List<NamedArgument> arguments, InvocationMode mode,
                                  boolean hasNestedSteps) {
        modelBuilder.stepMethodInvoked(method, arguments, mode, hasNestedSteps);

    }

    private boolean isNonModelStep(Method method) {
        return method.isAnnotationPresent(IntroWord.class) || method.isAnnotationPresent(StepComment.class)
            || method.isAnnotationPresent(
            FillerWord.class);
    }


    @Override
    public void introWordAdded(String introWord) {
        modelBuilder.introWordAdded(introWord);
    }

    @Override
    public void stepCommentUpdated(String comment) {
        modelBuilder.stepCommentUpdated(comment);
    }

    @Override
    public void stepMethodFailed(Throwable t) {
        modelBuilder.stepMethodFailed(t);
    }

    @Override
    public void stepMethodFinished(long durationInNanos, boolean hasNestedSteps) {
        modelBuilder.stepMethodFinished(durationInNanos, hasNestedSteps);
    }

    @Override
    public void scenarioFinished() {
        modelBuilder.scenarioFinished();
    }

    @Override
    public void attachmentAdded(Attachment attachment) {
        modelBuilder.attachmentAdded(attachment);
    }

    @Override
    public void extendedDescriptionUpdated(String extendedDescription) {
        modelBuilder.extendedDescriptionUpdated(extendedDescription);
    }

    @Override
    public void stepNameUpdated(String newStepName) {
        modelBuilder.stepNameUpdated(newStepName);
    }

    @Override
    public void sectionAdded(String sectionTitle) {
        modelBuilder.sectionAdded(sectionTitle);
    }

    @Override
    public void tagAdded(Class<? extends Annotation> annotationClass, String... values) {
        modelBuilder.tagAdded(annotationClass, values);
    }
}
