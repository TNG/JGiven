import org.scalatest._
import com.tngtech.jgiven.annotation._

class Steps extends Matchers {

  var someInt = 0
  
  def some_state = {
    someInt = 5
  }
  
  def some_action = {
    someInt *= 2
  }
  
  def some_outcome = {
    someInt should be (10)
  }
  
}