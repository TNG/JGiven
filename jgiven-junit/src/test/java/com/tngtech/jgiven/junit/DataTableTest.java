package com.tngtech.jgiven.junit;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.Test;

import com.tngtech.jgiven.junit.test.GivenTestStep;
import com.tngtech.jgiven.junit.test.ThenTestStep;
import com.tngtech.jgiven.junit.test.WhenTestStep;
import com.tngtech.jgiven.report.model.Word;

public class DataTableTest extends ScenarioTest<GivenTestStep, WhenTestStep, ThenTestStep> {

    @Test
    public void test_data_table_arguments() throws Throwable {
        given().the_following_data(
            new GivenTestStep.CoffeePrice( "Espresso", 1.5 ),
            new GivenTestStep.CoffeePrice( "Cappuccino", 2.5 ) )
            .and().some_boolean_value( true );
        when().something();
        then().something();

        getScenario().finished();

        Word lastWord = getScenario().getModel().getFirstStepModelOfLastScenario().getLastWord();
        List<List<String>> tableValue = lastWord.getArgumentInfo().getTableValue();
        assertThat( tableValue ).isNotNull();
        assertThat( tableValue.get( 0 ) ).containsExactly( "name", "price in EUR" );
        assertThat( tableValue.get( 1 ) ).containsExactly( "Espresso", "1.5" );
        assertThat( tableValue.get( 2 ) ).containsExactly( "Cappuccino", "2.5" );

    }
}
