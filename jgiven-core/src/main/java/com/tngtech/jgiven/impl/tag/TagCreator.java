package com.tngtech.jgiven.impl.tag;

import com.google.common.base.Strings;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.tngtech.jgiven.annotation.IsTag;
import com.tngtech.jgiven.config.AbstractJGivenConfiguration;
import com.tngtech.jgiven.config.TagConfiguration;
import com.tngtech.jgiven.exception.JGivenWrongUsageException;
import com.tngtech.jgiven.report.model.Tag;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Handles the conversion of Annotations on Classes and Methods to displayable Tags.
 */
public class TagCreator {

    private static final Logger log = LoggerFactory.getLogger(TagCreator.class);

    private final AbstractJGivenConfiguration configuration;

    public TagCreator(AbstractJGivenConfiguration configuration) {

        this.configuration = configuration;
    }

    /**
     * Turns an annotation class and a manually supplied set of annotation values into tags.
     * Permits the programmatic creation of tags.
     */
    public ResolvedTags toTags(Class<? extends Annotation> annotationClass, String... values) {
        TagConfiguration tagConfig = toTagConfiguration(annotationClass);
        if (tagConfig == null) {
            return new ResolvedTags();
        }

        List<Tag> tags = processConfiguredAnnotation(tagConfig);
        if (tags.isEmpty()) {
            return new ResolvedTags();
        }

        List<Tag> parents = getTags(annotationClass);
        if (values.length > 0) {
            List<Tag> explodedTags = getExplodedTags(Iterables.getOnlyElement(tags), values, null, tagConfig);
            return explodedTags.stream()
                .map(tag -> new ResolvedTags.ResolvedTag(tag, parents))
                .collect(new TagCollector());
        } else {
            return ResolvedTags.from(new ResolvedTags.ResolvedTag(Iterables.getOnlyElement(tags), parents));
        }
    }

    /**
     * Turns an annotation defined on a class or method into tags.
     * Permits the declarative creation of tags
     */
    public ResolvedTags toTags(Annotation annotation) {
        Class<? extends Annotation> annotationType = annotation.annotationType();
        TagConfiguration tagConfig = toTagConfiguration(annotationType);
        if (tagConfig == null) {
            return new ResolvedTags();
        }

        List<Tag> tags = processConfiguredAnnotation(tagConfig, annotation);
        List<Tag> parents = getTags(annotationType);
        return tags.stream()
            .map(tag -> new ResolvedTags.ResolvedTag(tag, parents))
            .collect(new TagCollector());
    }

    private List<Tag> processConfiguredAnnotation(TagConfiguration tagConfig, Annotation annotation) {
        if (tagConfig.isIgnoreValue()) {
            return processConfiguredAnnotation(tagConfig);
        }
        Tag tag = createStyledTag(tagConfig);
        tag.setTags(tagConfig.getTags());

        Optional<Object> valueOptional = getValuesFromAnnotation(annotation);
        if (valueOptional.isPresent()) {
            Object value = valueOptional.get();
            if (value.getClass().isArray()) {
                if (tagConfig.isExplodeArray()) {
                    return getExplodedTags(tag, (Object[]) value, annotation, tagConfig);
                } else {
                    tag.setValue(toStringList((Object[]) value));
                }
            } else {
                tag.setValue(String.valueOf(value));
            }
        } else {
            setIfNotNullOrEmpty(tagConfig.getDefaultValue(), tag::setValue);
        }
        tag.setDescription(getDescriptionFromGenerator(tagConfig, annotation, valueOptional.orElse(null)));
        tag.setHref(getHref(tagConfig, annotation, valueOptional.orElse(null)));
        return Collections.singletonList(tag);
    }

    private List<Tag> processConfiguredAnnotation(TagConfiguration tagConfig) {
        if (!tagConfig.isIgnoreValue()) {
            log.warn(
                "Tag configuration 'ignoreValue', set to 'false' is ignored, "
                    + "because no annotation that could be respected was given.");
        }

        Tag tag = createStyledTag(tagConfig);
        tag.setTags(tagConfig.getTags());
        String value = tagConfig.getDefaultValue();

        setIfNotNullOrEmpty(value, tag::setValue);
        tag.setDescription(getDescriptionFromGenerator(tagConfig, null, value));
        tag.setHref(getHref(tagConfig, null, value));

        return Collections.singletonList(tag);
    }

    private Tag createStyledTag(TagConfiguration tagConfig) {
        Tag tag = new Tag(tagConfig.getAnnotationFullType());

        tag.setType(tagConfig.getAnnotationType());
        tag.setPrependType(tagConfig.isPrependType());
        tag.setShowInNavigation(tagConfig.showInNavigation());

        setIfNotNullOrEmpty(tagConfig.getName(), tag::setName);
        setIfNotNullOrEmpty(tagConfig.getCssClass(), tag::setCssClass);
        setIfNotNullOrEmpty(tagConfig.getColor(), tag::setColor);
        setIfNotNullOrEmpty(tagConfig.getStyle(), tag::setStyle);
        return tag;
    }

    private void setIfNotNullOrEmpty(String value, Consumer<String> setter) {
        if (!Strings.isNullOrEmpty(value)) {
            setter.accept(value);
        }
    }

    private Optional<Object> getValuesFromAnnotation(Annotation annotation) {
        try {
            Method method = annotation.annotationType().getMethod("value");
            return Optional.ofNullable(method.invoke(annotation));
        } catch (NoSuchMethodException ignoreAnnotationsThatAreNotTags) {
            return Optional.empty();
        } catch (Exception e) {
            log.error("Error while getting 'value' method of annotation " + annotation, e);
            return Optional.empty();
        }
    }

    TagConfiguration toTagConfiguration(Class<? extends Annotation> annotationType) {
        IsTag isTag = annotationType.getAnnotation(IsTag.class);
        if (isTag != null) {
            return fromIsTag(isTag, annotationType);
        }

        return configuration.getTagConfiguration(annotationType);
    }

    private TagConfiguration fromIsTag(IsTag isTag, Class<? extends Annotation> annotationType) {
        String name = isTag.name();

        return TagConfiguration.builder(annotationType)
            .defaultValue(isTag.value())
            .description(isTag.description())
            .explodeArray(isTag.explodeArray())
            .ignoreValue(isTag.ignoreValue())
            .prependType(isTag.prependType())
            .name(name)
            .descriptionGenerator(isTag.descriptionGenerator())
            .cssClass(isTag.cssClass())
            .color(isTag.color())
            .style(isTag.style())
            .tags(getTagNames(annotationType))
            .href(isTag.href())
            .hrefGenerator(isTag.hrefGenerator())
            .showInNavigation(isTag.showInNavigation())
            .build();
    }

    private List<String> getTagNames(Class<? extends Annotation> annotationType) {
        List<Tag> tags = getTags(annotationType);
        List<String> tagNames = Lists.newArrayList();
        for (Tag tag : tags) {
            tagNames.add(tag.toIdString());
        }
        return tagNames;
    }

    private List<Tag> getTags(Class<? extends Annotation> annotationType) {
        List<Tag> allTags = Lists.newArrayList();

        for (Annotation annotation : annotationType.getAnnotations()) {
            if (annotation.annotationType().isAnnotationPresent(IsTag.class)) {
                allTags.addAll(toTags(annotation).resolvedTags.stream()
                    .flatMap(tag -> {
                        Stream<Tag> tagStream = Stream.of(tag.tag);
                        return Stream.concat(tagStream, tag.parents.stream());
                    })
                    .collect(Collectors.toList()));
            }
        }

        return allTags;
    }

    private List<String> toStringList(Object[] value) {
        List<String> values = Lists.newArrayList();
        for (Object v : value) {
            values.add(String.valueOf(v));
        }
        return values;
    }

    private String getDescriptionFromGenerator(TagConfiguration tagConfiguration, Annotation annotation, Object
        value) {
        try {
            return tagConfiguration.getDescriptionGenerator().getDeclaredConstructor().newInstance()
                .generateDescription(tagConfiguration, annotation, value);
        } catch (Exception e) {
            throw new JGivenWrongUsageException(
                "Error while trying to generate the description for annotation " + annotation
                    + " using DescriptionGenerator class "
                    + tagConfiguration.getDescriptionGenerator() + ": " + e.getMessage(),
                e);
        }
    }

    private String getHref(TagConfiguration tagConfiguration, Annotation annotation, Object value) {
        try {
            return tagConfiguration.getHrefGenerator().getDeclaredConstructor().newInstance()
                .generateHref(tagConfiguration, annotation, value);
        } catch (Exception e) {
            throw new JGivenWrongUsageException(
                "Error while trying to generate the href for annotation " + annotation
                    + " using HrefGenerator class "
                    + tagConfiguration.getHrefGenerator() + ": " + e.getMessage(),
                e);
        }
    }

    private List<Tag> getExplodedTags(Tag originalTag, Object[] values, Annotation annotation,
                                      TagConfiguration tagConfig) {
        List<Tag> result = Lists.newArrayList();
        for (Object singleValue : values) {
            Tag newTag = originalTag.copy();
            newTag.setValue(String.valueOf(singleValue));
            newTag.setDescription(getDescriptionFromGenerator(tagConfig, annotation, singleValue));
            newTag.setHref(getHref(tagConfig, annotation, singleValue));
            result.add(newTag);
        }
        return result;
    }

}
