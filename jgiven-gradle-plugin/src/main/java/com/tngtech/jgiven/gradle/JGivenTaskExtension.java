package com.tngtech.jgiven.gradle;

import java.io.File;
import org.gradle.api.provider.Provider;

public class JGivenTaskExtension {
    private Object resultsDir;

    public Object getResultsDir() {
        return resultsDir;
    }

    public void setResultsDir( Provider<File> resultsDir ) {
        this.resultsDir = resultsDir;
    }
    public void setResultsDir( File resultsDir ) {
        this.resultsDir = resultsDir;
    }
}
