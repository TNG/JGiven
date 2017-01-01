package com.tngtech.jgiven.impl.util;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class VersionTest {

    @Test
    public void version_strings_are_substituted() {
        assertThat( Version.VERSION.toString()).doesNotContain("$");
    }
}
