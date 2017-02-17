package com.tngtech.jgiven.report.config;

import java.util.List;

/**
 *  Defines a command line interface for use in {@link ConfigOption} with automatic help description generation used by {@link ConfigOptionParser#printUsageAndExit(List)}
 *  Instantiation through {@link CommandLineOptionBuilder}
 *
 */
public class CommandLineOption {
    private String delimiter;
    private boolean hasShortFlag = false;
    private String shortPrefix;
    private String longPrefix;
    private String placeholder;
    private boolean hasArgument = false;

    public void setArgumentDelimiter( String delimiter ) {
        this.delimiter = delimiter;
        this.hasArgument = true;
    }

    public String getDelimiter() {
        if( delimiter != null ) {
            return delimiter;
        } else {
            return "";
        }
    }

    public boolean hasShortFlag() {
        return hasShortFlag;
    }

    public void setShortPrefix( String shortPrefix ) {
        this.shortPrefix = shortPrefix;
        this.hasShortFlag = true;
    }

    public void setLongPrefix( String longPrefix ) {
        this.longPrefix = longPrefix;
    }

    public boolean hasArgument() {
        return hasArgument;
    }

    public String getShortFlag() {
        return shortPrefix + getDelimiter();
    }

    public String getLongFlag() {
        return longPrefix + getDelimiter();
    }

    public void setPlaceholder( String placeholder ) {
        this.placeholder = placeholder;
    }

    public String getPlaceholder() {
        if( placeholder != null ) {
            return "<" + placeholder + ">";
        } else {
            return "";
        }
    }

    public String showFlagInfo() {
        String shortFlag = hasShortFlag() ? " / " + getShortFlag() : "";
        return getLongFlag() + shortFlag + ( placeholder != null ? getPlaceholder() : "" );
    }
}
