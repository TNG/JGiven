package com.tngtech.jgiven.report.asciidoc;

record FeatureName(String value) {
    static FeatureName feature(String featureName) {
        return new FeatureName(featureName);
    }

    @Override
    public final String toString() {
        return value.toString();
    }
}
