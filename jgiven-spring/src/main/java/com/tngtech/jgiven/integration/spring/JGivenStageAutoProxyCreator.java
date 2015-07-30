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
 * <code>
 *   {@literal @}Bean<br>
 *   public JGivenStageAutoProxyCreator jGivenStageAutoProxyCreator() {<br>
 *   &nbsp;&nbsp;&nbsp;&nbsp;return new JGivenStageAutoProxyCreator();<br>
 *   }<br>
 *
 * </code>
 * @since 0.7.4
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
