package com.tngtech.jgiven.report.model;

import com.google.common.base.Objects;

public class Word {
    public String value;
    public boolean isArg;
    public boolean isIntroWord;

    public Word() {}

    public Word( String value ) {
        this.value = value;
    }

    public Word( String value, boolean isArg, boolean isIntroWord ) {
        this.value = value;
        this.isArg = isArg;
        this.isIntroWord = isIntroWord;
    }

    public static Word argWord( String value ) {
        return new Word( value, true, false );
    }

    public static Word introWord( String value ) {
        return new Word( value, false, true );
    }

    public void append( String word ) {
        value += " " + word;
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode( isArg, isIntroWord, value );
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
        return Objects.equal( isArg, other.isArg ) &&
                Objects.equal( isIntroWord, other.isIntroWord ) &&
                Objects.equal( value, other.value );
    }
}
