package com.tngtech.jgiven.base;

import java.util.Objects;

/**
 * Wrapper class for the name held in the stage objects.
 * Helps when making a single stage act as the default three stages.
 */
public class StageNameWrapper {
    private String stageName = null;

    public StageNameWrapper(String stageName) {
        this.stageName = stageName;
    }

    public String getStageName() {
        return stageName;
    }

    public void setStageName(String stageName) {
        this.stageName = stageName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StageNameWrapper that = (StageNameWrapper) o;
        return Objects.equals(stageName, that.stageName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(stageName);
    }
}
