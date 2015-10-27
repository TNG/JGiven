package com.tngtech.jgiven.integration.spring;

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

public class JGivenNamespaceHandler extends NamespaceHandlerSupport {

    @Override
    public void init() {
        registerBeanDefinitionParser( "annotation-driven", new AnnotationDrivenBeanDefinitionParser() );
    }

}
