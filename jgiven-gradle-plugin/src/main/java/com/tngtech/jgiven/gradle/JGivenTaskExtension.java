package com.tngtech.jgiven.gradle;

import org.gradle.api.file.DirectoryProperty;

public interface JGivenTaskExtension {
    DirectoryProperty getResultsDir();

}
