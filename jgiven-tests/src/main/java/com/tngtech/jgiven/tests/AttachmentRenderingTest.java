package com.tngtech.jgiven.tests;

import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.annotation.NestedSteps;
import com.tngtech.jgiven.annotation.ScenarioStage;
import com.tngtech.jgiven.attachment.Attachment;
import com.tngtech.jgiven.attachment.MediaType;
import com.tngtech.jgiven.testng.ScenarioTestListener;
import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testng.annotations.Listeners;

import static com.tngtech.jgiven.tests.AttachmentRenderingTest.*;
@Listeners( ScenarioTestListener.class )
@ExtendWith(JGivenReportExtractingExtension.class)
public class AttachmentRenderingTest extends ScenarioTestForTesting<AttachmentRenderingStages, AttachmentRenderingStages, AttachmentRenderingStages> {

    public AttachmentRenderingTest() {
        System.out.println("nasiges");
    }

    @Test
    @org.junit.jupiter.api.Test
    @org.testng.annotations.Test
    public void test_json_is_rendered_correctly() {
        given().a_step_with_attachment();
        when().a_step_with_nested_steps();
        then().a_final_step();
    }

    @SuppressWarnings("UnusedReturnValue")
    static class AttachmentRenderingStages extends Stage<AttachmentRenderingStages> {
        AttachmentRenderingStages a_step_with_attachment() {
            Attachment.fromText("a_step_with_attachment", MediaType.PLAIN_TEXT_UTF_8);
            return self();
        }

        @NestedSteps
        AttachmentRenderingStages a_step_with_nested_steps() {
            first_nested_step_with_attachment().
                    second_nested_step_with_attachment();
            return self();
        }

        AttachmentRenderingStages a_final_step() {
            return self();
        }

        AttachmentRenderingStages first_nested_step_with_attachment() {
            Attachment.fromText("first_nested_step_with_attachment", MediaType.PLAIN_TEXT_UTF_8);
            return self();
        }

        AttachmentRenderingStages second_nested_step_with_attachment() {
            Attachment.fromText("second_nested_step_with_attachment", MediaType.PLAIN_TEXT_UTF_8);
            return self();
        }
    }
}
