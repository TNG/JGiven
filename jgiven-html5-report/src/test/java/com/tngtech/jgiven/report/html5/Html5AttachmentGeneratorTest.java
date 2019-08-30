package com.tngtech.jgiven.report.html5;

import static com.tngtech.jgiven.report.html5.Html5AttachmentGenerator.MINIMAL_THUMBNAIL_SIZE;
import static com.tngtech.jgiven.report.html5.Html5AttachmentGenerator.scaleDown;
import static org.assertj.core.api.Assertions.assertThat;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import javax.imageio.ImageIO;

import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import org.assertj.core.util.Lists;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.google.common.io.BaseEncoding;
import com.tngtech.jgiven.attachment.Attachment;
import com.tngtech.jgiven.attachment.MediaType;
import com.tngtech.jgiven.report.model.StepModel;
import com.tngtech.jgiven.report.model.Word;
import org.junit.runner.RunWith;

@RunWith(DataProviderRunner.class)
public class Html5AttachmentGeneratorTest {

    private static final String JSON_SAMPLE = "{}";
    private static final byte[] BINARY_SAMPLE = BaseEncoding.base64().decode(
        "R0lGODlhFgAWAOZiAAUFBd3d3eHh4be3tzg4ODU1NaysrMHBwTs7O39/f15eXs/Pz+fn5wwMDEVFRTIyMh0dHUpKSigoKJeXl3x8fKioqOTk5NPT05qamjc3N2RkZDExMYmJiUxMTDo6Ouvr62pqanFxcW1tbfz8/CAgIM3Nze7u7vn5+eLi4m5ubjMzM52dnRcXF5ubm/Pz801NTcvLyz09PSwsLISEhJmZmT8/P+Xl5SIiIggICJGRkVxcXCUlJfDw8K2trZaWlhAQED4+Pmtra2hoaMfHx0BAQFpaWsrKynl5eVZWVq+vr6Ojo/j4+IeHh3p6ehQUFNjY2Ly8vK6ursLCwmBgYJSUlHBwcICAgIODg/Hx8WJiYouLi9/f33JycsTExC4uLvb29v///wICAi4uLgAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAACH5BAEAAGIALAAAAAAWABYAAAf/gGKCgwVFPgFfXwEtUwWDj4MbIQJglQxblWACKV6QggQ9YCcTDmGmYQVaX2BKHpAEMGADJKe1YRJGYAsIkVFgKwC2tgBXYAcPglyywcLCVGBNYgUCLhCnDwi1LFkNpjgoAh4KYDmnMpUJpjcWYBOnGmBBNGAEpySrYAk77GAcp04jBgTgYSsCPiyVDDAzVQLRE2ERlmRSaCtJogvCJHzIZEWYAUUfbElgUGkEOltdAlQAY83UD5JgDHTAB+IUABMDhIARcYpAwmAGwVQ45QBMFQICbHQzpQHEwgIJWoaBIgCImCP9mjUbx0TQgwNgFGitheDEgQyDiCwAQ2FsGB1fKxZ0gFQD7AUkS4lKMfbCkxgVFCh9KYEBwxATmmag9SsIgQiBiQIMCBHDUyAAOw==" );

    @Rule
    public final TemporaryFolder temporaryFolderRule = new TemporaryFolder();

    @Test
    public void testFileNameGeneration() {
        Html5AttachmentGenerator generator = new Html5AttachmentGenerator();

        assertThat( generator.getTargetFile( "foo", "txt" ).getName() ).isEqualTo( "foo.txt" );
        assertThat( generator.getTargetFile( "foo", "txt" ).getName() ).isEqualTo( "foo2.txt" );
        assertThat( generator.getTargetFile( "foo", "png" ).getName() ).isEqualTo( "foo3.png" );
        assertThat( generator.getTargetFile( "foo4", "png" ).getName() ).isEqualTo( "foo4.png" );
        assertThat( generator.getTargetFile( "foo", "png" ).getName() ).isEqualTo( "foo5.png" );

    }

    @Test
    public void testScalingOfImageToMinimumSize() throws IOException, URISyntaxException {
        Attachment attachment = Attachment.fromBinaryBytes( BINARY_SAMPLE, MediaType.GIF );
        BufferedImage before = ImageIO.read( new ByteArrayInputStream( BaseEncoding.base64().decode( attachment.getContent() ) ) );
        assertThat( before.getWidth() ).isEqualTo( 22 );
        assertThat( before.getHeight() ).isEqualTo( 22 );

        Html5AttachmentGenerator generator = new Html5AttachmentGenerator( temporaryFolderRule.getRoot() );
        StepModel stepModel = new StepModel( "test", Lists.<Word>newArrayList() );
        stepModel.addAttachment( attachment );
        generator.visit( stepModel );

        File writtenFile = new File( temporaryFolderRule.getRoot().getPath() + "/attachment-thumb.gif" );
        Attachment writtenAttachment = Attachment.fromBinaryFile( writtenFile, MediaType.GIF );
        BufferedImage after = ImageIO.read( new ByteArrayInputStream( BaseEncoding.base64().decode( writtenAttachment.getContent() ) ) );
        assertThat( after.getWidth() ).isEqualTo(MINIMAL_THUMBNAIL_SIZE);
        assertThat( after.getHeight() ).isEqualTo(MINIMAL_THUMBNAIL_SIZE);
    }

    @Test
    @DataProvider( value = {
        "100, 10, 100, 10",
        "100, 100, 20, 20",
        "10, 100, 10, 100",
        "1000, 500, 40, 20",
        "10, 10, 10, 10"
    })
    public void testScaleDown(int initialWidth, int initialHeight, int expectedWidth, int expectedHeight) {
        BufferedImage image = new BufferedImage(initialWidth, initialHeight, BufferedImage.TYPE_INT_BGR);
        BufferedImage thumb = scaleDown(image);

        assertThat( thumb.getWidth() ).isEqualTo(expectedWidth);
        assertThat( thumb.getHeight() ).isEqualTo(expectedHeight);
    }

}