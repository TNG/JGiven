package com.tngtech.jgiven.format;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.assertj.core.api.Java6Assertions.assertThat;

public class VarargsFormatterTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();
    private VarargsFormatter formatter;


    @Before
    public void setup() {
        this.formatter = new VarargsFormatter();
    }

    @Test
    public void testVarargsFormatSingle() {
        assertThat(formatter.format("value", "\"%s\"")).isEqualTo("\"value\"");
    }

    @Test
    public void testVarargsFormatSingleVarargs() {
        String[] varargs = {"value"};
        assertThat(formatter.format(varargs, "\"%s\"")).isEqualTo("\"value\"");
    }

    @Test
    public void testVarargsFormatMultiple() {
        Object[] varargs = {"value1", 1L};
        assertThat(formatter.format(varargs, "\"%s\"")).isEqualTo("\"value1\", \"1\"");
    }

    @Test
    public void testVarargsFormatNoFormatArgs() {
        String[] varargs = {"value"};

        thrown.expect(ArrayIndexOutOfBoundsException.class);
        formatter.format(varargs);
    }

    @Test
    public void testVarargsFormatNull() {
        String[] varargs = null;

        assertThat(formatter.format(varargs, "\"%s\"")).isEqualTo("\"null\"");
    }

}
