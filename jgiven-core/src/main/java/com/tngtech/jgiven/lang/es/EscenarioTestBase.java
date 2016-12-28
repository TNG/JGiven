package com.tngtech.jgiven.lang.es;

import com.tngtech.jgiven.base.ScenarioTestBase;

public abstract class EscenarioTestBase<DADO, CUANDO, ENTONCES> extends ScenarioTestBase<DADO, CUANDO, ENTONCES> {

    public DADO dado() {
        return getScenario().given( "Dado" );
    }
    
    public DADO dada() {
        return getScenario().given( "Dada" );
    }

    public CUANDO cuando() {
        return getScenario().when( "Cuando" );
    }

    public ENTONCES entonces() {
        return getScenario().then( "Entonces" );
    }

}
