package com.tngtech.jgiven.report.html5;

import static org.assertj.core.api.Assertions.assertThat;

import com.tngtech.jgiven.annotation.BeforeStage;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.testng.reporters.Files;

public class ThenHtml5App<SELF extends ThenHtml5App<?>> extends Html5AppStage<SELF> {

    private WebElement foundTag;
    private WebElement foundLink;
    private WebElement foundElement;

    @FindBy(id = "page-title")
    WebElement pageTitle;

    @FindBy(id = "statistics")
    WebElement statistics;

    @FindBy(className = "fa-paperclip")
    List<WebElement> attachmentIcons;

    @BeforeStage
    public void setup() {
        PageFactory.initElements(webDriver, this);
    }

    public SELF the_page_title_is(String title) {
        assertThat(pageTitle.getText()).isEqualTo(title);
        return self();
    }

    public SELF the_page_statistics_line_contains_text(String text) {
        assertThat(statistics.getText()).contains(text);
        return self();
    }

    public SELF $_attachment_icons_exist(int nrIcons) {
        assertThat(attachmentIcons).hasSize(nrIcons);
        return self();
    }

    public SELF an_attachment_icon_exists() {
        assertThat(attachmentIcons).isNotEmpty();
        return self();
    }

    public SELF the_content_of_the_attachment_referenced_by_the_icon_is(String content)
        throws IOException, URISyntaxException {
        return the_content_of_the_attachment_referenced_by_icon_$_is(1, content);
    }

    public SELF the_content_of_the_attachment_referenced_by_icon_$_is(int iconNr, String content)
        throws IOException, URISyntaxException {
        String href = attachmentIcons.get(iconNr - 1).findElement(By.xpath("../..")).getAttribute("href");
        String foundContent = Files.readFile(new File(new URL(href).toURI())).trim();
        assertThat(content).isEqualTo(foundContent);
        return self();
    }

    public SELF the_page_contains_tag(String tagName) {
        foundTag = findTagWithName(tagName);
        assertThat(foundTag).isNotNull();
        return self();
    }

    public SELF the_tag_has_style(String style) {
        WebElement span = foundTag.findElement(By.xpath("span"));
        assertThat(span.getAttribute("style")).contains(style);
        return self();
    }

    public SELF the_report_title_is(String title) {
        assertThat(webDriver.findElement(By.id("title")).getText()).isEqualTo(title);
        return self();
    }

    public SELF the_navigation_menu_has_a_link_with_text(String text) {
        foundLink = webDriver.findElement(By.linkText(text));
        assertThat(foundLink.getText()).isEqualTo(text);
        return self();
    }

    public SELF href(String href) {
        assertThat(foundLink).isNotNull();
        assertThat(foundLink.getAttribute("href")).isEqualTo(href);
        return self();
    }

    public SELF target(String target) {
        assertThat(foundLink).isNotNull();
        assertThat(foundLink.getAttribute("target")).isEqualTo(target);
        return self();
    }

    public SELF an_element_with_a_$_class_exists(String multiline) {
        foundElement = webDriver.findElement(By.className(multiline));
        assertThat(foundElement).isNotNull();
        return self();
    }

    /**
     * @param index the ordinal number of this element, starting at 1.
     */
    public SELF the_$_th_element_with_a_$_class_exists(int index, String multiline) {
        List<WebElement> elements = webDriver.findElements(By.className(multiline));
        assertThat(elements).hasSizeGreaterThanOrEqualTo(index);
        foundElement = elements.get(index - 1);
        return self();
    }

    public SELF has_content(String content) {
        assertThat(foundElement.getText()).isEqualTo(content);
        return self();
    }

    public SELF attribute_$_has_value_$(String attribute, String content) {
        assertThat(foundElement.getAttribute(attribute)).isEqualTo(content);
        return self();
    }

    public SELF the_image_is_loaded() {
        assertThat(foundElement.getAttribute("naturalHeight")).isNotEqualTo("0");
        return self();
    }
}
