package com.tngtech.jgiven.report.config;

import com.tngtech.jgiven.report.config.converter.StringConverter;

import java.util.List;

/**
 * An easier interface to create {@link ConfigOption} for use in {@link com.tngtech.jgiven.report.AbstractReportGenerator#additionalConfigOptions(List)}
 */
public class ConfigOptionBuilder {

    private ConfigOption co;

    public ConfigOptionBuilder( String longName ) {
        co = new ConfigOption();
        co.setLongName( longName );
    }

    public ConfigOptionBuilder setShortName( String shortName ) {
        co.setShortName( shortName );
        return this;
    }

    /**
     * if you want to parse an argument, you need a converter from String to Object
     *
     * @param commandLineOption specification of the command line options
     * @param converter how to convert your String value to a castable Object
     */
    public ConfigOptionBuilder setCommandLineOptionWithArgument( CommandLineOption commandLineOption, StringConverter converter ) {
        co.setCommandLineOption( commandLineOption );
        return setStringConverter( converter );
    }

    /**
     * if you don't have an argument, choose the value that is going to be inserted into the map instead
     *
     * @param commandLineOption specification of the command line options
     * @param value the value that is going to be inserted into the map instead of the argument
     */
    public ConfigOptionBuilder setCommandLineOptionWithoutArgument( CommandLineOption commandLineOption, Object value ) {
        co.setCommandLineOption( commandLineOption );
        co.setValue( value );
        return this;
    }

    public ConfigOptionBuilder setPropertyString( String propertyString, StringConverter converter ) {
        co.setPropertyString( propertyString );
        co.setConverter( converter );
        co.setHasArgument( true ); // TODO check if there are properties without arguments
        return this;
    }

    public ConfigOptionBuilder setEnvironmentString( String envString, StringConverter converter ) {
        co.setEnvString( envString );
        co.setConverter( converter );
        co.setHasArgument( true );
        return this;
    }

    public ConfigOptionBuilder setDescription( String description ) {
        co.setDescription( description );
        return this;
    }

    /**
     * if the option is optional, you don't have to use it
     */
    public ConfigOptionBuilder setOptional() {
        co.setOptional( true );
        return this;
    }

    /**
     * if you have a default, it's automatically optional
     */
    public ConfigOptionBuilder setDefaultWith( Object defaultValue ) {
        co.setHasDefault( true );
        co.setValue( defaultValue );
        return setOptional();
    }

    /**
     * if you want to convert some string to an object, you have an argument to parse
     */
    public ConfigOptionBuilder setStringConverter( StringConverter converter ) {
        co.setConverter( converter );
        co.setHasArgument( true );
        return this;
    }

    public ConfigOption build() {
        return co;
    }

}
