package com.tngtech.jgiven.junit5;

import java.util.function.Function;
import org.junit.platform.engine.TestSource;

interface SourceFilter extends Function<TestSource, Class<?>> {
}
