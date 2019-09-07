package com.tngtech.jgiven.junit.lang.de;

import org.junit.Test;

import com.tngtech.jgiven.junit.lang.de.DeSzenarioTest.DeutscheTestStufe;
import com.tngtech.jgiven.lang.de.Stufe;
import com.tngtech.jgiven.tags.FeatureGerman;

@FeatureGerman
public class DeEinfacherSzenarioTest extends EinfacherSzenarioTest<DeutscheTestStufe> {

    @Test
    public void Szenarien_können_in_deutsch_geschrieben_werden() {
        gegeben().ein_deutsches_Projekt();
        wenn().JGiven_verwendet_wird()
            .und().die_Szenarien_in_deutsch_geschrieben_werden();
        dann().generiert_JGiven_deutsche_Berichte();
    }

}
