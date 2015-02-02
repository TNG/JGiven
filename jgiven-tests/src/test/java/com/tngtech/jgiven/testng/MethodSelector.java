package com.tngtech.jgiven.testng;

import java.util.List;

import org.testng.IMethodSelector;
import org.testng.IMethodSelectorContext;
import org.testng.ITestNGMethod;

@SuppressWarnings( "serial" )
public class MethodSelector implements IMethodSelector {
    @Override
    public boolean includeMethod( IMethodSelectorContext context, ITestNGMethod method, boolean isTestMethod ) {
        if( TestNgExecutor.methodName != null ) {
            return method.getMethodName().equals( TestNgExecutor.methodName );
        }
        return true;
    }

    @Override
    public void setTestMethods( List<ITestNGMethod> testMethods ) {}
}