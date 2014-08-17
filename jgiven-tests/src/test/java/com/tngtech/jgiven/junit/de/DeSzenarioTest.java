package com.tngtech.jgiven.junit.de;

import org.junit.Test;

import com.tngtech.jgiven.junit.de.DeSzenarioTest.DeutscheTestStufe;
import com.tngtech.jgiven.lang.de.Stufe;
import com.tngtech.jgiven.tags.FeatureGerman;

@FeatureGerman
public class DeSzenarioTest extends SzenarioTest<DeutscheTestStufe, DeutscheTestStufe, DeutscheTestStufe> {

    @Test
    public void Szenarien_können_in_deutsch_geschrieben_werden() {
        gegeben().ein_deutsches_Projekt();
        wenn().JGiven_verwendet_wird()
            .und().die_Szenarien_in_deutsch_geschrieben_werden();
        dann().generiert_JGiven_deutsche_Berichte();
    }

    static class DeutscheTestStufe extends Stufe<DeutscheTestStufe> {

        public DeutscheTestStufe ein_deutsches_Projekt() {
            return self();
        }

        public DeutscheTestStufe generiert_JGiven_deutsche_Berichte() {
            return self();
        }

        public DeutscheTestStufe die_Szenarien_in_deutsch_geschrieben_werden() {
            return self();
        }

        public DeutscheTestStufe JGiven_verwendet_wird() {
            return self();
        }

    }
}
