/**
 * Example JPMS module that uses JGiven from a named module.
 *
 * <p>The test source set forms a named module so that JGiven's injection path
 * is exercised under JPMS rather than the unnamed-module classpath fallback.
 * The package is {@code opens}-ed to JGiven so that it can instrument
 * package-private stages and access package-private {@code @ScenarioState}
 * fields; without it JGiven falls back to the {@code WRAPPER} class loading
 * strategy, which breaks on package-private stages.
 */
module com.tngtech.jgiven.exampleprojects.java.module {
    requires com.tngtech.jgiven.junit6;
    requires com.tngtech.jgiven.core;
    requires org.junit.jupiter.api;

    opens com.tngtech.jgiven.exampleprojects.java.module to com.tngtech.jgiven.core,
            org.junit.platform.commons,
            org.junit.jupiter.engine,
            net.bytebuddy;
}