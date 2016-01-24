import com.tngtech.jgiven.annotation._
import com.tngtech.jgiven._

class GivenStage extends Stage[GivenStage] {

  @ProvidedScenarioState
  var someInt = 0
  
  @ProvidedScenarioState
  var anotherInt = 0
  
  def some_value(a:Int) = {
    someInt = a
    this
  }
  
  def another_value(b:Int) = {
    anotherInt = b
    this
  }
  
}