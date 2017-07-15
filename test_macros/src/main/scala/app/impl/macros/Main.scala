package app.impl.macros

import app.impl.macros.Macros.hello
import app.impl.scalaz.TestDSL
import org.junit.Test

/**
  * Created by pabloperezgarcia on 09/07/2017.
  */
class Main extends TestDSL {

  @Test
  def helloWorld(): Unit = {
    hello("world!")
  }

  @Test
  def TestDSLValidator(): Unit = {
    hello("world")
    Given(:: -> "Make a request to server")
    println("End")
  }

  @Test
  def testDSL(): Unit = {
    Given("Giving a number", 1)
      .When("add '10'")
      .And("multiply by '20'")
      .Then("The result should be higher than '100'")
      .runScenario
  }

  @Test
  def testErrorDSL(): Unit = {
    Given("Giving a number", 1)
      .When("multiply by '20'")
      .Then("The result should be higher than '100'")
      .runScenario
  }


  def Given(message: String) = {
    println(message)
  }

  def When(message: String) = {
    println("When")
  }


}
