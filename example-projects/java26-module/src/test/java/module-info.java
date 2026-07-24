/**
 * Example JPMS module that uses JGiven from a named module.
 *
 * <p>This descriptor lives in {@code src/test/java} so that the test source
 * set itself forms a named module, exercising the JGiven injection path
 * under the Java Platform Module System rather than the unnamed-module /
 * classpath fallback.</p>
 *
 * <p>The package is {@code opens}-ed to JGiven so that
 * {@link com.tngtech.jgiven.impl.ByteBuddyStageClassCreator} can obtain a
 * {@link java.lang.invoke.MethodHandles.Lookup} via
 * {@link java.lang.invoke.MethodHandles#privateLookupIn} and inject the
 * generated stage subclass into this module's runtime package. Without the
 * {@code opens} directive below, package-private stage classes cannot be
 * instrumented and JGiven will fall back to the {@code WRAPPER} class loading
 * strategy, which breaks on package-private stages.</p>
 *
 * <p>The same {@code opens} directive also permits reflective access to the
 * package-private {@code @ScenarioState} fields read and written by
 * {@link com.tngtech.jgiven.impl.inject.ValueInjector}.</p>
 */
module com.tngtech.jgiven.exampleprojects.java26.module {
    requires com.tngtech.jgiven.junit6;
    requires com.tngtech.jgiven.core;
    requires org.junit.jupiter.api;

    // Required so JGiven (an automatic module) can deep-reflect into the
    // stage package: instrument package-private stages and access
    // package-private @ScenarioState fields.
    opens com.tngtech.jgiven.exampleprojects.java26.module to com.tngtech.jgiven.core;
}
