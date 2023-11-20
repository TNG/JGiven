package com.tngtech.jgiven.maven;

import org.apache.maven.shared.invoker.*;
import org.gradle.testkit.runner.GradleRunner;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static org.assertj.core.api.Assertions.assertThat;

class BasicMavenMojoTest {

    private static final String PROJECT_FOLDER = "sampleProject"; //name is also used in renovate.json
    @TempDir
    public Path temporaryDirectory;

    @BeforeEach
    void copyResourcesToTemporaryDirectory() throws IOException {
        var sourceFolder = Path.of("src/test/resources" , PROJECT_FOLDER);
        copyFolder(sourceFolder, temporaryDirectory);
    }

    @BeforeEach
    void publishPluginVersionToMavenLocal()throws IOException {
        GradleRunner.create().withProjectDir(new File(System.getProperty("user.dir")))
                .withArguments("publishToMavenLocal", "-x", "test") //don't test, or we'll loop infinitely
                .withArguments("-x", "signPluginMavenPublication") //may break releases.
                .build();
    }

    @Test
    void testMavenProducesHtmlAndAsciiReport() throws MavenInvocationException {
        var mavenExecutable= this.findMavenExecutable();

        InvocationRequest request = new DefaultInvocationRequest();
        request.setInputStream(InputStream.nullInputStream());
        request.setBatchMode(true);
        request.addArg("-Dorg.slf4j.simpleLogger.log.org.apache.maven.cli.transfer.Slf4jMavenTransferListener=warn");
        request.setPomFile(new File(temporaryDirectory.toFile(),"pom.xml"));
        request.setGoals(List.of("verify"));
        request.setOutputHandler(System.out::println);

        Invoker invoker = new DefaultInvoker();
        invoker.setMavenExecutable(mavenExecutable);
        InvocationResult result = invoker.execute(request);

        assertThat(result.getExitCode()).isZero();
        assertThat(new File(temporaryDirectory.toFile(), "target/jgiven-reports/html/index.html")).exists();
        assertThat(new File(temporaryDirectory.toFile(), "target/jgiven-reports/asciidoc/index.asciidoc")).exists();
        assertThat(new File(temporaryDirectory.toFile(), "target/jgiven-reports/text")).exists();
    }

    private void copyFolder(Path src, Path dest) throws IOException {
        try (Stream<Path> stream = Files.walk(src)) {
            stream.forEach(source -> copy(source, dest.resolve(src.relativize(source))));
        }
    }

    private void copy(Path source, Path dest) {
        try {
            Files.copy(source, dest, REPLACE_EXISTING);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    private File findMavenExecutable(){
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
