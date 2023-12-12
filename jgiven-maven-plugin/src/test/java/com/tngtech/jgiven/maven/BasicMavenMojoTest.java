package com.tngtech.jgiven.maven;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.shared.invoker.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

import static java.lang.String.format;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static org.assertj.core.api.Assertions.assertThat;

class BasicMavenMojoTest {

    private static final String PROJECT_FOLDER = "sampleProject"; //name is also used in renovate.json

    private static File projectFile;

    @BeforeAll
    static void prepareTestProject(@TempDir File temporaryDirectory) throws Exception {
        projectFile = new File(temporaryDirectory, "project");

        var sourceFolder = Path.of("src/test/resources", PROJECT_FOLDER);
        copyFolder(sourceFolder, projectFile.toPath());

        buildMavenTestProject();
    }

    static Stream<Arguments> provideFormatsAndExpectedOutput() {
        return Stream.of(Arguments.of("html", "index.html"),
                Arguments.of("asciidoc", "index.asciidoc"),
                Arguments.of("text", "testpackage.ThingDoerTest.feature")
        );
    }

    @ParameterizedTest
    @MethodSource("provideFormatsAndExpectedOutput")
    void should_determine_output_dir_based_on_format(String format, String expectedOutput) throws MojoExecutionException {
        var mavenMojo = new JGivenReportMojo(
                new File(projectFile, "target"),
                null,
                new File(projectFile, "target/jgiven-reports/json"),
                new File(projectFile, "src/test/resources/jgiven/custom.css"),
                new File(projectFile, "src/test/resources/jgiven/custom.js"),
                format,
                "JGiven Report",
                false,
                true
        );

        mavenMojo.execute();

        assertThat(new File(projectFile, format("target/jgiven-reports/%s/%s",format, expectedOutput))).exists();
    }
    @Test
    void should_prefer_writing_to_explicit_output_directory(@TempDir File outputDir) throws MojoExecutionException {
        var mavenMojo = new JGivenReportMojo(
                new File(projectFile, "target"),
                outputDir,
                new File(projectFile, "target/jgiven-reports/json"),
                new File(projectFile, "src/test/resources/jgiven/custom.css"),
                new File(projectFile, "src/test/resources/jgiven/custom.js"),
                "html",
                "JGiven Report",
                false,
                true
        );

        mavenMojo.execute();

        assertThat(new File(outputDir, "index.html")).exists();
    }

    private static void copyFolder(Path src, Path dest) throws IOException {
        try (Stream<Path> stream = Files.walk(src)) {
            stream.forEach(source -> copy(source, dest.resolve(src.relativize(source))));
        }
    }

    private static void copy(Path source, Path dest) {
        try {
            Files.copy(source, dest, REPLACE_EXISTING);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    private static void buildMavenTestProject() throws MavenInvocationException {
        var mavenExecutable = findMavenExecutable();

        InvocationRequest request = new DefaultInvocationRequest();
        request.setInputStream(InputStream.nullInputStream());
        request.setBatchMode(true);
        request.addArg("-Dorg.slf4j.simpleLogger.log.org.apache.maven.cli.transfer.Slf4jMavenTransferListener=warn");
        request.setPomFile(new File(projectFile, "pom.xml"));
        request.setGoals(List.of("verify"));
        request.setOutputHandler(System.out::println);

        Invoker invoker = new DefaultInvoker();
        invoker.setMavenExecutable(mavenExecutable);
        invoker.execute(request);
    }


    private static File findMavenExecutable() {
        return System.getenv().keySet().stream()
                .filter(key -> key.equalsIgnoreCase("PATH"))
                .map(key -> System.getenv().get(key))
                .flatMap(path -> Stream.of(path.split(System.getProperty("path.separator"))))
                .map(File::new)
                .map(file -> file.listFiles((__, name) -> name.equals("mvn")))
                .filter(files -> files != null && files.length > 0)
                .flatMap(Stream::of)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No maven executable found on the system path"));
    }

}
