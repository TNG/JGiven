package com.tngtech.jgiven.impl.util;

import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;
import com.tngtech.jgiven.exception.JGivenExecutionException;
import com.tngtech.jgiven.exception.JGivenInjectionException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import java.lang.reflect.AccessibleObject;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(DataProviderRunner.class)
public class ReflectionUtilTest {

    @Rule
    public final ExpectedException expectedException = ExpectedException.none();

    static class TestClass {
        private String testField;

        private void testMethod(Integer someArg) {
        }
    }

    @DataProvider
    public static List<Object> reflectionPrimitives() {
        return Arrays.asList("b".getBytes()[0], 1, 1L, 1.2f, 1e300, 'a', true);
    }

    //TODO: define String / BigInt Reflection behavior.
    @Test
    @UseDataProvider("reflectionPrimitives")
    public void wrapper_types_are_returned_as_themselves(Object input) throws Exception {
        List<Object> output = ReflectionUtil.getAllFieldValues(input, ReflectionUtil.getAllNonStaticFields(input.getClass()), "");

        assertThat(output).containsExactly(input);
    }

    @Test
    public void injection_exception_is_thrown_if_field_cannot_be_set() throws Exception {
        expectedException.expect(JGivenInjectionException.class);
        ReflectionUtil.setField(TestClass.class.getDeclaredField("testField"), new TestClass(), 5, "test description");
    }

    @Test
    public void makeAccessible_does_not_throw_execptions() throws Exception {
        AccessibleObject stub = new AccessibleObject() {
            @Override
            public void setAccessible(boolean flag) throws SecurityException {
                throw new SecurityException();
            }
        };
        ReflectionUtil.makeAccessible(stub, "test");
    }

    @Test
    public void execution_exception_is_thrown_if_method_cannot_be_invoked() throws Exception {
        expectedException.expect(JGivenExecutionException.class);
        TestClass testClass = new TestClass();
        ReflectionUtil.invokeMethod(testClass, TestClass.class.getDeclaredMethod("testMethod", Integer.class), "test description");
    }

}
