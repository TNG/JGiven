package com.tngtech.jgiven.report.model;

import com.google.common.base.Objects;

/**
 * Represents a part of a step.
 */
public class Word {
    /**
     * The string representation of this word formatted with the default string formatter.
     */
    private String value;

    /**
     * Whether this word is an introduction word.
     * <p>
     * Typical English introduction words are given, when, then, and, but
     * <p>
     * Is {@code null} if false to avoid unneeded entries in the JSON model
     * </p>
     */
    private Boolean isIntroWord;

    /**
     * Is set when this word is an argument, is <code>null</code> otherwise.
     */
    private ArgumentInfo argumentInfo;

    /**
     * Whether this word does not appear in all cases of the scenario.
     * This is can be used to highlight this word in the report.
     * <p>
     * Is {@code null} if false to avoid unneeded entries in the JSON model
     * </p>
     */
    private Boolean isDifferent;

    public Word() {}

    public Word( String value ) {
        this.setValue( value );
    }

    public Word( String value, boolean isIntroWord ) {
        this.setValue( value );
        this.setIntroWord( isIntroWord );
    }

    public static Word argWord( String argumentName, String value, String formattedValue ) {
        Word word = new Word( value );
        word.argumentInfo = new ArgumentInfo();
        word.argumentInfo.setArgumentName( argumentName );
        word.argumentInfo.setFormattedValue( formattedValue );
        return word;
    }

    public static Word argWord( String argumentName, String value, DataTable dataTable ) {
        Word word = new Word( value );
        word.argumentInfo = new ArgumentInfo();
        word.argumentInfo.setArgumentName( argumentName );
        word.argumentInfo.setDataTable( dataTable );
        return word;
    }

    public String getFormattedValue() {
        if( isArg() && getArgumentInfo().getFormattedValue() != null ) {
            return getArgumentInfo().getFormattedValue();
        }
        return getValue();
    }

    public static Word introWord( String value ) {
        return new Word( value, true );
    }

    public void setValue( String name ) {
        value = name;
    }

    public void append( CharSequence word ) {
        setValue( getValue() + " " + word );
    }

    public boolean isArg() {
        return argumentInfo != null;
    }

    public ArgumentInfo getArgumentInfo() {
        return argumentInfo;
    }

    @Override
    public String toString() {
        return getValue();
    }

    @Override
    public int hashCode() {
        return Objects.hashCode( isIntroWord(), getValue(), argumentInfo );
    }

    @Override
    public boolean equals( Object obj ) {
        if( this == obj ) {
            return true;
        }
        if( obj == null ) {
            return false;
        }
        if( getClass() != obj.getClass() ) {
            return false;
        }
        Word other = (Word) obj;
        return Objects.equal( isIntroWord(), other.isIntroWord() ) &&
                Objects.equal( getValue(), other.getValue() ) &&
                Objects.equal( argumentInfo, other.argumentInfo );
    }

    public String getValue() {
        return value;
    }

    public boolean isIntroWord() {
        return isIntroWord != null;
    }

    public void setIntroWord( boolean isIntroWord ) {
        this.isIntroWord = isIntroWord ? true : null;
    }

    public void setIsDifferent( boolean b ) {
        this.isDifferent = b ? true : null;
    }

    public boolean isDifferent() {
        return isDifferent != null;
    }

    public boolean isDataTable() {
        return isArg() && getArgumentInfo().isDataTable();
    }
}
