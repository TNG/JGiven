package com.tngtech.jgiven.lang.de;

import com.tngtech.jgiven.Scenario;

public class Szenario<GEGEBEN, WENN, DANN> extends Scenario<GEGEBEN, WENN, DANN> {

    public GEGEBEN gegeben() {
        return given( "Gegeben" );
    }

    public WENN wenn() {
        return when( "Wenn" );
    }

    public DANN dann() {
        return then( "Dann" );
    }
}
