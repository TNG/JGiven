package com.tngtech.jgiven.example.springboot;

import org.junit.Test;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockServletContext;
import org.springframework.test.context.web.WebAppConfiguration;

import com.tngtech.jgiven.annotation.As;
import com.tngtech.jgiven.annotation.JGivenConfiguration;
import com.tngtech.jgiven.integration.spring.SimpleSpringRuleScenarioTest;

@SpringBootTest( classes = { MockServletContext.class, HelloTestContext.class } )
@JGivenConfiguration( HelloJGivenConfiguration.class )
@AutoConfigureTestEntityManager
public class JpaTest extends SimpleSpringRuleScenarioTest<JpaStage> {

    @Test
    public void test_entity_manager_can_be_used() throws Exception {
        given().test_entity_manager_is_defined();
    }


}
