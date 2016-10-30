package com.tngtech.jgiven.gradle;

import java.io.File;

public class JGivenTaskExtension {
    private File resultsDir;

    public File getResultsDir() {
        return resultsDir;
    }

    public void setResultsDir( File resultsDir ) {
        this.resultsDir = resultsDir;
    }
}
