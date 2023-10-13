package testpackage;

import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.junit5.SimpleScenarioTest;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
public class ThingDoerTest extends SimpleScenarioTest<ThingDoerTest.ThingDoerStage>{

    @Test
    void test(){
        given().a_thing_doer();
        when().do_thing();
        then().thing_doer_should_say_hello_world();
    }

    static class ThingDoerStage extends Stage<ThingDoerStage>{
        private ThingDoer underTest;
        private String result;
        public ThingDoerStage a_thing_doer(){
            underTest = new ThingDoer();
            return self();
        }
        public ThingDoerStage do_thing(){
            result = underTest.doThing();
            return self();
        }
        public ThingDoerStage thing_doer_should_say_hello_world(){
            assertThat(result).isEqualTo("Hello World!");
            return self();
        }
    }
}
