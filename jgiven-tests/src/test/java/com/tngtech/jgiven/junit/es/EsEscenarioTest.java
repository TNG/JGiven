package com.tngtech.jgiven.junit.es;

import org.junit.Test;

import com.tngtech.jgiven.junit.es.EsEscenarioTest.FaseEspannolTest;
import com.tngtech.jgiven.lang.es.Fase;
import com.tngtech.jgiven.tags.FeatureSpanish;

@FeatureSpanish
public class EsEscenarioTest extends EscenarioTest<FaseEspannolTest, FaseEspannolTest, FaseEspannolTest> {

    @Test
    public void los_escenarios_se_pueden_escribir_en_espannol() {
    	
    	dado().un_proyecto_en_espannol();
    	
    	cuando().se_usa_JGiven()
    		.y().los_escenarios_se_escriben_en_espannol();
    	
    	entonces().JGiven_genera_los_informes_en_espannol();

    }

    static class FaseEspannolTest extends Fase<FaseEspannolTest> {

        public FaseEspannolTest un_proyecto_en_espannol() {
            return self();
        }

        public FaseEspannolTest se_usa_JGiven() {
        	return self();
        }

        public FaseEspannolTest los_escenarios_se_escriben_en_espannol() {
        	return self();
        }

        public FaseEspannolTest JGiven_genera_los_informes_en_espannol() {
            return self();
        }
    }
}

