package com.tngtech.jgiven.example.springboot;

import com.tngtech.jgiven.integration.spring.SimpleSpringRuleScenarioTest;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockServletContext;
import org.springframework.test.context.web.WebAppConfiguration;

import com.tngtech.jgiven.annotation.As;
import com.tngtech.jgiven.annotation.JGivenConfiguration;

@SpringBootTest( classes = { MockServletContext.class, HelloTestContext.class } )
@WebAppConfiguration
@JGivenConfiguration( HelloJGivenConfiguration.class )
public class HelloControllerTest extends SimpleSpringRuleScenarioTest<HelloStage> {

    @Test
    public void the_root_path_returns_greetings_from_JGiven() throws Exception {
        when().get( "/" );
        then().the_status_is( HttpStatus.OK )
            .and().the_content_is( "Greetings from JGiven!" );
    }

    @Test
    @As( "The path '/foo' returns NOT FOUND" )
    public void the_path_foo_returns_not_found() throws Exception {
        when().get( "/foo" );
        then().the_status_is( HttpStatus.NOT_FOUND );
    }

}