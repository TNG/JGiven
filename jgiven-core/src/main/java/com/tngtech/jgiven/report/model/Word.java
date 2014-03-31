package com.tngtech.jgiven.report.model;

public class Word {
    public String value;
    public boolean isArg;
    public boolean isIntroWord;

    public Word( String value ) {
        this.value = value;
    }

    public Word() {}

    public void append( String word ) {
        value += " " + word;
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ( isArg ? 1231 : 1237 );
        result = prime * result + ( value == null ? 0 : value.hashCode() );
        return result;
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
        if( isArg != other.isArg )
            return false;
        if( value == null ) {
            if( other.value != null )
                return false;
        } else if( !value.equals( other.value ) )
            return false;
        return true;
    }
}