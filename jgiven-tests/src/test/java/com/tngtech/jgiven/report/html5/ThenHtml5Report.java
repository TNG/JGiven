package com.tngtech.jgiven.report.html5;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.reporters.Files;

public class ThenHtml5Report<SELF extends ThenHtml5Report<?>> extends Html5ReportStage<SELF> {

    public SELF the_page_title_is( String pageTitle ) {
        assertThat( webDriver.findElement( By.id( "page-title" ) ).getText() ).isEqualTo( pageTitle );
        return self();
    }

    public SELF the_page_statistics_line_contains_text( String text ) throws IOException {
        assertThat( webDriver.findElement( By.id( "statistics" ) ).getText() ).contains( text );
        return self();
    }

    public SELF an_attachment_icon_exists() {
        assertThat( findAttachmentIcon() ).isNotEmpty();
        return self();
    }

    private List<WebElement> findAttachmentIcon() {
        return webDriver.findElements( By.className( "fa-paperclip" ) );
    }

    public SELF the_content_of_the_referenced_attachment_is( String content ) throws IOException, URISyntaxException {
        String href = findAttachmentIcon().get( 0 ).findElement( By.xpath( ".." ) ).getAttribute( "href" );
        String foundContent = Files.readFile( new File( new URL( href ).toURI() ) ).trim();
        assertThat( content ).isEqualTo( foundContent );
        return self();
    }
}
