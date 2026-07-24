# Java 21 Module Example Project

This project shows how JGiven can be used from a JPMS named module
(`module-info.java`) on Java 26, exercising the future-proof injection path
that is required once JGiven stages live in a named module rather than on the
classpath.

It demonstrates two things that are easy to break under the Java Platform
Module System:

1. **A package-private stage class** &mdash; `PackagePrivateStage`. JGiven
   instruments stages by generating a ByteBuddy subclass in the *same runtime
   package* as the stage itself. When the stage lives in a named module, that
   is only possible if the module `opens` the stage's package so
   `MethodHandles.privateLookupIn` can acquire the necessary `PACKAGE`/
   `MODULE` privileges (see
   `com.tngtech.jgiven.impl.ByteBuddyStageClassCreator#tryLookupStrategy`).

2. **Package-private `@ScenarioState` fields** &mdash; on both
   `PackagePrivateStage` and `ThenStage`. JGiven reads and writes these
   fields reflectively via `com.tngtech.jgiven.impl.inject.ValueInjector`,
   which calls `Field.setAccessible(true)`. Under JPMS this is only permitted
   when the declaring module has opened the package containing the field.

The module descriptor lives in `src/test/java/module-info.java` so that the
test source set itself forms the named module
`com.tngtech.jgiven.exampleprojects.java21.module`. Gradle infers the module
path automatically (because `module-info.java` is present) and
`--patch-module`s the compiled test classes into that module.

## Run

1. Publish the JGiven artifacts to a local Maven repository, or point
   `version` in `build.gradle.kts` at a released version.
2. From this directory:

   ```
   ../../gradlew build
   ```
3. Open `build/reports/jgiven/test/html/index.html` for the JGiven scenario
   report, and `build/reports/tests/test/index.html` for the JUnit results.

## Caveats

The `test` task sets `jgiven.report.text=false`. The text report path goes
through `com.tngtech.jgiven.impl.util.AnsiUtil`, which statically imports
`org.fusesource.jansi.internal.CLibrary`; under JPMS jansi does not export
that package, so instantiating `PlainTextReporter` raises an
`IllegalAccessError`. The HTML report produced by `jgivenTestReport` is
unaffected.

If the module does not `opens` its package, the scenario fails at runtime
with a `WARN` from `ByteBuddyStageClassCreator` followed by an
`IncompatibleClassChangeError` / `IllegalAccessError` when JGiven tries to
instantiate the generated stage subclass.
