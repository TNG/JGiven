package com.tngtech.jgiven.gradle;

import com.tngtech.jgiven.report.ReportGenerator;
import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.CacheableTask;
import org.gradle.api.tasks.InputDirectory;
import org.gradle.api.tasks.OutputDirectory;
import org.gradle.api.tasks.PathSensitive;
import org.gradle.api.tasks.PathSensitivity;
import org.gradle.api.tasks.SkipWhenEmpty;
import org.gradle.api.tasks.TaskAction;

import java.io.File;

@CacheableTask
public class JGivenReport extends DefaultTask {
    private File results;
    private File destination;

    @InputDirectory
    @SkipWhenEmpty
    @PathSensitive( PathSensitivity.NONE )
    public File getResults() {
        return results;
    }

    public void setResults( File results ) {
        this.results = results;
    }

    @TaskAction
    public void generate() throws Exception {
        ReportGenerator generator = new ReportGenerator();
        generator.setTargetDirectory( getDestination() );
        generator.setSourceDirectory( getResults() );
        generator.generate();
    }

    @OutputDirectory
    public File getDestination() {
        return destination;
    }

    public void setDestination( File destination ) {
        this.destination = destination;
    }
}
