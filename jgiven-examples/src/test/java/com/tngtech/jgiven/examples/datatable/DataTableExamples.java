package com.tngtech.jgiven.examples.datatable;

import static com.tngtech.jgiven.annotation.Table.HeaderType.VERTICAL;
import static java.util.Arrays.asList;

import java.util.List;

import org.junit.Test;

import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.annotation.Table;
import com.tngtech.jgiven.junit.SimpleScenarioTest;

public class DataTableExamples extends SimpleScenarioTest<DataTableExamples.DataTableStage> {

    static class DataTableStage extends Stage<DataTableStage> {

        public DataTableStage a_list_of_lists_is_used_as_parameter(
                @Table List<List<String>> table ) {
            return self();
        }

        public DataTableStage a_list_of_lists_is_used_as_parameter_with_column_titles(
                @Table( columnTitles = { "Name", "Email" } ) List<List<String>> table ) {
            return self();
        }

        public DataTableStage a_list_of_POJOs_is_used_as_parameters(
                @Table TestCustomer... testCustomer ) {
            return self();
        }

        public DataTableStage a_list_of_POJOs_is_used_as_parameters_with_header_type_VERTICAL(
                @Table( header = VERTICAL ) TestCustomer... testCustomer ) {
            return self();
        }

        public DataTableStage a_list_of_POJOs_is_used_as_parameters_with_header_type_VERTICAL_and_numbered_columns(
                @Table( header = VERTICAL, numberedColumns = true ) TestCustomer... testCustomer ) {
            return self();
        }

        public void some_action_happens() {

        }

        public void a_single_POJO_is_used_as_parameters(
                @Table( header = VERTICAL ) TestCustomer testCustomer ) {}

        public void a_list_of_POJOs_with_numbered_rows(
                @Table( numberedRows = true ) TestCustomer... testCustomer ) {}

        public void a_list_of_POJOs_with_numbered_rows_and_custom_header(
                @Table( numberedRowsHeader = "Counter" ) TestCustomer... testCustomer ) {}

        public void a_two_dimensional_array_with_numbered_rows(
                @Table( numberedRows = true, columnTitles = "t" ) Object[][] testCustomer ) {}

    }

    static class TestCustomer {
        String name;
        String email;

        public TestCustomer( String name, String email ) {
            this.name = name;
            this.email = email;
        }
    }

    @Test
    public void a_list_of_list_can_be_used_as_table_parameter() {
        given().a_list_of_lists_is_used_as_parameter(
            asList(
                asList( "Name", "Email" ),
                asList( "John Doe", "john@doe.com" ),
                asList( "Jane Roe", "jane@roe.com" ) )
            );
    }

    @Test
    public void a_list_of_list_can_be_used_as_table_parameter_and_column_titles_can_be_set() {
        given().a_list_of_lists_is_used_as_parameter_with_column_titles(
            asList(
                asList( "John Doe", "john@doe.com" ),
                asList( "Jane Roe", "jane@roe.com" ) )
            );
    }

    @Test
    public void a_list_of_POJOs_can_be_represented_as_data_tables() {
        given().a_list_of_POJOs_is_used_as_parameters(
            new TestCustomer( "John Doe", "john@doe.com" ),
            new TestCustomer( "Jane Roe", "jane@roe.com" )
            );
    }

    @Test
    public void a_list_of_POJOs_can_be_represented_as_a_data_table_with_a_vertical_header() {
        given().a_list_of_POJOs_is_used_as_parameters_with_header_type_VERTICAL(
            new TestCustomer( "John Doe", "john@doe.com" ),
            new TestCustomer( "Jane Roe", "jane@roe.com" )
            );
    }

    @Test
    public void a_list_of_POJOs_can_be_represented_as_a_data_table_with_a_vertical_header_and_numbered_columns() {
        given().a_list_of_POJOs_is_used_as_parameters_with_header_type_VERTICAL_and_numbered_columns(
            new TestCustomer( "John Doe", "john@doe.com" ),
            new TestCustomer( "Jane Roe", "jane@roe.com" )
            );
    }

    @Test
    public void a_single_POJO_can_be_represented_as_a_data_table() {
        given().a_single_POJO_is_used_as_parameters(
            new TestCustomer( "Jane Roe", "jane@roe.com" )
            );
    }

    @Test
    public void parameter_tables_can_have_numbered_rows() {
        given().a_list_of_POJOs_with_numbered_rows(
            new TestCustomer( "John Doe", "john@doe.com" ),
            new TestCustomer( "Jane Roe", "jane@roe.com" ),
            new TestCustomer( "Lee Smith", "lee@smith.com" )
            );
    }

    @Test
    public void parameter_tables_can_have_numbered_rows_with_custom_headers() {
        given().a_list_of_POJOs_with_numbered_rows_and_custom_header(
            new TestCustomer( "John Doe", "john@doe.com" ),
            new TestCustomer( "Jane Roe", "jane@roe.com" ),
            new TestCustomer( "Lee Smith", "lee@smith.com" )
            );
    }

    @Test
    public void two_dimensional_arrays_can_be_numbered() {
        given().a_two_dimensional_array_with_numbered_rows( new Object[][] {
            { "a" },
            { "b" } } );
    }
}
