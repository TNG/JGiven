package com.tngtech.jgiven.config;

import java.lang.annotation.Annotation;
import java.util.List;

import com.google.common.collect.Lists;
import com.tngtech.jgiven.annotation.DefaultTagDescriptionGenerator;
import com.tngtech.jgiven.annotation.TagDescriptionGenerator;

/**
 * Represents the configuration of a tag.
 * 
 * @see com.tngtech.jgiven.annotation.IsTag for a documentation of the different values.
 */
public class TagConfiguration {
    private final String annotationType;
    private boolean ignoreValue;
    private boolean explodeArray = true;
    private boolean prependType;
    private String defaultValue = "";
    private String description = "";
    private String color = "";
    private String cssClass = "";
    private Class<? extends TagDescriptionGenerator> descriptionGenerator = DefaultTagDescriptionGenerator.class;
    private String name = "";
    private List<String> tags = Lists.newArrayList();

    public TagConfiguration( Class<? extends Annotation> tagAnnotation ) {
        this.annotationType = tagAnnotation.getSimpleName();
    }

    public static Builder builder( Class<? extends Annotation> tagAnnotation ) {
        return new Builder( new TagConfiguration( tagAnnotation ) );
    }

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

        /**
         * @deprecated use {@link #name(String)} instead
         */
        @Deprecated
        public Builder type( String s ) {
            configuration.name = s;
            return this;
        }

        public Builder name( String s ) {
            configuration.name = s;
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

        public Builder tags( List<String> tags ) {
            configuration.tags = tags;
            return this;
        }

        public TagConfiguration build() {
            return configuration;
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
     * @deprecated use {@link #getName()} instead
     * @see com.tngtech.jgiven.annotation.IsTag
     */
    @Deprecated
    public String getType() {
        return name;
    }

    /**
     * {@link com.tngtech.jgiven.annotation.IsTag#name()}
     * @see com.tngtech.jgiven.annotation.IsTag
     */
    public String getName() {
        return name;
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

    public List<String> getTags() {
        return tags;
    }

    public String getAnnotationType() {
        return annotationType;
    }

}
