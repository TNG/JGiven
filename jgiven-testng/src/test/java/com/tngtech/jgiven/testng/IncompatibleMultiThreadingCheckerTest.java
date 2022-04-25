package com.tngtech.jgiven.testng;


import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.tngtech.jgiven.annotation.ScenarioStage;
import com.tngtech.jgiven.exception.JGivenWrongUsageException;
import org.testng.ITestResult;
import org.testng.annotations.Test;

@Test(singleThreaded = true)
@SuppressWarnings({"DefaultAnnotationParam", "unused"})
public class IncompatibleMultiThreadingCheckerTest {

    private final IncompatibleMultithreadingChecker underTest = new IncompatibleMultithreadingChecker();

    @Test
    public void testFailsIfMultithreadingDeclaredOnClass() throws NoSuchMethodException {
        @Test(singleThreaded = false)
        class MultiThreadedInjectedTestClass {
            @ScenarioStage
            private String injected;

            @Test(enabled = false)
            public void multithreadedTest() {
            }
        }

        ITestResult testInput = mockInput(MultiThreadedInjectedTestClass.class, "multithreadedTest");
        assertThatCode(() -> underTest.checkIncompatibleMultiThreading(testInput))
            .isInstanceOf(JGivenWrongUsageException.class);
    }

    @Test
    public void testNoErrorThrownIfSingleThreaded() throws NoSuchMethodException {
        @Test(singleThreaded = true)
        class SingleThreadedInjectedTestClass {
            @ScenarioStage
            private String injected;

            @Test(enabled = false)
            public void singleThreadedTest() {
            }
        }

        ITestResult testInput = mockInput(SingleThreadedInjectedTestClass.class, "singleThreadedTest");
        assertThatCode(() -> underTest.checkIncompatibleMultiThreading(testInput))
            .doesNotThrowAnyException();
    }

    @Test
    public void testNoErrorThrownIfNoInjectedStages() throws NoSuchMethodException {
        @Test(singleThreaded = false, enabled = false)
        class MultiThreadedTestClass {

            public void test() {
            }
        }

        ITestResult testInput = mockInput(MultiThreadedTestClass.class, "test");
        assertThatCode(() -> underTest.checkIncompatibleMultiThreading(testInput))
            .doesNotThrowAnyException();
    }

    private ITestResult mockInput(Class<?> testClass, String methodName) throws NoSuchMethodException {
        ITestResult testInput = mock(ITestResult.class, RETURNS_DEEP_STUBS);
        when(testInput.getTestClass().getRealClass()).then(invocation -> testClass);
        when(testInput.getMethod().getConstructorOrMethod().getMethod()).thenReturn(testClass.getMethod(methodName));
        return testInput;
    }
}
