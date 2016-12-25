import com.tngtech.jgiven.junit.SimpleScenarioTest;
import org.junit.Test;

public class MavenTest extends SimpleScenarioTest<MavenTest.Steps> {

    @Test
    public void example_scenario() {
        given().some_context();
        when().some_action();
        then().some_outcome();
    }

    public static class Steps {

        public void some_context() {

        }

        public void some_action() {

        }

        public void some_outcome() {

        }
    }
}