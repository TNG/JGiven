package com.tngtech.jgiven.config;

import com.tngtech.jgiven.annotation.IsTag;

public class TagConfiguration {
    boolean ignoreValue;
    boolean explodeArray = true;
    boolean prependType = false;
    String defaultValue = "";
    String description = "";
    String type = "";

    public static class Builder {
        final TagConfiguration configuration;

        Builder( TagConfiguration configuration ) {
            this.configuration = configuration;
        }

        public Builder ignoreValue( boolean b ) {
            configuration.ignoreValue = b;
            return this;
        }

        public Builder explodeArray( boolean b ) {
            configuration.explodeArray = b;
            return this;
        }

        public Builder defaultValue( String s ) {
            configuration.defaultValue = s;
            return this;
        }

        public Builder description( String s ) {
            configuration.description = s;
            return this;
        }

        public Builder type( String s ) {
            configuration.type = s;
            return this;
        }

        public Builder prependType( boolean b ) {
            configuration.prependType = b;
            return this;
        }
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public String getDescription() {
        return description;
    }

    public String getType() {
        return type;
    }

    public boolean isExplodeArray() {
        return explodeArray;
    }

    public boolean isIgnoreValue() {
        return ignoreValue;
    }

    public boolean isPrependType() {
        return prependType;
    }

    public static TagConfiguration fromIsTag( IsTag isTag ) {
        TagConfiguration result = new TagConfiguration();
        result.defaultValue = isTag.value();
        result.description = isTag.description();
        result.explodeArray = isTag.explodeArray();
        result.ignoreValue = isTag.ignoreValue();
        result.prependType = isTag.prependType();
        result.type = isTag.type();
        return result;
    }
}
