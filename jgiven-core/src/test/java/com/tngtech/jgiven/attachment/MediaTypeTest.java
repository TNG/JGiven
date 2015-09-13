package com.tngtech.jgiven.attachment;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.charset.Charset;

import org.junit.Test;

public class MediaTypeTest {

    @Test
    public void testSimpleMethods() {
        assertThat( MediaType.Type.fromString( "image" ) ).isSameAs( MediaType.Type.IMAGE );
        assertThat( MediaType.JPEG.isImage() ).isTrue();
        assertThat( MediaType.PLAIN_TEXT_UTF_8.isImage() ).isFalse();
        assertThat( MediaType.PLAIN_TEXT_UTF_8.getCharset() ).isEqualTo( Charset.forName( "utf8" ) );
        assertThat( MediaType.application( "word" ).isBinary() ).isTrue();
        assertThat( MediaType.audio( "mp3" ).isBinary() ).isTrue();
        assertThat( MediaType.PNG.getType() ).isEqualTo( MediaType.Type.IMAGE );
        assertThat( MediaType.PNG.getSubType() ).isEqualTo( "png" );
        assertThat( MediaType.PNG.asString() ).isEqualTo( "image/png" );
    }
}
