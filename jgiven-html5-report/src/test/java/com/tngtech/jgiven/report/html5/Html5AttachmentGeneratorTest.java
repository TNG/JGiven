package com.tngtech.jgiven.report.html5;

import static com.tngtech.jgiven.report.html5.Html5AttachmentGenerator.MINIMAL_THUMBNAIL_SIZE;
import static com.tngtech.jgiven.report.html5.Html5AttachmentGenerator.scaleDown;
import static org.assertj.core.api.Assertions.assertThat;

import com.google.common.io.BaseEncoding;
import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.jgiven.attachment.Attachment;
import com.tngtech.jgiven.attachment.MediaType;
import com.tngtech.jgiven.report.model.ReportModel;
import com.tngtech.jgiven.report.model.ScenarioCaseModel;
import com.tngtech.jgiven.report.model.ScenarioModel;
import com.tngtech.jgiven.report.model.StepModel;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URISyntaxException;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import org.assertj.core.util.Lists;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;

@RunWith(DataProviderRunner.class)
public class Html5AttachmentGeneratorTest {

    private Html5AttachmentGenerator generator;
    private static final byte[] BINARY_SAMPLE = BaseEncoding.base64().decode(
            "R0lGODlhFgAWAOZiAAUFBd3d3eHh4be3tzg4ODU1NaysrMHBwTs7O39/f15eXs/Pz+fn5wwMDEVFRTIyMh0dHUpKSigoKJeXl3x8fKioqOTk5NPT05qamjc3N2RkZDExMYmJiUxMTDo6Ouvr62pqanFxcW1tbfz8/CAgIM3Nze7u7vn5+eLi4m5ubjMzM52dnRcXF5ubm/Pz801NTcvLyz09PSwsLISEhJmZmT8/P+Xl5SIiIggICJGRkVxcXCUlJfDw8K2trZaWlhAQED4+Pmtra2hoaMfHx0BAQFpaWsrKynl5eVZWVq+vr6Ojo/j4+IeHh3p6ehQUFNjY2Ly8vK6ursLCwmBgYJSUlHBwcICAgIODg/Hx8WJiYouLi9/f33JycsTExC4uLvb29v///wICAi4uLgAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAACH5BAEAAGIALAAAAAAWABYAAAf/gGKCgwVFPgFfXwEtUwWDj4MbIQJglQxblWACKV6QggQ9YCcTDmGmYQVaX2BKHpAEMGADJKe1YRJGYAsIkVFgKwC2tgBXYAcPglyywcLCVGBNYgUCLhCnDwi1LFkNpjgoAh4KYDmnMpUJpjcWYBOnGmBBNGAEpySrYAk77GAcp04jBgTgYSsCPiyVDDAzVQLRE2ERlmRSaCtJogvCJHzIZEWYAUUfbElgUGkEOltdAlQAY83UD5JgDHTAB+IUABMDhIARcYpAwmAGwVQ45QBMFQICbHQzpQHEwgIJWoaBIgCImCP9mjUbx0TQgwNgFGitheDEgQyDiCwAQ2FsGB1fKxZ0gFQD7AUkS4lKMfbCkxgVFCh9KYEBwxATmmag9SsIgQiBiQIMCBHDUyAAOw==");

    @Rule
    public final TemporaryFolder temporaryFolderRule = new TemporaryFolder();

    @Before
    public void setup() {
        generator = new Html5AttachmentGenerator(temporaryFolderRule.getRoot());
    }

    @Test
    public void testFileNameGeneration() {
        assertThat(generator.getTargetFile("foo", "txt").getName()).isEqualTo("foo.txt");
        assertThat(generator.getTargetFile("foo", "txt").getName()).isEqualTo("foo2.txt");
        assertThat(generator.getTargetFile("foo", "png").getName()).isEqualTo("foo3.png");
        assertThat(generator.getTargetFile("foo4", "png").getName()).isEqualTo("foo4.png");
        assertThat(generator.getTargetFile("foo", "png").getName()).isEqualTo("foo5.png");
    }

    @Test
    public void testScalingOfImageToMinimumSize() throws IOException, URISyntaxException {
        Attachment attachment = Attachment.fromBinaryBytes(BINARY_SAMPLE, MediaType.GIF);
        BufferedImage before = ImageIO.read(new ByteArrayInputStream(BaseEncoding.base64()
                                            .decode(attachment.getContent())));
        assertThat(before.getWidth()).isEqualTo(22);
        assertThat(before.getHeight()).isEqualTo(22);

        StepModel stepModel = new StepModel("test", Lists.newArrayList());
        stepModel.addAttachment(attachment);
        generator.visit(stepModel);

        File writtenFile = new File(temporaryFolderRule.getRoot().getPath() + "/attachment-thumb.gif");
        Attachment writtenAttachment = Attachment.fromBinaryFile(writtenFile, MediaType.GIF);
        BufferedImage after = ImageIO.read(new ByteArrayInputStream(BaseEncoding.base64()
                                            .decode(writtenAttachment.getContent())));
        assertThat(after.getWidth()).isEqualTo(MINIMAL_THUMBNAIL_SIZE);
        assertThat(after.getHeight()).isEqualTo(MINIMAL_THUMBNAIL_SIZE);
    }

    @Test
    public void testFindingAndGeneratingAttachmentsInAllSteps() throws IOException {
        File root = temporaryFolderRule.getRoot();
        generator.generateAttachments(root, generateReportModelWithAttachments());

        File parentStepFile = new File(temporaryFolderRule.getRoot().getPath()
                        + "/attachments/testing/parentAttachment.gif");
        File nestedStepFile = new File(temporaryFolderRule.getRoot().getPath()
                        + "/attachments/testing/nestedAttachment.gif");

        Attachment writtenParentAttachment = Attachment.fromBinaryFile(parentStepFile, MediaType.GIF);
        Attachment writtenNestedAttachment = Attachment.fromBinaryFile(nestedStepFile, MediaType.GIF);
        assertThat(writtenParentAttachment.getContent()).isNotNull();
        assertThat(writtenNestedAttachment.getContent()).isNotNull();

    }

    @Test
    public void testGetImageDimensions() {
        assertThat(generator.getImageDimension(BINARY_SAMPLE)).isEqualTo(new Dimension(22, 22));
    }

    @Test
    public void testPNGConvertor() {
        File sampleSVG = new File("src/test/resources/SampleSVG.svg");
        String pngContent = generator.getPNGFromSVG(sampleSVG);

        assertThat(generator.getImageDimension(BaseEncoding.base64().decode(pngContent)))
                .isEqualTo(new Dimension(25, 25));
    }

    @Test
    public void testSVGFilesHaveAGeneratedThumbnail() throws IOException {
        File sampleSVG = new File("src/test/resources/SampleSVG.svg");
        Attachment sampleSVGAttachment = Attachment.fromTextFile(sampleSVG, MediaType.SVG_UTF_8)
                .withFileName("SampleSVG");
        StepModel stepModel = new StepModel("svgTest", Lists.newArrayList());
        stepModel.addAttachment(sampleSVGAttachment);

        generator.visit(stepModel);

        File svgThumbnail = new File(temporaryFolderRule.getRoot().getPath()
                + "/SampleSVG-thumb.svg");

        String pngContent = generator.getPNGFromSVG(svgThumbnail);

        assertThat(generator.getImageDimension(BaseEncoding.base64().decode(pngContent)))
                .isEqualTo(new Dimension(MINIMAL_THUMBNAIL_SIZE, MINIMAL_THUMBNAIL_SIZE));
    }

    private ReportModel generateReportModelWithAttachments() {
        Attachment nestedAttachment = Attachment.fromBinaryBytes(BINARY_SAMPLE, MediaType.GIF)
                                .withFileName("nestedAttachment");
        Attachment parentAttachment = Attachment.fromBinaryBytes(BINARY_SAMPLE, MediaType.GIF)
                                .withFileName("parentAttachment");
        StepModel parentStep = new StepModel("test", Lists.newArrayList());
        StepModel nestedStep = new StepModel("test", Lists.newArrayList());
        nestedStep.addAttachment(nestedAttachment);
        parentStep.addNestedStep(nestedStep);
        parentStep.addAttachment(parentAttachment);
        ReportModel model = new ReportModel();
        ArrayList<ScenarioModel> scenarios = new ArrayList<ScenarioModel>();
        ScenarioModel scenarioModel = new ScenarioModel();
        ScenarioCaseModel scenarioCase = new ScenarioCaseModel();
        scenarioCase.addStep(parentStep);
        scenarioModel.addCase(scenarioCase);
        scenarios.add(scenarioModel);
        model.setScenarios(scenarios);
        model.setClassName("testing");
        return model;
    }

    @Test
    @DataProvider(value = {
        "100, 10, 100, 10",
        "100, 100, 20, 20",
        "10, 100, 10, 100",
        "1000, 500, 40, 20",
        "10, 10, 10, 10"
    })
    public void testScaleDown(int initialWidth, int initialHeight, int expectedWidth, int expectedHeight) {
        BufferedImage image = new BufferedImage(initialWidth, initialHeight, BufferedImage.TYPE_INT_BGR);
        BufferedImage thumb = scaleDown(image);

        assertThat(thumb.getWidth()).isEqualTo(expectedWidth);
        assertThat(thumb.getHeight()).isEqualTo(expectedHeight);
    }
}
