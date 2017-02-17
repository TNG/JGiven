package com.tngtech.jgiven.report.config;

import com.tngtech.jgiven.report.config.converter.StringConverter;

import java.util.Map;

/**
 * Defines a configuration for the report which can be from the command line, property or the environment
 * Instantiated with {@link ConfigOptionBuilder}
 * For use in {@link com.tngtech.jgiven.report.AbstractReportGenerator#addConfigOptions(ConfigOption...)} and the generated object map in {@link com.tngtech.jgiven.report.AbstractReportConfig#AbstractReportConfig(Map)}
 */
public class ConfigOption {

    private String longName;
    private String shortName;
    private CommandLineOption commandLineOption;
    private String propertyString;
    private String envString;

    private String description;
    private boolean optional = false;
    private boolean hasArgument = false;
    private boolean hasDefault = false;
    private Object value;
    private StringConverter converter;

    public String getLongName() {
        return longName;
    }

    public void setLongName( String longName ) {
        this.longName = longName;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName( String shortName ) {
        this.shortName = shortName;
    }

    public CommandLineOption getCommandLineOption() {
        return commandLineOption;
    }

    public void setCommandLineOption( CommandLineOption commandLineOption ) {
        this.commandLineOption = commandLineOption;
    }

    public String getPropertyString() {
        return propertyString;
    }

    public void setPropertyString( String propertyString ) {
        this.propertyString = propertyString;
    }

    public String getEnvString() {
        return envString;
    }

    public void setEnvString( String envString ) {
        this.envString = envString;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription( String description ) {
        this.description = description;
    }

    public boolean isOptional() {
        return optional;
    }

    public void setOptional( boolean optional ) {
        this.optional = optional;
    }

    public boolean isHasArgument() {
        return hasArgument;
    }

    public void setHasArgument( boolean hasArgument ) {
        this.hasArgument = hasArgument;
    }

    public boolean hasDefault() {
        return hasDefault;
    }

    public void setHasDefault( boolean hasDefault ) {
        this.hasDefault = hasDefault;
    }

    public Object getValue() {
        return value;
    }

    public void setValue( Object value ) {
        this.value = value;
    }

    public StringConverter getConverter() {
        return converter;
    }

    public void setConverter( StringConverter converter ) {
        this.converter = converter;
    }

    public Object toObject( String input ) {
        return converter.apply( input );
    }

    public String getEnhancedDescription() {
        return description + ( isOptional() ? " (optional)" : "" );
    }

}
