package com.tngtech.jgiven.impl.tag;

import com.tngtech.jgiven.annotation.IsTag;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@SuppressWarnings({"checkstyle:OneTopLevelClass", "checkstyle:OuterTypeFilename"})
@IsTag
@Retention(RetentionPolicy.RUNTIME)
@interface AnnotationWithoutValue {
}

@SuppressWarnings("checkstyle:OneTopLevelClass")
@AnnotationWithoutValue
class AnnotationTestClass {
}

@SuppressWarnings("checkstyle:OneTopLevelClass")
@IsTag
@Retention(RetentionPolicy.RUNTIME)
@interface AnnotationWithSingleValue {
    String value();
}

@SuppressWarnings("checkstyle:OneTopLevelClass")
@AnnotationWithSingleValue("testvalue")
class AnnotationWithSingleValueTestClass {
}

@SuppressWarnings("checkstyle:OneTopLevelClass")
@IsTag(name = "AnotherName")
@Retention(RetentionPolicy.RUNTIME)
@interface AnnotationWithName {
}

@SuppressWarnings("checkstyle:OneTopLevelClass")
@AnnotationWithName()
class AnnotationWithNameTestClass {
}

@SuppressWarnings("checkstyle:OneTopLevelClass")
@IsTag(ignoreValue = true)
@Retention(RetentionPolicy.RUNTIME)
@interface AnnotationWithIgnoredValue {
    String value();
}

@SuppressWarnings("checkstyle:OneTopLevelClass")
@AnnotationWithIgnoredValue("testvalue")
class AnnotationWithIgnoredValueTestClass {
}


@SuppressWarnings("checkstyle:OneTopLevelClass")
@IsTag
@Retention(RetentionPolicy.RUNTIME)
@interface AnnotationWithArray {
    String[] value();
}

@SuppressWarnings("checkstyle:OneTopLevelClass")
@AnnotationWithArray({"foo", "bar"})
class AnnotationWithArrayValueTestClass {
}


@SuppressWarnings("checkstyle:OneTopLevelClass")
@IsTag(explodeArray = false)
@Retention(RetentionPolicy.RUNTIME)
@interface AnnotationWithoutExplodedArray {
    String[] value();
}

@SuppressWarnings("checkstyle:OneTopLevelClass")
@AnnotationWithoutExplodedArray({"foo", "bar"})
class AnnotationWithoutExplodedArrayValueTestClass {
}


@SuppressWarnings("checkstyle:OneTopLevelClass")
@IsTag(description = "Some Description")
@Retention(RetentionPolicy.RUNTIME)
@interface TagWithDescription {
}

@SuppressWarnings("checkstyle:OneTopLevelClass")
@TagWithDescription
class AnnotationWithDescription {
}


@SuppressWarnings("checkstyle:OneTopLevelClass")
@IsTag(description = "Some Description", ignoreValue = true)
@Retention(RetentionPolicy.RUNTIME)
@TagWithDescription
@interface TagWithDescriptionAndIgnoreValue {
    String value();
}

@SuppressWarnings("checkstyle:OneTopLevelClass")
@TagWithDescriptionAndIgnoreValue(value = "some value")
class AnnotationWithDescriptionAndIgnoreValue {
}


@SuppressWarnings("checkstyle:OneTopLevelClass")
@IsTag
@Retention(RetentionPolicy.RUNTIME)
@interface ParentTag {
}

@SuppressWarnings("checkstyle:OneTopLevelClass")
@IsTag
@Retention(RetentionPolicy.RUNTIME)
@interface ParentTagWithValue {
    String value();
}

@SuppressWarnings("checkstyle:OneTopLevelClass")
@ParentTagWithValue("SomeValue")
@ParentTag
@IsTag
@Retention(RetentionPolicy.RUNTIME)
@interface TagWithParentTags {
}

@SuppressWarnings("checkstyle:OneTopLevelClass")
@TagWithParentTags
@IsTag
@Retention(RetentionPolicy.RUNTIME)
@interface TagWithGrandparentTags {
}



@SuppressWarnings("checkstyle:OneTopLevelClass")
@TagWithParentTags
class AnnotationWithParentTag {
}


