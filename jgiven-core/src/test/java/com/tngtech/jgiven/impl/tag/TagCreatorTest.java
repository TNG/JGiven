package com.tngtech.jgiven.impl.tag;


import static org.assertj.core.api.Assertions.assertThat;

import com.tngtech.jgiven.config.DefaultConfiguration;
import com.tngtech.jgiven.report.model.Tag;
import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.List;
import org.junit.Test;

public class TagCreatorTest {

    private final TagCreator underTest = new TagCreator(new DefaultConfiguration());

    @Test
    public void testAnnotationParsing() {
        Tag tag = getOnlyTagFor(AnnotationTestClass.class.getAnnotations()[0]);
        assertThat(tag.getName()).isEqualTo("AnnotationWithoutValue");
        assertThat(tag.getValues()).isEmpty();
    }

    @Test
    public void testAnnotationWithValueParsing() {
        Tag tag = getOnlyTagFor(AnnotationWithSingleValueTestClass.class.getAnnotations()[0]);
        assertThat(tag.getName()).isEqualTo("AnnotationWithSingleValue");
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
        assertThat(tag.getName()).isEqualTo("AnnotationWithIgnoredValue");
        assertThat(tag.getValues()).isEmpty();
        assertThat(tag.toIdString()).isEqualTo(AnnotationWithIgnoredValue.class.getName());
    }

    @Test
    public void testAnnotationWithoutExplodedArrayParsing() {
        Tag tag = getOnlyTagFor(AnnotationWithoutExplodedArrayValueTestClass.class.getAnnotations()[0]);
        assertThat(tag.getName()).isEqualTo("AnnotationWithoutExplodedArray");
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
            ParentTagWithValue.class.getPackage().getName() + ".ParentTagWithValue-SomeValue")
        );
    }

    @Test
    public void testAnnotationWithArrayParsing() {
        List<Tag> tags = underTest.toTags(AnnotationWithArrayValueTestClass.class.getAnnotations()[0]);
        assertThat(tags).hasSize(2);
        assertThat(tags.get(0).getName()).isEqualTo("AnnotationWithArray");
        assertThat(tags.get(0).getValues()).containsExactly("foo");
        assertThat(tags.get(1).getName()).isEqualTo("AnnotationWithArray");
        assertThat(tags.get(1).getValues()).containsExactly("bar");
    }

    private Tag getOnlyTagFor(Annotation annotation) {
        List<Tag> tags = underTest.toTags(annotation);
        assertThat(tags).hasSize(1);
        return tags.get(0);
    }
}
