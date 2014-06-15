package com.tngtech.jgiven.impl.util;

import java.lang.reflect.AccessibleObject;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.tngtech.jgiven.exception.JGivenExecutionException;
import com.tngtech.jgiven.exception.JGivenInjectionException;

public class ReflectionUtilTest {

    @Rule
    public final ExpectedException expectedException = ExpectedException.none();

    static class TestClass {
        private String testField;

        private void testMethod( Integer someArg ) {}
    }

    @Test
    public void injection_exception_is_thrown_if_field_cannot_be_set() throws Exception {
        expectedException.expect( JGivenInjectionException.class );
        ReflectionUtil.setField( TestClass.class.getDeclaredField( "testField" ), new TestClass(), 5, "test description" );
    }

    @Test
    public void makeAccessible_does_not_throw_execptions() throws Exception {
        AccessibleObject stub = new AccessibleObject() {
            @Override
            public void setAccessible( boolean flag ) throws SecurityException {
                throw new SecurityException();
            }
        };
        ReflectionUtil.makeAccessible( stub, "test" );
    }

    @Test
    public void execution_exception_is_thrown_if_method_cannot_be_invoked() throws Exception {
        expectedException.expect( JGivenExecutionException.class );
        TestClass testClass = new TestClass();
        ReflectionUtil.invokeMethod( testClass, TestClass.class.getDeclaredMethod( "testMethod", Integer.class ), "test description" );
    }

}
