package com.tngtech.jgiven.junit.lang.es;

import org.junit.Test;

import com.tngtech.jgiven.junit.lang.es.EsEscenarioTest.FaseEspannolTest;
import com.tngtech.jgiven.lang.es.Fase;
import com.tngtech.jgiven.tags.FeatureSpanish;

@FeatureSpanish
public class EsSimpleEscenarioTest extends SimpleEscenarioTest<FaseEspannolTest> {

    @Test
    public void los_escenarios_se_pueden_escribir_en_espannol() {

	dado().un_proyecto_en_espannol();

	cuando().se_usa_JGiven()
		.y().los_escenarios_se_escriben_en_espannol();

	entonces().JGiven_genera_los_informes_en_espannol();

    }

}
