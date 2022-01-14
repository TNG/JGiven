import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;


public class JUnit5Tests {

    @Test
    void testThatPass() {
        assumeTrue(true);
        assertEquals(1, 1);
    }

    @Test
    @Tag("TestJunit5Tag")
    void testThatFail() {
        assumeTrue(true);
        assertEquals(1, 2);
    }

    @Test
    void testThatWouldPassButIsSkipped() {
        assumeTrue(false);
        assertEquals(1, 1);
    }

    @Test
    void testThatWouldFailButIsSkipped() {
        assumeTrue(false);
        assertEquals(1, 2);
    }
}


