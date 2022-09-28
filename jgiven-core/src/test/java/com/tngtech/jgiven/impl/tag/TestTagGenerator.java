package com.tngtech.jgiven.impl.tag;

import com.tngtech.jgiven.impl.tag.ResolvedTags.ResolvedTag;
import com.tngtech.jgiven.report.model.Tag;
import java.util.Collections;
import java.util.Iterator;
import java.util.stream.StreamSupport;

class TestTagGenerator implements Iterator<ResolvedTag> {

    int count = 0;

    @Override
    public boolean hasNext() {
        return count < Integer.MAX_VALUE;
    }

    @Override
    public ResolvedTag next() {
        count++;
        return new ResolvedTag(
            new Tag("tag" + count),
            Collections.singletonList(new Tag("parent" + count))
        );
    }

    static ResolvedTags getEnumeratedResolvedTags(int number) {
        return StreamSupport.stream(
                ((Iterable<ResolvedTag>) TestTagGenerator::new).spliterator(), false)
            .limit(number)
            .collect(new TagCollector());
    }
}
