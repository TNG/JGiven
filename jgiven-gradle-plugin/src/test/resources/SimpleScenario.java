import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.junit.SimpleScenarioTest;
import org.junit.Test;

public class SimpleScenario extends SimpleScenarioTest<SimpleScenario.TestStep> {

    @Test
    public void some_scenario() {
        given().all_is_good();

        then().all_is_good();
    }

    public static class TestStep extends Stage<TestStep> {
        public TestStep all_is_good() {
            return self();
        }
    }
}