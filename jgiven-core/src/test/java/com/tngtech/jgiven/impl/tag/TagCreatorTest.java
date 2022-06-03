package com.tngtech.jgiven.impl.tag;


import static org.assertj.core.api.Assertions.assertThat;

import com.tngtech.jgiven.config.DefaultConfiguration;
import com.tngtech.jgiven.impl.TestUtil.JGivenLogHandler;
import com.tngtech.jgiven.report.model.Tag;
import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import java.util.stream.Stream;
import org.junit.Before;
import org.junit.Test;

public class TagCreatorTest {

    private final TagCreator underTest = new TagCreator(new DefaultConfiguration());
    private final JGivenLogHandler interceptor = new JGivenLogHandler();

    @Before
    public void addLogInterceptor() {
        Logger testLogger = LogManager.getLogManager().getLogger(TagCreator.class.getName());
        testLogger.addHandler(interceptor);
    }

    @Test
    public void testAnnotationParsing() {
        Tag tag = getOnlyTagFor(AnnotationTestClass.class.getAnnotations()[0]);
        assertThat(tag.getName()).isEqualTo(AnnotationWithoutValue.class.getSimpleName());
        assertThat(tag.getValues()).isEmpty();
        assertThat(interceptor.containsLoggingEvent(record -> record.getLevel() == Level.SEVERE))
            .as("Attempt to convert an annotation without value method results in an error log")
            .isFalse();
    }

    @Test
    public void testAnnotationWithValueParsing() {
        Tag tag = getOnlyTagFor(AnnotationWithSingleValueTestClass.class.getAnnotations()[0]);
        assertThat(tag.getName()).isEqualTo(AnnotationWithSingleValue.class.getSimpleName());
        assertThat(tag.getValues()).containsExactly("testvalue");
    }

    @Test
    public void testAnnotationWithName() {
        Tag tag = getOnlyTagFor(AnnotationWithNameTestClass.class.getAnnotations()[0]);
        assertThat(tag.getName()).isEqualTo("AnotherName");
        assertThat(tag.getValues()).isEmpty();
        assertThat(tag.toIdString()).isEqualTo(AnnotationWithName.class.getName());
    }

    @Test
    public void testAnnotationWithIgnoredValueParsing() {
        Tag tag = getOnlyTagFor(AnnotationWithIgnoredValueTestClass.class.getAnnotations()[0]);
        assertThat(tag.getName()).isEqualTo(AnnotationWithIgnoredValue.class.getSimpleName());
        assertThat(tag.getValues()).isEmpty();
        assertThat(tag.toIdString()).isEqualTo(AnnotationWithIgnoredValue.class.getName());
    }

    @Test
    public void testAnnotationWithoutExplodedArrayParsing() {
        Tag tag = getOnlyTagFor(AnnotationWithoutExplodedArrayValueTestClass.class.getAnnotations()[0]);
        assertThat(tag.getName()).isEqualTo(AnnotationWithoutExplodedArray.class.getSimpleName());
        assertThat(tag.getValues()).containsExactly("foo", "bar");
    }

    @Test
    public void testAnnotationWithDescription() {
        Tag tag = getOnlyTagFor(AnnotationWithDescription.class.getAnnotations()[0]);
        assertThat(tag.getDescription()).isEqualTo("Some Description");
    }

    @Test
    public void testAnnotationWithDescriptionAndIgnoreValue() {
        Tag tag = getOnlyTagFor(AnnotationWithDescriptionAndIgnoreValue.class.getAnnotations()[0]);
        assertThat(tag.getValues()).isEmpty();
        assertThat(tag.getDescription()).isEqualTo("Some Description");
        assertThat(tag.getTags()).hasSize(1);
    }

    @Test
    public void testAnnotationWithParentTag() {
        Tag tag = getOnlyTagFor(AnnotationWithParentTag.class.getAnnotations()[0]);
        assertThat(tag.getTags()).containsAll(Arrays.asList(
            ParentTag.class.getName(),
            ParentTagWithValue.class.getName() + "-SomeValue")
        );
    }

    @Test
    public void testAnnotationWithArrayParsing() {
        List<Tag> tags =
            underTest.toTags(AnnotationWithArrayValueTestClass.class.getAnnotations()[0]).getDeclaredTags();
        assertThat(tags).hasSize(2);
        assertThat(tags.get(0).getName()).isEqualTo("AnnotationWithArray");
        assertThat(tags.get(0).getValues()).containsExactly("foo");
        assertThat(tags.get(1).getName()).isEqualTo("AnnotationWithArray");
        assertThat(tags.get(1).getValues()).containsExactly("bar");
    }

    @Test
    public void testAllParentsOfTagAreResolved() {
        String[] expectedNames = Stream.of(TagWithParentTags.class, ParentTag.class, ParentTagWithValue.class)
            .map(Class::getSimpleName).toArray(String[]::new);

        ResolvedTags resolvedTags = underTest.toTags(TagWithGrandparentTags.class);

        assertThat(resolvedTags.getAncestors()).extracting(Tag::getName).containsExactlyInAnyOrder(expectedNames);
        assertThat(resolvedTags.getDeclaredTags()).extracting(Tag::getName)
            .containsExactly(TagWithGrandparentTags.class.getSimpleName());
    }

    @Test
    public void testTagConfigurationOnlyRefersToTheTagsSingleParent() {
        ResolvedTags resolvedTags = underTest.toTags(TagWithGrandparentTags.class);
        assertThat(resolveParentNames(resolvedTags)).containsExactly(TagWithParentTags.class.getName());
    }

    @Test
    public void testTagConfigurationRefersToBothParentTags() {
        ResolvedTags resolvedTags = underTest.toTags(TagWithParentTags.class);
        assertThat(resolveParentNames(resolvedTags)).containsExactlyInAnyOrder(
            ParentTag.class.getName(),
            ParentTagWithValue.class.getName() + "-SomeValue"
        );
    }

    private Stream<String> resolveParentNames(ResolvedTags resolvedTags) {
        return resolvedTags.getDeclaredTags().stream()
            .map(Tag::getTags)
            .flatMap(List::stream);
    }

    private Tag getOnlyTagFor(Annotation annotation) {
        List<Tag> tags = underTest.toTags(annotation).getDeclaredTags();
        assertThat(tags).hasSize(1);
        return tags.get(0);
    }
}
