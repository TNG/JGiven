package com.tngtech.jgiven.integration.spring.test.proxy;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Aspect
public class TestAspect {

    private static final Logger log = LoggerFactory.getLogger(TestAspect.class);

    @Before( "execution(public * com.tngtech.jgiven.integration.spring.test.proxy.*WithAspect.*(..))" )
    public void intercept( JoinPoint joinPoint ) {
        log.info("executing " + joinPoint.getSignature());
    }
}
