package com.tngtech.jgiven.impl.tag;

import java.util.HashSet;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

class TagCollector implements Collector<ResolvedTags.ResolvedTag, ResolvedTags, ResolvedTags> {
    @Override
    public Supplier<ResolvedTags> supplier() {
        return ResolvedTags::new;
    }

    @Override
    public BiConsumer<ResolvedTags, ResolvedTags.ResolvedTag> accumulator() {
        return (container, element) -> container.resolvedTags.add(element);
    }

    @Override
    public BinaryOperator<ResolvedTags> combiner() {
        return (one, other) -> {
            one.resolvedTags.addAll(other.resolvedTags);
            return one;
        };
    }

    @Override
    public Function<ResolvedTags, ResolvedTags> finisher() {
        return Function.identity();
    }

    @Override
    public Set<Characteristics> characteristics() {
        Set<Characteristics> characteristics = new HashSet<>();
        characteristics.add(Characteristics.IDENTITY_FINISH);
        return characteristics;
    }
}
