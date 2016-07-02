package com.tngtech.jgiven.config;

import com.google.common.collect.Lists;
import com.tngtech.jgiven.annotation.TagDescriptionGenerator;
import com.tngtech.jgiven.annotation.TagHrefGenerator;
import com.tngtech.jgiven.impl.tag.DefaultTagDescriptionGenerator;
import com.tngtech.jgiven.impl.tag.DefaultTagHrefGenerator;

import java.lang.annotation.Annotation;
import java.util.List;

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
    private String style = "";
    private Class<? extends TagDescriptionGenerator> descriptionGenerator = DefaultTagDescriptionGenerator.class;
    private String name = "";
    private List<String> tags = Lists.newArrayList();
    private String href = "";
    private Class<? extends TagHrefGenerator> hrefGenerator = DefaultTagHrefGenerator.class;
    private boolean showInNavigation = true;

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

        public Builder style( String style ) {
            configuration.style = style;
            return this;
        }

        public Builder tags( List<String> tags ) {
            configuration.tags = tags;
            return this;
        }

        public Builder href( String s ) {
            configuration.href = s;
            return this;
        }

        public Builder hrefGenerator( Class<? extends TagHrefGenerator> hrefGenerator ) {
            configuration.hrefGenerator = hrefGenerator;
            return this;
        }

        public Builder showInNavigation( boolean value ) {
            configuration.showInNavigation = value;
            return this;
        }

        public TagConfiguration build() {
            return configuration;
        }

    }

    /**
     * @see com.tngtech.jgiven.annotation.IsTag#value
     */
    public String getDefaultValue() {
        return defaultValue;
    }

    /**
     * @see com.tngtech.jgiven.annotation.IsTag#description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @see com.tngtech.jgiven.annotation.IsTag#descriptionGenerator
     */
    public Class<? extends TagDescriptionGenerator> getDescriptionGenerator() {
        return descriptionGenerator;
    }

    /**
     * @deprecated use {@link #getName()} instead
     * @see com.tngtech.jgiven.annotation.IsTag#type
     */
    @Deprecated
    public String getType() {
        return name;
    }

    /**
     * @see com.tngtech.jgiven.annotation.IsTag#name
     */
    public String getName() {
        return name;
    }

    /**
     * @see com.tngtech.jgiven.annotation.IsTag#explodeArray
     */
    public boolean isExplodeArray() {
        return explodeArray;
    }

    /**
     * @see com.tngtech.jgiven.annotation.IsTag#ignoreValue
     */
    public boolean isIgnoreValue() {
        return ignoreValue;
    }

    /**
     * @see com.tngtech.jgiven.annotation.IsTag#prependType
     */
    public boolean isPrependType() {
        return prependType;
    }

    /**
     * @see com.tngtech.jgiven.annotation.IsTag#color
     */
    public String getColor() {
        return color;
    }

    /**
     * @see com.tngtech.jgiven.annotation.IsTag#style
     */
    public String getStyle() {
        return style;
    }

    /**
     * @see com.tngtech.jgiven.annotation.IsTag#cssClass
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

    /**
     * @see com.tngtech.jgiven.annotation.IsTag#href
     */
    public String getHref() {
        return href;
    }

    /**
     * @see com.tngtech.jgiven.annotation.IsTag#hrefGenerator
     */
    public Class<? extends TagHrefGenerator> getHrefGenerator() {
        return hrefGenerator;
    }

    /**
     * @see com.tngtech.jgiven.annotation.IsTag#showInNavigation
     */
    public boolean showInNavigation() {
        return showInNavigation;
    }

}
