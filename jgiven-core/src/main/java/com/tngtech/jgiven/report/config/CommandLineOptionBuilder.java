package com.tngtech.jgiven.report.config;

/**
 * An easier interface to instantiate {@link CommandLineOption} for use in {@link ConfigOption}
 */
public class CommandLineOptionBuilder {
    private CommandLineOption clo;

    public CommandLineOptionBuilder( String longPrefix ) {
        clo = new CommandLineOption();
        clo.setLongPrefix( longPrefix );
    }

    public CommandLineOptionBuilder setArgumentDelimiter( String delimiter ) {
        clo.setArgumentDelimiter( delimiter );
        return this;
    }

    public CommandLineOptionBuilder setShortPrefix( String shortPrefix ) {
        clo.setShortPrefix( shortPrefix );
        return this;
    }

    public CommandLineOptionBuilder setVisualPlaceholder( String placeholder ) {
        clo.setPlaceholder( placeholder );
        return this;
    }

    public CommandLineOption build() {
        return clo;
    }
}
