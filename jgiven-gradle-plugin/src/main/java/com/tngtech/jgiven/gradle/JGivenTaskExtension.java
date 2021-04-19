package com.tngtech.jgiven.gradle;

import java.io.File;
import org.gradle.api.provider.Provider;

public class JGivenTaskExtension {
    private Provider<File> resultsDir;

    public Provider<File> getResultsDir() {
        return resultsDir;
    }

    public void setResultsDir( Provider<File> resultsDir ) {
        this.resultsDir = resultsDir;
    }
}
