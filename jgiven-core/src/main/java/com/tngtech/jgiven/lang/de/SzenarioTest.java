package com.tngtech.jgiven.lang.de;

import com.tngtech.jgiven.base.ScenarioTestBase;

public class SzenarioTest<GEGEBEN, WENN, DANN> extends ScenarioTestBase<GEGEBEN, WENN, DANN> {

    public GEGEBEN gegeben() {
        return scenario.given( "Gegeben" );
    }

    public WENN wenn() {
        return scenario.when( "Wenn" );
    }

    public DANN dann() {
        return scenario.then( "Dann" );
    }

}
