package com.tngtech.jgiven.integration.spring;

import org.springframework.beans.BeansException;
import org.springframework.beans.FatalBeanException;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;

import com.tngtech.jgiven.impl.ByteBuddyStageClassCreator;
import com.tngtech.jgiven.impl.CachingStageClassCreator;
import com.tngtech.jgiven.impl.StageClassCreator;

public class JGivenBeanFactoryPostProcessor implements BeanFactoryPostProcessor {

    private final StageClassCreator stageClassCreator = new CachingStageClassCreator( new ByteBuddyStageClassCreator() );

    @Override
    public void postProcessBeanFactory( ConfigurableListableBeanFactory beanFactory ) throws BeansException {
        String[] beanNames = beanFactory.getBeanDefinitionNames();
        for( String beanName : beanNames ) {
            if( beanFactory.containsBeanDefinition( beanName ) ) {
                BeanDefinition beanDefinition = beanFactory.getBeanDefinition( beanName );
                if( beanDefinition instanceof AnnotatedBeanDefinition ) {
                    AnnotatedBeanDefinition annotatedBeanDefinition = (AnnotatedBeanDefinition) beanDefinition;
                    if( annotatedBeanDefinition.getMetadata().hasAnnotation( JGivenStage.class.getName() ) ) {
                        String className = beanDefinition.getBeanClassName();
                        Class<?> stageClass = createStageClass( beanName, className );
                        beanDefinition.setBeanClassName( stageClass.getName() );
                    }
                }
            }
        }
    }

    private Class<?> createStageClass( String beanName, String className ) {
        try {
            Class<?> aClass = Thread.currentThread().getContextClassLoader().loadClass( className );
            return stageClassCreator.createStageClass( aClass );
        } catch( ClassNotFoundException e ) {
            throw new FatalBeanException( "Error while trying to create JGiven stage for bean " + beanName, e );
        }
    }
}
