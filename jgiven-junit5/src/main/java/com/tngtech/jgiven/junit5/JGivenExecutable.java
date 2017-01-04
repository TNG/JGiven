package com.tngtech.jgiven.junit5;

import com.tngtech.jgiven.impl.ScenarioBase;

/**
 * JGiven-specific variant of the {@link org.junit.jupiter.api.function.Executable} interface
 * of JUnit 5 for writing dynamic tests.
 *
 * <h1>HIGHLY EXPERIMENTAL</h1>
 *
 * Most likely this interface will change in future versions of JGiven without prior-notice,
 * please don't use this for any serious projects, yet.
 *
 * @see org.junit.jupiter.api.function.Executable
 * @since 0.15.0
 */
@FunctionalInterface
public interface JGivenExecutable {
    void execute(ScenarioBase scenario) throws Throwable;
}
