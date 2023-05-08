package com.tngtech.jgiven.format;

import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatCode;


@RunWith(DataProviderRunner.class)
public class DefaultFormatterTest {


    @DataProvider
    public static List<?> primitivesProvider() {
        return List.of(
                new boolean[]{true, false},
                "I define myself as a byte array".getBytes(StandardCharsets.UTF_8),
                new short[]{1},
                new int[]{1},
                new long[]{1L},
                "I identify as a char array".toCharArray(),
                new float[]{1.2f},
                new double[] {1e300}
        );
    }

    @Test
    @UseDataProvider("primitivesProvider")
    public void handlesPrimitiveArrays(Object primitveArray){
        DefaultFormatter<Object> underTest = new DefaultFormatter<>();
        assertThatCode(()->underTest.format(primitveArray)).doesNotThrowAnyException();
    }

    @Test
    public void formatsAPrimitiveArray(){
        DefaultFormatter<char[]> underTest = new DefaultFormatter<>();
        String result = underTest.format("comma me up".toCharArray());
        assertThat(result).isEqualTo("c, o, m, m, a,  , m, e,  , u, p");
    }

    @Test
    public void ignoresSecondaryArguments() throws Exception{
        DefaultFormatter<String> underTest = new DefaultFormatter<>();
        String str = "You don't know how complex I am inside";
        assertThat(underTest.format(str, getClass().getAnnotations()))
                .isEqualTo(underTest.format(str, "What am I doing here?", "No clue either"))
                .isEqualTo(underTest.format(str))
                .isEqualTo(str);
    }

}