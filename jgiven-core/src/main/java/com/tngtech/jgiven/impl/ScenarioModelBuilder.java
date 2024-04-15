package com.tngtech.jgiven.impl;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.tngtech.jgiven.annotation.As;
import com.tngtech.jgiven.annotation.AsProvider;
import com.tngtech.jgiven.annotation.CaseAs;
import com.tngtech.jgiven.annotation.CaseAsProvider;
import com.tngtech.jgiven.annotation.Description;
import com.tngtech.jgiven.annotation.ExtendedDescription;
import com.tngtech.jgiven.annotation.FillerWord;
import com.tngtech.jgiven.annotation.Hidden;
import com.tngtech.jgiven.annotation.IntroWord;
import com.tngtech.jgiven.annotation.Pending;
import com.tngtech.jgiven.annotation.StepComment;
import com.tngtech.jgiven.attachment.Attachment;
import com.tngtech.jgiven.config.AbstractJGivenConfiguration;
import com.tngtech.jgiven.config.ConfigurationUtil;
import com.tngtech.jgiven.config.DefaultConfiguration;
import com.tngtech.jgiven.exception.JGivenWrongUsageException;
import com.tngtech.jgiven.format.ObjectFormatter;
import com.tngtech.jgiven.impl.format.ParameterFormattingUtil;
import com.tngtech.jgiven.impl.intercept.ScenarioListener;
import com.tngtech.jgiven.impl.tag.ResolvedTags;
import com.tngtech.jgiven.impl.tag.TagCreator;
import com.tngtech.jgiven.impl.util.AnnotationUtil;
import com.tngtech.jgiven.impl.util.AssertionUtil;
import com.tngtech.jgiven.impl.util.ReflectionUtil;
import com.tngtech.jgiven.impl.util.WordUtil;
import com.tngtech.jgiven.report.model.ExecutionStatus;
import com.tngtech.jgiven.report.model.InvocationMode;
import com.tngtech.jgiven.report.model.NamedArgument;
import com.tngtech.jgiven.report.model.ReportModel;
import com.tngtech.jgiven.report.model.ScenarioCaseModel;
import com.tngtech.jgiven.report.model.ScenarioModel;
import com.tngtech.jgiven.report.model.StepFormatter;
import com.tngtech.jgiven.report.model.StepModel;
import com.tngtech.jgiven.report.model.StepStatus;
import com.tngtech.jgiven.report.model.Word;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.Stack;

public class ScenarioModelBuilder implements ScenarioListener {

    private static final Set<String> STACK_TRACE_FILTER = ImmutableSet
        .of("sun.reflect", "com.tngtech.jgiven.impl.intercept",
            "$$EnhancerByCGLIB$$",
            "java.lang.reflect", "net.sf.cglib.proxy", "com.sun.proxy");
    private static final boolean FILTER_STACK_TRACE = Config.config().filterStackTrace();

    private ScenarioModel scenarioModel;
    private ScenarioCaseModel scenarioCaseModel;
    private StepModel currentStep;
    private final Stack<StepModel> parentSteps = new Stack<>();

    private final SentenceBuilder sentenceBuilder = new SentenceBuilder();

    private long scenarioStartedNanos;

    private AbstractJGivenConfiguration configuration = new DefaultConfiguration();

    private ReportModel reportModel;
    private TagCreator tagCreator;

    public void setReportModel(ReportModel reportModel) {
        this.reportModel = reportModel;
    }

    private final Stack<Integer> discrepancyOnLayer = new Stack<>();

    @Override
    public void scenarioStarted(String description) {
        scenarioStartedNanos = System.nanoTime();
        String readableDescription = description;

        if (description.contains("_")) {
            readableDescription = description.replace('_', ' ');
        } else if (!description.contains(" ")) {
            readableDescription = WordUtil.camelCaseToCapitalizedReadableText(description);
        }

        scenarioCaseModel = new ScenarioCaseModel();

        scenarioModel = new ScenarioModel();
        scenarioModel.addCase(scenarioCaseModel);
        scenarioModel.setDescription(readableDescription);
        this.tagCreator = new TagCreator(configuration);
        discrepancyOnLayer.push(0);
    }

    @Override
    public void scenarioStarted(Class<?> testClass, Method method, List<NamedArgument> namedArguments) {
        readConfiguration(testClass);
        readAnnotations(testClass, method);
        scenarioModel.setClassName(testClass.getName());
        setParameterNames(getNames(namedArguments));

        // must come at last
        setMethodName(method.getName());

        ParameterFormattingUtil parameterFormattingUtil = new ParameterFormattingUtil(configuration);
        List<ObjectFormatter<?>> formatter =
            parameterFormattingUtil.getFormatter(method.getParameterTypes(), getNames(namedArguments),
                method.getParameterAnnotations());

        setArguments(parameterFormattingUtil.toStringList(formatter, getValues(namedArguments)));
        setCaseDescription(testClass, method, namedArguments);
    }

    private void addStepMethod(Method paramMethod, List<NamedArgument> arguments, InvocationMode mode,
                               boolean hasNestedSteps) {
        StepModel stepModel = createStepModel(paramMethod, arguments, mode);

        if (parentSteps.empty()) {
            getCurrentScenarioCase().addStep(stepModel);
        } else {
            parentSteps.peek().addNestedStep(stepModel);
        }

        if (hasNestedSteps) {
            parentSteps.push(stepModel);
            discrepancyOnLayer.push(0);
        }
        currentStep = stepModel;
    }

    StepModel createStepModel(Method paramMethod, List<NamedArgument> arguments, InvocationMode mode) {
        StepModel stepModel = new StepModel();

        stepModel.setName(getDescription(paramMethod));

        ExtendedDescription extendedDescriptionAnnotation = paramMethod.getAnnotation(ExtendedDescription.class);
        if (extendedDescriptionAnnotation != null) {
            stepModel.setExtendedDescription(extendedDescriptionAnnotation.value());
        }

        List<NamedArgument> nonHiddenArguments =
            filterHiddenArguments(arguments, paramMethod.getParameterAnnotations());

        ParameterFormattingUtil parameterFormattingUtil = new ParameterFormattingUtil(configuration);
        List<ObjectFormatter<?>> formatters =
            parameterFormattingUtil.getFormatter(paramMethod.getParameterTypes(), getNames(arguments),
                paramMethod.getParameterAnnotations());

        new StepFormatter(stepModel.getName(), nonHiddenArguments, formatters).buildFormattedWords()
            .forEach(sentenceBuilder::addWord);

        stepModel.setWords(sentenceBuilder.getWords());

        sentenceBuilder.clear();

        stepModel.setStatus(mode.toStepStatus());
        return stepModel;
    }

    private List<NamedArgument> filterHiddenArguments(List<NamedArgument> arguments,
                                                      Annotation[][] parameterAnnotations) {
        List<NamedArgument> result = Lists.newArrayList();
        for (int i = 0; i < parameterAnnotations.length; i++) {
            if (!AnnotationUtil.isHidden(parameterAnnotations[i])) {
                result.add(arguments.get(i));
            }
        }
        return result;
    }

    @Override
    public void introWordAdded(String value) {
        sentenceBuilder.addIntroWord(value);
    }

    private void addToSentence(String value, boolean joinToPreviousWord, boolean joinToNextWord) {
        if (!sentenceBuilder.hasWords() && currentStep != null && joinToPreviousWord) {
            currentStep.getLastWord().addSuffix(value);
        } else {
            sentenceBuilder.addWord(value, joinToPreviousWord, joinToNextWord);
        }
    }

    private void addStepComment(List<NamedArgument> arguments) {
        if (arguments == null || arguments.size() != 1) {
            throw new JGivenWrongUsageException("A step comment method must have exactly one parameter.");
        }

        if (!(arguments.get(0).getValue() instanceof String)) {
            throw new JGivenWrongUsageException("The step comment method parameter must be a string.");
        }

        if (currentStep == null) {
            throw new JGivenWrongUsageException("A step comment must be added after the corresponding step, "
                + "but no step has been executed yet.");
        }

        stepCommentUpdated((String) arguments.get(0).getValue());
    }


    @Override
    public void stepCommentUpdated(String comment) {
        currentStep.setComment(comment);
    }

    private ScenarioCaseModel getCurrentScenarioCase() {
        if (scenarioCaseModel == null) {
            scenarioStarted("A Scenario");
        }
        return scenarioCaseModel;
    }

    private void incrementDiscrepancy() {
        int discrepancyOnCurrentLayer = discrepancyOnLayer.pop();
        discrepancyOnCurrentLayer++;
        discrepancyOnLayer.push(discrepancyOnCurrentLayer);
    }

    private void decrementDiscrepancy() {
        if (discrepancyOnLayer.peek() > 0) {
            int discrepancyOnCurrentLayer = discrepancyOnLayer.pop();
            discrepancyOnCurrentLayer--;
            discrepancyOnLayer.push(discrepancyOnCurrentLayer);
        }
    }

    @Override
    public void stepMethodInvoked(Method method, List<NamedArgument> arguments, InvocationMode mode,
                                  boolean hasNestedSteps) {
        if (method.isAnnotationPresent(IntroWord.class)) {
            introWordAdded(getDescription(method));
            incrementDiscrepancy();
        } else if (method.isAnnotationPresent(FillerWord.class)) {
            FillerWord fillerWord = method.getAnnotation(FillerWord.class);
            addToSentence(getDescription(method), fillerWord.joinToPreviousWord(), fillerWord.joinToNextWord());
            incrementDiscrepancy();
        } else if (method.isAnnotationPresent(StepComment.class)) {
            addStepComment(arguments);
            incrementDiscrepancy();
        } else {
            addTags(method.getAnnotations());
            addTags(method.getDeclaringClass().getAnnotations());

            addStepMethod(method, arguments, mode, hasNestedSteps);
        }
    }

    public void setMethodName(String methodName) {
        scenarioModel.setTestMethodName(methodName);
    }

    public void setArguments(List<String> arguments) {
        scenarioCaseModel.setExplicitArguments(arguments);
    }

    public void setParameterNames(List<String> parameterNames) {
        scenarioModel.setExplicitParameters(removeUnderlines(parameterNames));
    }

    private static List<String> removeUnderlines(List<String> parameterNames) {
        List<String> result = Lists.newArrayListWithCapacity(parameterNames.size());
        for (String paramName : parameterNames) {
            result.add(WordUtil.fromSnakeCase(paramName));
        }
        return result;
    }

    private String getDescription(Method paramMethod) {
        if (paramMethod.isAnnotationPresent(Hidden.class)) {
            return "";
        }

        Description description = paramMethod.getAnnotation(Description.class);
        if (description != null) {
            return description.value();
        }

        As as = paramMethod.getAnnotation(As.class);
        return getAsProvider(as).as(as, paramMethod);
    }

    public void setStatus(ExecutionStatus status) {
        scenarioCaseModel.setStatus(status);
    }

    private void setException(Throwable throwable) {
        scenarioCaseModel.setErrorMessage(throwable.getClass().getName() + ": " + throwable.getMessage());
        scenarioCaseModel.setStackTrace(getStackTrace(throwable, FILTER_STACK_TRACE));
    }

    private List<String> getStackTrace(Throwable exception, boolean filterStackTrace) {
        StackTraceElement[] stackTraceElements = exception.getStackTrace();
        ArrayList<String> stackTrace = new ArrayList<>(stackTraceElements.length);

        outer:
        for (StackTraceElement element : stackTraceElements) {
            if (filterStackTrace) {
                for (String filter : STACK_TRACE_FILTER) {
                    if (element.getClassName().contains(filter)) {
                        continue outer;
                    }
                }
            }
            stackTrace.add(element.toString());
        }
        return stackTrace;
    }

    @Override
    public void stepMethodFailed(Throwable t) {
        if (currentStep != null) {
            currentStep.setStatus(StepStatus.FAILED);
        }
    }

    @Override
    public void stepMethodFinished(long durationInNanos, boolean hasNestedSteps) {
        if (hasNestedSteps && !parentSteps.isEmpty()) {
            currentStep = parentSteps.peek();
        }

        if (currentStep != null) {
            if (discrepancyOnLayer.empty() || discrepancyOnLayer.peek() == 0) {
                currentStep.setDurationInNanos(durationInNanos);
            }
            if (hasNestedSteps) {
                if (currentStep.getStatus() != StepStatus.FAILED) {
                    currentStep.setStatus(getStatusFromNestedSteps(currentStep.getNestedSteps()));
                }
                parentSteps.pop();
                discrepancyOnLayer.pop();
            }
        }

        if (!hasNestedSteps && !parentSteps.isEmpty()) {
            currentStep = parentSteps.peek();
        }

        decrementDiscrepancy();

    }

    private StepStatus getStatusFromNestedSteps(List<StepModel> nestedSteps) {
        StepStatus status = StepStatus.PASSED;
        for (StepModel nestedModel : nestedSteps) {
            StepStatus nestedStatus = nestedModel.getStatus();

            switch (nestedStatus) {
                case FAILED:
                    return StepStatus.FAILED;
                case PENDING:
                    status = StepStatus.PENDING;
                    break;
                default:
            }
        }
        return status;
    }

    @Override
    public void scenarioFailed(Throwable e) {
        setStatus(ExecutionStatus.FAILED);
        setException(e);
    }

    private void setCaseDescription(Class<?> testClass, Method method, List<NamedArgument> namedArguments) {

        CaseAs annotation = null;
        if (method.isAnnotationPresent(CaseAs.class)) {
            annotation = method.getAnnotation(CaseAs.class);
        } else if (testClass.isAnnotationPresent(CaseAs.class)) {
            annotation = testClass.getAnnotation(CaseAs.class);
        }

        if (annotation != null) {
            CaseAsProvider caseDescriptionProvider = ReflectionUtil.newInstance(annotation.provider());
            String value = annotation.value();
            List<?> values;
            if (annotation.formatValues()) {
                values = scenarioCaseModel.getExplicitArguments();
            } else {
                values = getValues(namedArguments);
            }
            String caseDescription = caseDescriptionProvider.as(value, scenarioModel.getExplicitParameters(), values);
            scenarioCaseModel.setDescription(caseDescription);
        }
    }

    private List<Object> getValues(List<NamedArgument> namedArguments) {
        List<Object> result = Lists.newArrayList();
        for (NamedArgument a : namedArguments) {
            result.add(a.value);
        }
        return result;
    }

    private List<String> getNames(List<NamedArgument> namedArguments) {
        List<String> result = Lists.newArrayList();
        for (NamedArgument a : namedArguments) {
            result.add(a.name);
        }
        return result;
    }

    private void readConfiguration(Class<?> testClass) {
        configuration = ConfigurationUtil.getConfiguration(testClass);
    }

    private void readAnnotations(Class<?> testClass, Method method) {
        String scenarioDescription = evaluateMethodForDescription(method);
        scenarioStarted(scenarioDescription);

        if (method.isAnnotationPresent(ExtendedDescription.class)) {
            scenarioModel.setExtendedDescription(method.getAnnotation(ExtendedDescription.class).value());
        }

        if (method.isAnnotationPresent(Pending.class)
            || method.getDeclaringClass().isAnnotationPresent(Pending.class)) {
            scenarioCaseModel.setStatus(ExecutionStatus.SCENARIO_PENDING);
        }

        if (scenarioCaseModel.getCaseNr() == 1) {
            addTags(testClass.getAnnotations());
            addTags(method.getAnnotations());
        }
    }
    private String evaluateMethodForDescription(Method method) {
        if (method.isAnnotationPresent(Description.class)) {
            return method.getAnnotation(Description.class).value();
        } else {
            As as = method.getAnnotation(As.class);
            return getAsProvider(as).as(as, method);
        }
    }

    @Override
    public void scenarioFinished() {
        AssertionUtil.assertTrue(scenarioStartedNanos > 0, "Scenario has no start time");
        long durationInNanos = System.nanoTime() - scenarioStartedNanos;
        scenarioCaseModel.setDurationInNanos(durationInNanos);
        scenarioModel.addDurationInNanos(durationInNanos);
        reportModel.addScenarioModelOrMergeWithExistingOne(scenarioModel);
    }

    @Override
    public void attachmentAdded(Attachment attachment) {
        currentStep.addAttachment(attachment);
    }

    @Override
    public void extendedDescriptionUpdated(String extendedDescription) {
        currentStep.setExtendedDescription(extendedDescription);
    }

    @Override
    public void stepNameUpdated(String newStepName) {
        List<Word> newWords = Lists.newArrayList();

        for (Word word : currentStep.getWords()) {
            if (word.isIntroWord()) {
                newWords.add(word);
            }
        }

        newWords.add(new Word(newStepName));

        currentStep.setWords(newWords);
        currentStep.setName(newStepName);
    }

    @Override
    public void sectionAdded(String sectionTitle) {
        StepModel stepModel = new StepModel();
        stepModel.setName(sectionTitle);
        stepModel.addWords(new Word(sectionTitle));
        stepModel.setIsSectionTitle(true);
        getCurrentScenarioCase().addStep(stepModel);
    }

    @Override
    public void tagAdded(Class<? extends Annotation> annotationClass, String... values) {
        addTags(tagCreator.toTags(annotationClass, values));
    }


    private void addTags(Annotation... annotations) {
        for (Annotation annotation : annotations) {
            addTags(tagCreator.toTags(annotation));
        }
    }

    private void addTags(ResolvedTags tags) {
        if (tags.isEmpty()) {
            return;
        }

        if (reportModel != null) {
            this.reportModel.addTags(tags.getDeclaredTags());
            //The report model needs to declare the parent tags in a tag map, or the tags cannot be displayed.
            this.reportModel.addTags(tags.getAncestors());
        }

        if (scenarioModel != null) {
            this.scenarioModel.addTags(tags.getDeclaredTags());
        }
    }

    public ReportModel getReportModel() {
        return reportModel;
    }

    public ScenarioModel getScenarioModel() {
        return scenarioModel;
    }

    public ScenarioCaseModel getScenarioCaseModel() {
        return scenarioCaseModel;
    }

    private AsProvider getAsProvider(As as) {
        return as != null
            ? ReflectionUtil.newInstance(as.provider())
            : configuration.getAsProvider();
    }
}
