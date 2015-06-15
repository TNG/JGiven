package com.tngtech.jgiven.examples.attachments;

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
    @DataProvider( {
        "English, Hello World",
        "German, Hallo Welt",
        "Chinese, 你好世界" } )
    public void attachments_work_with_data_tables( String title, String content ) {
        given().some_text_content( content );
        then().it_can_be_added_as_an_attachment_to_the_step_with_title( title );
    }
}
