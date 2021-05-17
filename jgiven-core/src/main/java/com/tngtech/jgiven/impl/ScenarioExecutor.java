package com.tngtech.jgiven.impl;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Lists.reverse;
import static com.tngtech.jgiven.impl.ScenarioExecutor.State.FINISHED;
import static com.tngtech.jgiven.impl.ScenarioExecutor.State.STARTED;

import com.tngtech.jgiven.CurrentScenario;
import com.tngtech.jgiven.CurrentStep;
import com.tngtech.jgiven.annotation.AfterScenario;
import com.tngtech.jgiven.annotation.BeforeScenario;
import com.tngtech.jgiven.annotation.Pending;
import com.tngtech.jgiven.annotation.ScenarioRule;
import com.tngtech.jgiven.annotation.ScenarioStage;
import com.tngtech.jgiven.attachment.Attachment;
import com.tngtech.jgiven.exception.FailIfPassedException;
import com.tngtech.jgiven.exception.JGivenMissingRequiredScenarioStateException;
import com.tngtech.jgiven.exception.JGivenUserException;
import com.tngtech.jgiven.impl.inject.ValueInjector;
import com.tngtech.jgiven.impl.intercept.NoOpScenarioListener;
import com.tngtech.jgiven.impl.intercept.ScenarioListener;
import com.tngtech.jgiven.impl.intercept.StageTransitionHandler;
import com.tngtech.jgiven.impl.intercept.StepInterceptorImpl;
import com.tngtech.jgiven.impl.util.FieldCache;
import com.tngtech.jgiven.impl.util.ReflectionUtil;
import com.tngtech.jgiven.integration.CanWire;
import com.tngtech.jgiven.report.model.InvocationMode;
import com.tngtech.jgiven.report.model.NamedArgument;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Main class of JGiven for executing scenarios.
 */
public class ScenarioExecutor {
    private static final Logger log = LoggerFactory.getLogger(ScenarioExecutor.class);

    enum State {
        INIT,
        STARTED,
        FINISHED
    }

    private Object currentTopLevelStage;
    private State state = State.INIT;
    private boolean beforeScenarioMethodsExecuted;

    /**
     * Whether life cycle methods should be executed.
     * This is only false for scenarios that are annotated with @NotImplementedYet
     */
    private boolean executeLifeCycleMethods = true;

    protected final Map<Class<?>, StageState> stages = new LinkedHashMap<>();

    private final List<Object> scenarioRules = new ArrayList<>();

    private final ValueInjector injector = new ValueInjector();
    private StageCreator stageCreator = createStageCreator(new ByteBuddyStageClassCreator());
    private ScenarioListener listener = new NoOpScenarioListener();
    protected final StageTransitionHandler stageTransitionHandler = new StageTransitionHandlerImpl();
    protected final StepInterceptorImpl methodInterceptor =
        new StepInterceptorImpl(this, listener, stageTransitionHandler);

    /**
     * Set if an exception was thrown during the execution of the scenario and
     * suppressStepExceptions is true.
     */
    private Throwable failedException;

    private boolean failIfPass;

    /**
     * Whether exceptions caught while executing steps should be thrown at the end
     * of the scenario. Only relevant if suppressStepExceptions is true, because otherwise
     * the exceptions are not caught at all.
     */
    private boolean suppressExceptions;

    /**
     * Whether exceptions thrown while executing steps should be suppressed or not.
     * Only relevant for normal executions of scenarios.
     */
    private boolean suppressStepExceptions = true;

    public ScenarioExecutor() {
        injector.injectValueByType(ScenarioExecutor.class, this);
        injector.injectValueByType(CurrentStep.class, new StepAccessImpl());
        injector.injectValueByType(CurrentScenario.class, new ScenarioAccessImpl());
    }

    class StepAccessImpl implements CurrentStep {

        @Override
        public void addAttachment(Attachment attachment) {
            listener.attachmentAdded(attachment);
        }

        @Override
        public void setExtendedDescription(String extendedDescription) {
            listener.extendedDescriptionUpdated(extendedDescription);
        }

        @Override
        public void setName(String name) {
            listener.stepNameUpdated(name);
        }

        @Override
        public void setComment(String comment) {
            listener.stepCommentUpdated(comment);
        }
    }

    class ScenarioAccessImpl implements CurrentScenario {

        @Override
        public void addTag(Class<? extends Annotation> annotationClass, String... values) {
            listener.tagAdded(annotationClass, values);
        }

    }

    class StageTransitionHandlerImpl implements StageTransitionHandler {

        @Override
        public void enterStage(Object parentStage, Object childStage) throws Throwable {
            if (parentStage == childStage || currentTopLevelStage == childStage) { // NOSONAR: reference comparison OK
                return;
            }

            // if currentStage == null, this means that no stage at
            // all has been executed, thus we call all beforeScenarioMethods
            if (currentTopLevelStage == null) {
                ensureBeforeScenarioMethodsAreExecuted();
            } else {
                // in case parentStage == null, this is the first top-level
                // call on this stage, thus we have to call the afterStage methods
                // from the current top level stage
                if (parentStage == null) {
                    executeAfterStageMethods(currentTopLevelStage);
                    readScenarioState(currentTopLevelStage);
                } else {
                    // as the parent stage is not null, we have a true child call
                    // thus we have to read the state from the parent stage
                    readScenarioState(parentStage);

                    // if there has been a child stage that was executed before
                    // and the new child stage is different, we have to execute
                    // the after stage methods of the previous child stage
                    StageState stageState = getStageState(parentStage);
                    if (stageState.currentChildStage != null && stageState.currentChildStage != childStage
                        && !afterStageMethodsCalled(stageState.currentChildStage)) {
                        updateScenarioState(stageState.currentChildStage);
                        executeAfterStageMethods(stageState.currentChildStage);
                        readScenarioState(stageState.currentChildStage);
                    }

                    stageState.currentChildStage = childStage;
                }
            }

            updateScenarioState(childStage);
            executeBeforeStageMethods(childStage);
            if (parentStage == null) {
                currentTopLevelStage = childStage;
            }
        }

        @Override
        public void leaveStage(Object parentStage, Object childStage) throws Throwable {
            if (parentStage == childStage || parentStage == null) {
                return;
            }

            readScenarioState(childStage);

            // in case we leave a child stage that itself had a child stage
            // we have to execute the after stage method of that transitive child
            StageState childState = getStageState(childStage);
            if (childState.currentChildStage != null) {
                updateScenarioState(childState.currentChildStage);
                if (!getStageState(childState.currentChildStage).allAfterStageMethodsHaveBeenExecuted()) {
                    executeAfterStageMethods(childState.currentChildStage);
                    readScenarioState(childState.currentChildStage);
                    updateScenarioState(childStage);
                }
                childState.currentChildStage = null;
            }

            updateScenarioState(parentStage);
        }

    }

    @SuppressWarnings("unchecked")
    public <T> T addStage(Class<T> stageClass) {
        if (stages.containsKey(stageClass)) {
            return (T) stages.get(stageClass).instance;
        }

        T result = stageCreator.createStage(stageClass, methodInterceptor);
        methodInterceptor.enableMethodInterception(true);

        stages.put(stageClass, new StageState(result));
        gatherRules(result);
        injectStages(result);
        return result;
    }

    public void addIntroWord(String word) {
        listener.introWordAdded(word);
    }

    @SuppressWarnings("unchecked")
    private void gatherRules(Object stage) {
        for (Field field : FieldCache.get(stage.getClass()).getFieldsWithAnnotation(ScenarioRule.class)) {
            log.debug("Found rule in field {} ", field);
            try {
                scenarioRules.add(field.get(stage));
            } catch (IllegalAccessException e) {
                throw new RuntimeException("Error while reading field " + field, e);
            }
        }

    }

    private <T> void updateScenarioState(T t) {
        try {
            injector.updateValues(t);
        } catch (JGivenMissingRequiredScenarioStateException e) {
            if (!suppressExceptions) {
                throw e;
            }
        }
    }

    private boolean afterStageMethodsCalled(Object stage) {
        return getStageState(stage).allAfterStageMethodsHaveBeenExecuted();
    }

    public StageState getStageState(Object stage) {
        StageState stageState = stages.get(stage.getClass());
        return stageState != null ? stageState : stages.get(stage.getClass().getSuperclass());
    }

    private void ensureBeforeScenarioMethodsAreExecuted() throws Throwable {
        if (state != State.INIT) {
            return;
        }
        state = STARTED;
        methodInterceptor.enableMethodInterception(false);

        try {
            for (Object rule : scenarioRules) {
                invokeRuleMethod(rule, "before");
            }

            beforeScenarioMethodsExecuted = true;

            for (StageState stage : stages.values()) {
                executeBeforeScenarioMethods(stage.instance);
            }
        } catch (Throwable e) {
            failed(e);
            finished();
            throw e;
        }

        methodInterceptor.enableMethodInterception(true);
    }

    private <T extends Annotation> List<Method>
    getExecutableLifecycleMethods(Object stage, Class<T> annotation, Predicate<Method> selectForExecution) {
        List<Method> methodList = newArrayList();
        ReflectionUtil.forEachMethod(stage, stage.getClass(), annotation, (Object object, Method method) -> {
            if (selectForExecution.test(method)) {
                methodList.add(method);
            }
        });
        return methodList;
    }

    //TODO: factor out log statements
    private <T extends Annotation> void doExecuteLifeCycleMethods(Object target, Iterable<Method> methods,
                                                                  Class<T> annotationType) throws Throwable {
        if (!executeLifeCycleMethods) {
            return;
        }
        log.debug("Executing methods annotated with @{}", annotationType.getName());
        boolean previousMethodExecution = methodInterceptor.enableMethodExecution(true);
        try {
            methodInterceptor.enableMethodInterception(false);
            methods.forEach(it -> ReflectionUtil.invokeMethod(target,
                it,
                " with annotation @" + annotationType.getName())
            );
            methodInterceptor.enableMethodInterception(true);
        } catch (JGivenUserException e) {
            throw e.getCause();
        } finally {
            methodInterceptor.enableMethodExecution(previousMethodExecution);
        }
    }

    private void invokeRuleMethod(Object rule, String methodName) throws Throwable {
        if (!executeLifeCycleMethods) {
            return;
        }

        Optional<Method> optionalMethod = ReflectionUtil.findMethodTransitively(rule.getClass(), methodName);
        if (!optionalMethod.isPresent()) {
            log.debug("Class {} has no {} method, but was used as ScenarioRule!", rule.getClass(), methodName);
            return;
        }

        try {
            ReflectionUtil.invokeMethod(rule, optionalMethod.get(), " of rule class " + rule.getClass().getName());
        } catch (JGivenUserException e) {
            throw e.getCause();
        }
    }

    private void executeBeforeScenarioMethods(Object stage) throws Throwable {
        List<Method> methodsToExecute =
            getExecutableLifecycleMethods(stage, BeforeScenario.class, (it) -> true);
        doExecuteLifeCycleMethods(stage, methodsToExecute, BeforeScenario.class);
    }

    private void executeBeforeStageMethods(Object stage) throws Throwable {
        StageLifecycleManager stageState = getStageState(stage);
        if (stageState.allBeforeStageMethodsHaveBeenExecuted()) {
            return;
        }
        stageState.executeBeforeStageMethods(methodInterceptor, executeLifeCycleMethods);
    }

    private void executeAfterStageMethods(Object stage) throws Throwable {
        StageLifecycleManager stageState = getStageState(stage);
        if (stageState.allAfterStageMethodsHaveBeenExecuted()) {
            return;
        }
        stageState.executeAfterStageMethods(methodInterceptor, executeLifeCycleMethods);
    }

    private void executeAfterScenarioMethods(Object stage) throws Throwable {
        List<Method> methodsToExecute =
            getExecutableLifecycleMethods(stage, AfterScenario.class, (it) -> true);
        doExecuteLifeCycleMethods(stage, methodsToExecute, AfterScenario.class);
    }

    public void readScenarioState(Object object) {
        injector.readValues(object);
    }

    /**
     * Used for DI frameworks to inject values into stages.
     */
    public void wireSteps(CanWire canWire) {
        for (StageState steps : stages.values()) {
            canWire.wire(steps.instance);
        }
    }

    /**
     * Has to be called when the scenario is finished in order to execute after methods.
     */
    public void finished() throws Throwable {
        if (state == FINISHED) {
            return;
        }

        State previousState = state;

        state = FINISHED;
        methodInterceptor.enableMethodInterception(false);

        try {
            if (previousState == STARTED) {
                callFinishLifeCycleMethods();
            }
        } finally {
            listener.scenarioFinished();
        }
    }

    private void callFinishLifeCycleMethods() throws Throwable {
        Throwable firstThrownException = failedException;
        if (beforeScenarioMethodsExecuted) {
            try {
                if (currentTopLevelStage != null) {
                    executeAfterStageMethods(currentTopLevelStage);
                }
            } catch (Exception e) {
                firstThrownException = logAndGetFirstException(firstThrownException, e);
            }

            for (StageState stage : reverse(newArrayList(stages.values()))) {
                try {
                    executeAfterScenarioMethods(stage.instance);
                } catch (Exception e) {
                    firstThrownException = logAndGetFirstException(firstThrownException, e);
                }
            }
        }

        for (Object rule : reverse(scenarioRules)) {
            try {
                invokeRuleMethod(rule, "after");
            } catch (Exception e) {
                firstThrownException = logAndGetFirstException(firstThrownException, e);
            }
        }

        failedException = firstThrownException;

        if (!suppressExceptions && failedException != null) {
            throw failedException;
        }

        if (failIfPass && failedException == null) {
            throw new FailIfPassedException();
        }
    }

    private Throwable logAndGetFirstException(Throwable firstThrownException, Throwable newException) {
        log.error(newException.getMessage(), newException);
        return firstThrownException == null ? newException : firstThrownException;
    }

    @SuppressWarnings("unchecked")
    public void injectStages(Object stage) {
        for (Field field : FieldCache.get(stage.getClass()).getFieldsWithAnnotation(ScenarioStage.class)) {
            Object steps = addStage(field.getType());
            ReflectionUtil.setField(field, stage, steps, ", annotated with @ScenarioStage");
        }
    }

    public boolean hasFailed() {
        return failedException != null;
    }

    public Throwable getFailedException() {
        return failedException;
    }

    public void setFailedException(Exception e) {
        failedException = e;
    }

    public void failed(Throwable e) {
        if (hasFailed()) {
            log.error(e.getMessage(), e);
        } else {
            if (!failIfPass) {
                listener.scenarioFailed(e);
            }
            methodInterceptor.disableMethodExecution();
            failedException = e;
        }
    }

    /**
     * Starts a scenario with the given description.
     *
     * @param description the description of the scenario
     */
    public void startScenario(String description) {
        listener.scenarioStarted(description);
    }

    /**
     * Starts the scenario with the given method and arguments.
     * Derives the description from the method name.
     *
     * @param method    the method that started the scenario
     * @param arguments the test arguments with their parameter names
     */
    public void startScenario(Class<?> testClass, Method method, List<NamedArgument> arguments) {
        listener.scenarioStarted(testClass, method, arguments);

        if (Config.config().dryRun()) {
            methodInterceptor.setDefaultInvocationMode(InvocationMode.PENDING);
            methodInterceptor.disableMethodExecution();
            executeLifeCycleMethods = false;
            suppressExceptions = true;
        } else {
            Pending annotation = extractPendingAnnotation(method);
            if (annotation == null) {
                methodInterceptor.setSuppressExceptions(suppressStepExceptions);
            } else {
                if (annotation.failIfPass()) {
                    failIfPass();
                } else {
                    methodInterceptor.setDefaultInvocationMode(InvocationMode.PENDING);
                    if (!annotation.executeSteps()) {
                        methodInterceptor.disableMethodExecution();
                        executeLifeCycleMethods = false;
                    }
                }
                suppressExceptions = true;
            }
        }
    }

    private Pending extractPendingAnnotation(Method method) {
        if (method.isAnnotationPresent(Pending.class)) {
            return method.getAnnotation(Pending.class);
        }
        if (method.getDeclaringClass().isAnnotationPresent(Pending.class)) {
            return method.getDeclaringClass().getAnnotation(Pending.class);
        }
        return null;
    }

    public void setListener(ScenarioListener listener) {
        this.listener = listener;
        methodInterceptor.setScenarioListener(listener);
    }

    public void failIfPass() {
        failIfPass = true;
    }

    public void setSuppressStepExceptions(boolean suppressStepExceptions) {
        this.suppressStepExceptions = suppressStepExceptions;
    }

    public void setSuppressExceptions(boolean suppressExceptions) {
        this.suppressExceptions = suppressExceptions;
    }

    public void addSection(String sectionTitle) {
        listener.sectionAdded(sectionTitle);
    }

    public void setStageCreator(StageCreator stageCreator) {
        this.stageCreator = stageCreator;
    }

    public void setStageClassCreator(StageClassCreator stageClassCreator) {
        this.stageCreator = createStageCreator(stageClassCreator);
    }

    private StageCreator createStageCreator(StageClassCreator stageClassCreator) {
        return new DefaultStageCreator(new CachingStageClassCreator(stageClassCreator));
    }

    private static class StageState extends StageLifecycleManager {
        final Object instance;
        Object currentChildStage;
        private StageState(Object instance) {
            super(instance);
            this.instance = instance;
        }

    }

}
