package com.tngtech.jgiven.impl;

import com.tngtech.jgiven.annotation.AfterStage;
import com.tngtech.jgiven.impl.util.ReflectionUtil;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

//TODO This class is still closely linked with the ScenarioExecutor Maybe it should be an inner class
class StageState2 {
    final Object instance;
    private Map<Method, StepExecutionState> afterStageCalled;
    boolean beforeStageCalled;
    Object currentChildStage;

    StageState2(Object instance){
       this.instance = instance;
       this.afterStageCalled = new HashMap<>();
       beforeStageCalled = false;
       fillAfterStageRegister();
    }

    private void fillAfterStageRegister(){
        ReflectionUtil.forEachMethod(instance, instance.getClass(), AfterStage.class,
                (object, method)-> {
                   Arrays.stream(method.getDeclaredAnnotations())
                           .filter(annotation -> annotation instanceof AfterStage)
                           .map(annotation -> (AfterStage) annotation)
                           .findFirst()
                           .ifPresent(
                                   it -> afterStageCalled.put(method, it.repeatable()? StepExecutionState.REPEATABLE : StepExecutionState.NOT_EXECUTED)
                           );
                });
    }

    boolean allAfterStageMethodsHaveBeenExecuted(){
        for ( StepExecutionState value: afterStageCalled.values()) {
           if(!value.toBoolean()){
              return false;
           }
        }
        return true;
    }
    boolean afterStageMethodHasBeenExecuted(Method method){
        return Optional.ofNullable(afterStageCalled.get(method))
                .map(StepExecutionState::toBoolean)
                .orElse(true);
    }
    void markAfterStageAsExecuted(Method method){
       StepExecutionState afterStepState = afterStageCalled.get(method);
         if(afterStepState == StepExecutionState.NOT_EXECUTED){
            afterStageCalled.put(method, StepExecutionState.EXECUTED);
         }
    }

   private enum StepExecutionState {
        EXECUTED(true),
        REPEATABLE(false),
        NOT_EXECUTED(false);

       private boolean hasBeenExecuted;

       StepExecutionState(boolean hasBeenExecuted){
          this.hasBeenExecuted = hasBeenExecuted;
       }
        public boolean toBoolean(){ return this.hasBeenExecuted; }
    }

}
