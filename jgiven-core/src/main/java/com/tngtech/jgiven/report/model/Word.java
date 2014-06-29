package com.tngtech.jgiven.report.model;

import com.google.common.base.Objects;

public class Word {
    public String value;
    public boolean isIntroWord;

    /**
     * Is set when this word is an argument, is <code>null</code> otherwise
     */
    private ArgumentInfo argumentInfo;

    public Word() {}

    public Word( String value ) {
        this.value = value;
    }

    public Word( String value, boolean isIntroWord ) {
        this.value = value;
        this.isIntroWord = isIntroWord;
    }

    public static Word argWord( String value ) {
        Word word = new Word( value );
        word.argumentInfo = new ArgumentInfo();
        return word;
    }

    public static Word introWord( String value ) {
        return new Word( value, true );
    }

    public void setValue( String name ) {
        value = name;
    }

    public void append( String word ) {
        value += " " + word;
    }

    public boolean isArg() {
        return argumentInfo != null;
    }

    public ArgumentInfo getArgumentInfo() {
        return argumentInfo;
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode( isIntroWord, value, argumentInfo );
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
        return Objects.equal( isIntroWord, other.isIntroWord ) &&
                Objects.equal( value, other.value ) &&
                Objects.equal( argumentInfo, other.argumentInfo );
    }

}
