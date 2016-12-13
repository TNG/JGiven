package com.tngtech.jgiven.integration.spring;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.aop.config.AopNamespaceUtils;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.parsing.BeanComponentDefinition;
import org.springframework.beans.factory.parsing.CompositeComponentDefinition;
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

            RootBeanDefinition stageCreator = new RootBeanDefinition( SpringStageCreator.class );
            stageCreator.setSource( eleSource );
            stageCreator.setRole( BeanDefinition.ROLE_INFRASTRUCTURE );
            String executorName = parserContext.getReaderContext().registerWithGeneratedName( stageCreator );
            logger.debug( "Registered SpringStageCreator with name " + executorName );

            RootBeanDefinition beanFactoryPostProcessor = new RootBeanDefinition( JGivenBeanFactoryPostProcessor.class );
            beanFactoryPostProcessor.setRole( BeanDefinition.ROLE_INFRASTRUCTURE );
            parserContext.getRegistry().registerBeanDefinition( BEAN_NAME, beanFactoryPostProcessor );

            CompositeComponentDefinition componentDefinition = new CompositeComponentDefinition( element.getTagName(), eleSource );
            componentDefinition.addNestedComponent( new BeanComponentDefinition( stageCreator, executorName ) );
            componentDefinition.addNestedComponent( new BeanComponentDefinition( beanFactoryPostProcessor, BEAN_NAME ) );
            parserContext.registerComponent( componentDefinition );
        }
        return null;
    }

}
