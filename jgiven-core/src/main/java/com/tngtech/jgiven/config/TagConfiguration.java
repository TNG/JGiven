package com.tngtech.jgiven.config;

import com.tngtech.jgiven.annotation.DefaultTagDescriptionGenerator;
import com.tngtech.jgiven.annotation.IsTag;
import com.tngtech.jgiven.annotation.TagDescriptionGenerator;

/**
 * Represents the configuration of a tag.
 * 
 * @see com.tngtech.jgiven.annotation.IsTag for a documentation of the different values.
 */
public class TagConfiguration {
    private boolean ignoreValue;
    private boolean explodeArray = true;
    private boolean prependType;
    private String defaultValue = "";
    private String description = "";
    private String color = "";
    private String cssClass = "";
    private Class<? extends TagDescriptionGenerator> descriptionGenerator = DefaultTagDescriptionGenerator.class;
    private String type = "";

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

        public Builder descriptionGenerator( Class<? extends TagDescriptionGenerator> descriptionGenerator ) {
            configuration.descriptionGenerator = descriptionGenerator;
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

        public Builder cssClass( String cssClass ) {
            configuration.cssClass = cssClass;
            return this;
        }

        public Builder color( String color ) {
            configuration.color = color;
            return this;
        }

    }

    /**
     * {@link com.tngtech.jgiven.annotation.IsTag#value()}
     * @see com.tngtech.jgiven.annotation.IsTag
     */
    public String getDefaultValue() {
        return defaultValue;
    }

    /**
     * {@link com.tngtech.jgiven.annotation.IsTag#description()}
     * @see com.tngtech.jgiven.annotation.IsTag
     */
    public String getDescription() {
        return description;
    }

    /**
     * {@link com.tngtech.jgiven.annotation.IsTag#descriptionGenerator()}
     * @see com.tngtech.jgiven.annotation.IsTag
     */
    public Class<? extends TagDescriptionGenerator> getDescriptionGenerator() {
        return descriptionGenerator;
    }

    /**
     * {@link com.tngtech.jgiven.annotation.IsTag#type()}
     * @see com.tngtech.jgiven.annotation.IsTag
     */
    public String getType() {
        return type;
    }

    /**
     * {@link com.tngtech.jgiven.annotation.IsTag#explodeArray()}
     * @see com.tngtech.jgiven.annotation.IsTag
     */
    public boolean isExplodeArray() {
        return explodeArray;
    }

    /**
     * {@link com.tngtech.jgiven.annotation.IsTag#ignoreValue()}
     * @see com.tngtech.jgiven.annotation.IsTag
     */
    public boolean isIgnoreValue() {
        return ignoreValue;
    }

    /**
     * {@link com.tngtech.jgiven.annotation.IsTag#prependType()}
     * @see com.tngtech.jgiven.annotation.IsTag
     */
    public boolean isPrependType() {
        return prependType;
    }

    /**
     * {@link com.tngtech.jgiven.annotation.IsTag#color()} 
     * @see com.tngtech.jgiven.annotation.IsTag
     */
    public String getColor() {
        return color;
    }

    /**
     * {@link com.tngtech.jgiven.annotation.IsTag#cssClass()} 
     * @see com.tngtech.jgiven.annotation.IsTag
     */
    public String getCssClass() {
        return cssClass;
    }

    public static TagConfiguration fromIsTag( IsTag isTag ) {
        TagConfiguration result = new TagConfiguration();
        result.defaultValue = isTag.value();
        result.description = isTag.description();
        result.explodeArray = isTag.explodeArray();
        result.ignoreValue = isTag.ignoreValue();
        result.prependType = isTag.prependType();
        result.type = isTag.type();
        result.descriptionGenerator = isTag.descriptionGenerator();
        result.cssClass = isTag.cssClass();
        result.color = isTag.color();
        return result;
    }
}
