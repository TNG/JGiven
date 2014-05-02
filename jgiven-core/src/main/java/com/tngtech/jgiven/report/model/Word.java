package com.tngtech.jgiven.report.model;

import com.google.common.base.Objects;

public class Word {
    public String value;
    public boolean isIntroWord;

    public Word() {}

    public Word( String value ) {
        this.value = value;
    }

    public Word( String value, boolean isIntroWord ) {
        this.value = value;
        this.isIntroWord = isIntroWord;
    }

    public static ArgumentWord argWord( String value ) {
        return new ArgumentWord( value );
    }

    public static Word introWord( String value ) {
        return new Word( value, true );
    }

    public void append( String word ) {
        value += " " + word;
    }

    public boolean isArg() {
        return false;
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode( isIntroWord, value );
    }

    @Override
    public boolean equals( Object obj ) {
        if( this == obj )
            return true;
        if( obj == null )
            return false;
        if( getClass() != obj.getClass() )
            return false;
        Word other = (Word) obj;
        return Objects.equal( isIntroWord, other.isIntroWord ) &&
                Objects.equal( value, other.value );
    }
}
