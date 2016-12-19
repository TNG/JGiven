package com.tngtech.jgiven.integration.spring.test.proxy;

import com.tngtech.jgiven.integration.spring.EnableJGiven;
import org.springframework.aop.framework.autoproxy.BeanNameAutoProxyCreator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;

@EnableJGiven
@ComponentScan( basePackages = "com.tngtech.jgiven.integration.spring.test.proxy" )
@Import(TestAspect.class)
@EnableAspectJAutoProxy(proxyTargetClass=true)
public class ProxyTestConfig {
}
