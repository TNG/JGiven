package com.tngtech.jgiven.report.html5;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

public class Html5AttachmentGeneratorTest {

    @Test
    public void testFileNameGeneration() {
        Html5AttachmentGenerator generator = new Html5AttachmentGenerator();

        assertThat( generator.getTargetFile( "foo", "txt" ).getName() ).isEqualTo( "foo.txt" );
        assertThat( generator.getTargetFile( "foo", "txt" ).getName() ).isEqualTo( "foo2.txt" );
        assertThat( generator.getTargetFile( "foo", "png" ).getName() ).isEqualTo( "foo3.png" );
        assertThat( generator.getTargetFile( "foo4", "png" ).getName() ).isEqualTo( "foo4.png" );
        assertThat( generator.getTargetFile( "foo", "png" ).getName() ).isEqualTo( "foo5.png" );

    }
}