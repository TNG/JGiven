package com.tngtech.jgiven.junit5;

import org.junit.platform.engine.TestSource;
import org.junit.platform.engine.support.descriptor.ClassSource;

class ClassSourceFilter implements SourceFilter {

    @Override
    public Class<?> apply(TestSource testSource) {
        return (testSource instanceof ClassSource) ? ((ClassSource) testSource).getJavaClass() : null;
    }
}
