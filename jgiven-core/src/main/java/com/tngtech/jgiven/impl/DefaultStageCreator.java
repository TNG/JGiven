package com.tngtech.jgiven.impl;

import com.tngtech.jgiven.impl.intercept.StageInterceptorInternal;
import com.tngtech.jgiven.impl.intercept.StepInterceptor;

public class DefaultStageCreator implements StageCreator {

    private final StageClassCreator stageClassCreator;

    public DefaultStageCreator(StageClassCreator stageClassCreator) {
        this.stageClassCreator = stageClassCreator;
    }

    @SuppressWarnings( "unchecked" )
    @Override
    public <T> T createStage( Class<T> stageClass, StepInterceptor stepInterceptor ) {
        try {
            Class<? extends T> stageSubClass = stageClassCreator.createStageClass(stageClass);
            T result = stageSubClass.newInstance();
            setStepInterceptor( result, stepInterceptor);
            return result;
        } catch( Error e ) {
            throw e;
        } catch( Exception e ) {
            throw new RuntimeException( "Error while trying to create an instance of class " + stageClass, e );
        }
    }

    protected <T> void setStepInterceptor(T result, StepInterceptor stepInterceptor) {
        ((StageInterceptorInternal) result).__jgiven_setStepInterceptor(stepInterceptor);
    }

}
