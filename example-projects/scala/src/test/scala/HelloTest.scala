import org.scalatest._
import org.scalatest.junit._

import org.junit.runner.RunWith
import org.junit._
import com.tngtech.jgiven.junit._

class HelloTest extends ScenarioTest[GivenStage, WhenStage, ThenStage] {
  
    @Test
    def my_first_JGiven_scenario_in_Scala = {
      
        given. some_value(5). 
          and. another_value(6)        
         when. adding_the_values
         then. the_result_should_be(11)
        
    }
}
