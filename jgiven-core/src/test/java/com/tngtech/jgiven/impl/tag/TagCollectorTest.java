package com.tngtech.jgiven.impl.tag;

import static org.assertj.core.api.Assertions.assertThat;

import com.tngtech.jgiven.impl.tag.ResolvedTags.ResolvedTag;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import org.junit.Test;

public class TagCollectorTest {

    private final TagCollector underTest = new TagCollector();
    private final TestTagGenerator tagGenerator = new TestTagGenerator();

    @Test
    public void testCollectorCollectsTags() {
        ResolvedTag tag1 = tagGenerator.next();
        ResolvedTag tag2 = tagGenerator.next();

        ResolvedTags result = Stream.of(tag1, tag2).collect(underTest);

        assertThat(result.resolvedTags).contains(tag1, tag2);
    }

    @Test
    public void testCollectorCanMergeParallelResults() {
        ResolvedTags resolvedTags = StreamSupport.stream(
            ((Iterable<ResolvedTag>) () -> tagGenerator).spliterator(), true)
            .limit(50)
            .collect(underTest);
        assertThat(resolvedTags.resolvedTags).hasSize(50);
    }

}
