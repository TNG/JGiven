import org.scalatest._
import com.tngtech.jgiven.annotation._
import com.tngtech.jgiven._

class WhenStage {

  @ExpectedScenarioState
  var someInt = 0

  @ExpectedScenarioState
  var anotherInt = 0
  
  @ProvidedScenarioState
  var result = 0
  
  def adding_the_values = {
    result = someInt + anotherInt
  }
}