package com.tngtech.jgiven.junit5;

import org.junit.platform.engine.TestSource;
import org.junit.platform.engine.support.descriptor.MethodSource;

class MethodSourceFilter implements SourceFilter {

    @Override
    public Class<?> apply(TestSource testSource) {
        return (testSource instanceof MethodSource) ? ((MethodSource) testSource).getJavaClass() : null;
    }
}
