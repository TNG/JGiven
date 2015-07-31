package com.tngtech.jgiven.integration.spring;

import org.springframework.aop.TargetSource;
import org.springframework.aop.framework.autoproxy.AbstractAutoProxyCreator;
import org.springframework.beans.BeansException;

/**
 * AutoProxyCreator that creates JGiven advices for all beans that
 * are annotated with the {@link JGivenStage} annotation. See below
 * on how to configure this bean.
 *
 * <p>
 * Sample configuration:<br>
 * <pre>
 *   {@literal @}Bean
 *   public JGivenStageAutoProxyCreator jGivenStageAutoProxyCreator() {
 *       return new JGivenStageAutoProxyCreator();
 *   }
 *
 * </pre>
 * @since 0.8.0
 */
public class JGivenStageAutoProxyCreator extends AbstractAutoProxyCreator {

    private static final long serialVersionUID = 1L;

    @Override
    protected Object[] getAdvicesAndAdvisorsForBean(Class<?> beanClass,
            String beanName, TargetSource customTargetSource)
            throws BeansException {
        if (beanClass.isAnnotationPresent(JGivenStage.class)) {
            return new Object[] { getBeanFactory().getBean(SpringStepMethodInterceptor.class) };
        }
        return DO_NOT_PROXY;
    }

}
