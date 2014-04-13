package com.tngtech.jgiven.junit.de;

import org.junit.Test;

import com.tngtech.jgiven.junit.de.DeSzenarioTest.DeutscheTestSchritte;
import com.tngtech.jgiven.lang.de.Schritte;

public class DeSzenarioTest extends SzenarioTest<DeutscheTestSchritte, DeutscheTestSchritte, DeutscheTestSchritte> {

    @Test
    public void Szenarien_können_in_deutsch_geschrieben_werden() {
        gegeben().ein_deutsches_Projekt();
        wenn().JGiven_verwendet_wird()
            .und().die_Szenarien_in_deutsch_geschrieben_werden();
        dann().generiert_JGiven_deutsche_Berichte();
    }

    static class DeutscheTestSchritte extends Schritte<DeutscheTestSchritte> {

        public DeutscheTestSchritte ein_deutsches_Projekt() {
            return self();
        }

        public DeutscheTestSchritte generiert_JGiven_deutsche_Berichte() {
            return self();
        }

        public DeutscheTestSchritte die_Szenarien_in_deutsch_geschrieben_werden() {
            return self();
        }

        public DeutscheTestSchritte JGiven_verwendet_wird() {
            return self();
        }

    }
}
