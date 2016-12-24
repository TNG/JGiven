import org.scalatest._
import org.scalatest.junit._

import org.junit.runner.RunWith
import org.junit._
import com.tngtech.jgiven.junit._

class SimpleHelloTest extends SimpleScenarioTest[Steps] {
  
    @Test
    def my_first_JGiven_scenario_in_Scala = {
      
        given.some_state
        when.some_action
        then.some_outcome
        
    }
}
