package com.tngtech.jgiven.report.model;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.util.function.Function;

/**
 * Marker interface for string wrappers used to provide some level of type safety
 */
public interface StringWrapper {
    static class StringWrapperConverter<T extends StringWrapper> extends TypeAdapter<T> {

        private final Function<String, T> instantiator;

        protected StringWrapperConverter(Function<String, T> instantiator) {
            this.instantiator = instantiator;
        }

        @Override
        public void write(JsonWriter out, T value) throws IOException {
            out.value(value.toString());
        }

        @Override
        public T read(JsonReader in) throws IOException {
            return instantiator.apply(in.nextString());
        }

    }
}
