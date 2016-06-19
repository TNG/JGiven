package com.tngtech.jgiven.report.html5;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.reporters.Files;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class ThenHtml5App<SELF extends ThenHtml5App<?>> extends Html5AppStage<SELF> {

    private WebElement foundTag;
    private WebElement foundLink;

    public SELF the_page_title_is( String pageTitle ) {
        assertThat( webDriver.findElement( By.id( "page-title" ) ).getText() ).isEqualTo( pageTitle );
        return self();
    }

    public SELF the_page_statistics_line_contains_text( String text ) throws IOException {
        assertThat( webDriver.findElement( By.id( "statistics" ) ).getText() ).contains( text );
        return self();
    }

    public SELF $_attachment_icons_exist( int nrIcons ) {
        assertThat( findAttachmentIcon() ).hasSize( nrIcons );
        return self();
    }

    public SELF an_attachment_icon_exists() {
        assertThat( findAttachmentIcon() ).isNotEmpty();
        return self();
    }

    private List<WebElement> findAttachmentIcon() {
        return webDriver.findElements( By.className( "fa-paperclip" ) );
    }

    public SELF the_content_of_the_attachment_referenced_by_the_icon_is( String content ) throws IOException, URISyntaxException {
        return the_content_of_the_attachment_referenced_by_icon_$_is( 1, content );
    }

    public SELF the_content_of_the_attachment_referenced_by_icon_$_is( int iconNr, String content ) throws IOException, URISyntaxException {
        String href = findAttachmentIcon().get( iconNr - 1 ).findElement( By.xpath( ".." ) ).getAttribute( "href" );
        String foundContent = Files.readFile( new File( new URL( href ).toURI() ) ).trim();
        assertThat( content ).isEqualTo( foundContent );
        return self();
    }

    public SELF the_page_contains_tag( String tagName ) {
        foundTag = findTagWithName( tagName );
        assertThat( foundTag ).isNotNull();
        return self();
    }

    public SELF the_tag_has_style( String style ) {
        WebElement span = foundTag.findElement( By.xpath( "span" ) );
        assertThat( span.getAttribute( "style" ) ).contains( style );
        return self();
    }

    public SELF the_report_title_is( String title ) {
        assertThat( webDriver.findElement( By.id( "title" ) ).getText() ).isEqualTo( title );
        return self();
    }

    public SELF the_navigation_menu_has_a_link_with_text( String text ) {
        foundLink = webDriver.findElement( By.linkText( text ) );
        assertThat( foundLink.getText() ).isEqualTo( text );
        return self();
    }

    public SELF href( String href ) {
        assertThat( foundLink ).isNotNull();
        assertThat( foundLink.getAttribute( "href" ) ).isEqualTo( href );
        return self();
    }

    public SELF target( String target ) {
        assertThat( foundLink ).isNotNull();
        assertThat( foundLink.getAttribute( "target" ) ).isEqualTo( target );
        return self();
    }
}
