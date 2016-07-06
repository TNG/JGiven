package com.tngtech.jgiven.junit.test;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;

import com.google.common.collect.Lists;
import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.annotation.Format;
import com.tngtech.jgiven.annotation.IsTag;
import com.tngtech.jgiven.annotation.ProvidedScenarioState;
import com.tngtech.jgiven.annotation.Quoted;
import com.tngtech.jgiven.annotation.Table;
import com.tngtech.jgiven.config.FormatterConfiguration;
import com.tngtech.jgiven.format.BooleanFormatter;
import com.tngtech.jgiven.format.ObjectFormatter;
import com.tngtech.jgiven.format.table.TableFormatter;
import com.tngtech.jgiven.format.table.TableFormatterFactory;
import com.tngtech.jgiven.report.model.DataTable;

public class GivenTestStep extends Stage<GivenTestStep> {

    @ProvidedScenarioState
    int value1;

    @ProvidedScenarioState
    int value2;

    public GivenTestStep some_integer_value( int someIntValue ) {
        this.value1 = someIntValue;
        return self();
    }

    public void some_boolean_value( boolean someBooleanValue ) {

    }

    @IsTag
    @Retention( RetentionPolicy.RUNTIME )
    @interface StepMethodTag {}

    @StepMethodTag
    public void a_tagged_step_method() {}

    public void $d_and_$d( int value1, int value2 ) {
        this.value1 = value1;
        this.value2 = value2;
    }

    public GivenTestStep another_integer_value( int secondArg ) {
        value2 = secondArg;
        return self();
    }

    public void a_third_integer_value( int thirdArg ) {}

    public void something() {

    }

    public void some_step_fails( boolean fail ) {
        assertThat( fail ).isFalse();
    }

    public void a_step_with_a_table_parameter_and_primitive_array( @Table int... args ) {}

    public GivenTestStep some_quoted_string_value( @Quoted String someQuotedStringValue ) {
        return self();
    }

    public GivenTestStep some_string_value( String someStringValue ) {
        return self();
    }

    public void another_quoted_string_value( @Quoted String anotherQuotedStringValue ) {

    }

    public static class TableClass {
        public String value;
    }

    public void some_data_table( @Table TableClass... param ) {

    }

    public static class TestTableEntry {
        int some_field = 1;
    }

    public void a_step_with_a_table_parameter( @Table TestTableEntry... args ) {}

    public static class CoffeePrice {
        String name;
        double price_in_EUR;

        public CoffeePrice( String name, double priceInEur ) {
            this.name = name;
            this.price_in_EUR = priceInEur;
        }
    }

    public GivenTestStep the_following_data( @Table CoffeePrice... data ) {
        return this;
    }

    public GivenTestStep a_list_of_booleans( @Table @Format( value = BooleanFormatter.class, args = { "on", "off" } ) List<Boolean> booleans ) {
        return this;
    }

    public GivenTestStep a_list_of_booleans_without_header( @Table( header = Table.HeaderType.NONE ) @Format(
        value = BooleanFormatter.class, args = { "on", "off" } ) List<Boolean> booleans ) {
        return this;
    }

    public static class TestTableFormatter implements TableFormatter {

        @Override
        public DataTable format( Object tableArgument, Table tableAnnotation, String parameterName, Annotation... allAnnotations ) {
            List<List<String>> data = Lists.newArrayList();
            CoffeePrice[] castedTableArgument = (CoffeePrice[]) tableArgument;
            data.add( Lists.newArrayList( parameterName ) );
            for( CoffeePrice price : castedTableArgument ) {
                data.add( Lists.newArrayList( price.name + ": " + price.price_in_EUR ) );
            }
            return new DataTable( Table.HeaderType.HORIZONTAL, data );
        }

        public static class Factory implements TableFormatterFactory {

            @Override
            public TableFormatter create( FormatterConfiguration formatterConfiguration, ObjectFormatter<?> objectFormatter ) {
                return new TestTableFormatter();
            }
        }
    }

    public GivenTestStep a_list_of_PoJos_with_custom_table_formatter(
            @Table( formatter = TestTableFormatter.Factory.class ) CoffeePrice... coffeePrices ) {
        return this;
    }
}
