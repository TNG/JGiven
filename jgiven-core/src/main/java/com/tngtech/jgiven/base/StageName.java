package com.tngtech.jgiven.base;

import java.util.Objects;

/**
 * Wrapper class for the name held in the stage objects.
 * Helps when making a single stage act as the default three stages.
 */
public class StageName {
    private String stageName = null;

    public StageName(String stageName) {
        this.stageName = stageName;
    }

    public String getStageName() {
        return stageName;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        StageName that = (StageName) o;
        return Objects.equals(stageName, that.stageName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(stageName);
    }

    @Override
    public String toString() {
        return "StageName{" +
                "stageName='" + stageName + '\'' +
                '}';
    }
}
