package com.tngtech.jgiven.examples.attachments;

import java.io.IOException;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.jgiven.junit.SimpleScenarioTest;

@RunWith( DataProviderRunner.class )
public class AttachmentsExample extends SimpleScenarioTest<AttachmentsExampleStage> {

    @Test
    public void attachments_can_be_added_to_steps() {
        given().some_text_content( "Hello World" );
        then().it_can_be_added_as_an_attachment_to_the_step_with_title( "Hi" );
    }

    @Test
    public void steps_can_have_multiple_attachments() {
        given().some_text_content( "Hi There" );
        then().it_can_be_added_as_an_attachment_multiple_times_to_the_step();
    }

    @Test
    @DataProvider( {
        "English, Hello World",
        "German, Hallo Welt",
        "Chinese, 你好世界" } )
    public void attachments_work_with_data_tables( String title, String content ) {
        given().some_text_content( content );
        then().it_can_be_added_as_an_attachment_to_the_step_with_title( title );
    }

    @Test
    public void attachments_can_be_directly_shown() throws IOException {
        given().an_oval_circle();

    }

    @Test
    public void large_attachments_can_be_zoomed() throws IOException {
        given().a_large_oval_circle();
    }

    @Test
    @DataProvider( { "blue", "red" } )
    public void inline_attachments_can_be_used_when_having_multiple_cases( String color ) throws IOException {
        given().a_$_oval_circle( color );
    }

}
