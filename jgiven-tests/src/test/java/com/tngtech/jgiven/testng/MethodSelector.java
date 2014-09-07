package com.tngtech.jgiven.testng;

import java.util.List;

import org.testng.IMethodSelector;
import org.testng.IMethodSelectorContext;
import org.testng.ITestNGMethod;

import com.tngtech.jgiven.testframework.TestNgExecutor;

@SuppressWarnings( "serial" )
public class MethodSelector implements IMethodSelector {
    @Override
    public boolean includeMethod( IMethodSelectorContext context, ITestNGMethod method, boolean isTestMethod ) {
        return method.getMethodName().equals( TestNgExecutor.methodName );
    }

    @Override
    public void setTestMethods( List<ITestNGMethod> testMethods ) {}
}