package com.tngtech.jgiven.gradle;

import org.gradle.api.provider.Property;

import java.io.File;

public interface JGivenTaskExtension {
    Property<File> getResultsDir();

}
