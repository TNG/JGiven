package com.tngtech.jgiven.lang.de;

import com.tngtech.jgiven.base.ScenarioTestBase;

public class SzenarioTestBasis<GEGEBEN, WENN, DANN> extends ScenarioTestBase<GEGEBEN, WENN, DANN> {

    public GEGEBEN gegeben() {
        return getScenario().given( "Gegeben" );
    }

    public WENN wenn() {
        return getScenario().when( "Wenn" );
    }

    public DANN dann() {
        return getScenario().then( "Dann" );
    }

}
