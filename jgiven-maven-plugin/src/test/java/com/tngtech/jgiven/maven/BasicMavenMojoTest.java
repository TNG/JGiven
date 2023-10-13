package com.tngtech.jgiven.maven;

import org.apache.maven.shared.invoker.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assumptions.assumeThat;

class BasicMavenMojoTest {

    private static final String PROJECT_FOLDER = "sampleProject"; //name is also used in renovate.json
    @TempDir
    public Path temporaryDirectory;

    @BeforeEach
    void copyResourcesToTemporaryDirectory() throws URISyntaxException, IOException {
        var sourceFolder = Path.of("src/test/resources" , PROJECT_FOLDER);
        copyFolder(sourceFolder, temporaryDirectory);
    }

    @Test
    void testMavenProducesHtmlAndAsciiReport() throws MavenInvocationException {
        var mavenExecutable= this.findMavenExecutable();
        assumeThat(mavenExecutable).isNotEmpty();

        InvocationRequest request = new DefaultInvocationRequest();
        request.setInputStream(InputStream.nullInputStream());
        request.setPomFile(new File(temporaryDirectory.toFile(),"pom.xml"));
        request.setGoals(List.of("install"));
        request.setOutputHandler(System.out::println);

        Invoker invoker = new DefaultInvoker();
        invoker.setMavenExecutable(mavenExecutable.orElseThrow());
        InvocationResult result = invoker.execute(request);

        assertThat(result.getExitCode()).isZero();
        assertThat(new File(temporaryDirectory.toFile(), "target/jgiven-reports/html/index.html")).exists();
        assertThat(new File(temporaryDirectory.toFile(), "target/jgiven-reports/asciidoc/index.asciidoc")).exists();
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

    private Optional<File> findMavenExecutable(){
        return System.getenv().keySet().stream()
                .filter(key -> key.equalsIgnoreCase("PATH"))
                .map(key -> System.getenv().get(key))
                .flatMap(path -> Stream.of(path.split(System.getProperty("path.separator"))))
                .filter(path -> path.contains("maven"))
                .map(File::new)
                .map(file -> file.listFiles((__, name) -> name.equals("mvn")))
                .filter(files -> files != null && files.length > 0)
                .flatMap(Stream::of)
                .findFirst();
    }

}
