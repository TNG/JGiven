import com.tngtech.jgiven.junit5.JGivenExtension;
import org.junit.jupiter.api.Test;

import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.junit5.ScenarioTest;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GivenSomeState extends Stage<GivenSomeState> {
    public GivenSomeState some_state() {
        return self();
    }
}

class WhenSomeAction extends Stage<WhenSomeAction> {
    public WhenSomeAction some_action() {
        return self();
    }
}

class ThenSomeOutcome extends Stage<ThenSomeOutcome> {
    public ThenSomeOutcome some_outcome() {
        assertEquals(1, 1);
        return self();
    }

    public ThenSomeOutcome failed_outcome() {
        assertEquals(1, 2);
        return self();
    }
}

public class JGivenTests extends ScenarioTest<GivenSomeState, WhenSomeAction, ThenSomeOutcome> {

    @Test
    public void something_should_happen() {
        given().some_state();
        when().some_action();
        then().some_outcome();
    }

    @Test
    @ExtendWith({JGivenExtension.class})
    public void something_should_fail() {
        given().some_state();
        when().some_action();
        then().failed_outcome();
    }
}
