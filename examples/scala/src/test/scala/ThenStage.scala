import org.scalatest._
import com.tngtech.jgiven._
import com.tngtech.jgiven.annotation._
import Assertions._

class ThenStage extends Stage[ThenStage] {

  @ExpectedScenarioState
  var result = 0
  
  def the_result_should_be(expectedResult:Int) = {
    assert(result == expectedResult)
  }
  
}