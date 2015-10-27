package com.tngtech.jgiven.integration.spring;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.aop.config.AopNamespaceUtils;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.parsing.BeanComponentDefinition;
import org.springframework.beans.factory.parsing.CompositeComponentDefinition;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

public class AnnotationDrivenBeanDefinitionParser implements BeanDefinitionParser {

    private final Log logger = LogFactory.getLog(getClass());

    private static final String BEAN_NAME = "com.tngtech.jgiven.integration.spring.jGivenStageAutoProxyCreator";

    @Override
    public BeanDefinition parse( Element element, ParserContext parserContext ) {
        AopNamespaceUtils.registerAutoProxyCreatorIfNecessary( parserContext, element );
        if( !parserContext.getRegistry().containsBeanDefinition( BEAN_NAME ) ) {
            Object eleSource = parserContext.extractSource( element );

            // create Interceptor
            RootBeanDefinition interceptorDef = new RootBeanDefinition( SpringStepMethodInterceptor.class );
            interceptorDef.setScope( AbstractBeanDefinition.SCOPE_PROTOTYPE );
            interceptorDef.setSource( eleSource );
            interceptorDef.setRole( BeanDefinition.ROLE_INFRASTRUCTURE );
            String interceptorName = parserContext.getReaderContext().registerWithGeneratedName( interceptorDef );
            logger.debug( "Registered SpringStepMethodInterceptor with name " + interceptorName );

            // create Scenario Executor
            RootBeanDefinition executorDef = new RootBeanDefinition( SpringScenarioExecutor.class );
            executorDef.setScope( AbstractBeanDefinition.SCOPE_PROTOTYPE );
            executorDef.setSource( eleSource );
            interceptorDef.setRole( BeanDefinition.ROLE_INFRASTRUCTURE );
            String executorName = parserContext.getReaderContext().registerWithGeneratedName( executorDef );
            logger.debug( "Registered SpringScenarioExecutor with name " + executorName );

            // create AutoProxyCreator
            RootBeanDefinition autoProxyCreatorDef = new RootBeanDefinition( JGivenStageAutoProxyCreator.class );
            autoProxyCreatorDef.setRole( BeanDefinition.ROLE_INFRASTRUCTURE );
            parserContext.getRegistry().registerBeanDefinition( BEAN_NAME, autoProxyCreatorDef );

            CompositeComponentDefinition componentDefinition = new CompositeComponentDefinition( element.getTagName(), eleSource );
            componentDefinition.addNestedComponent( new BeanComponentDefinition( interceptorDef, interceptorName ) );
            componentDefinition.addNestedComponent( new BeanComponentDefinition( executorDef, executorName ) );
            componentDefinition.addNestedComponent( new BeanComponentDefinition( autoProxyCreatorDef, BEAN_NAME ) );
            parserContext.registerComponent( componentDefinition );
        }
        return null;
    }

}
