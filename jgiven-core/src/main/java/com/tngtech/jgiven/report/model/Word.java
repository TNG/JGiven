package com.tngtech.jgiven.report.model;

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
}