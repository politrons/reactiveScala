package app.impl.macros

import app.impl.macros.Macros._
import app.impl.scalaz.TestDSL
import org.junit.Test

/**
  * Created by pabloperezgarcia on 09/07/2017.
  */
class Main extends TestDSL {

  @Test
  def helloWorld(): Unit = {
    hello("world!")
    printparam("test")
    debug("test2")
    println("End")
  }

  val y = 10

  def test() {
    val p = 11
    debug(p)
    debug(p + y)
  }

  @Test
  def testPrintln(): Unit ={
    test()
  }

  @Test
  def testDSL(): Unit = {
    Given(:: -> "A message with version 2.0", 1)
      .When(:: -> "add '10'")
      .And(:: -> "multiply by '20'")
      .Then(:: -> "The result should be higher than '100'")
      .runScenario
  }

  @Test
  def testErrorDSL(): Unit = {
    Given(:: -> "Giving a number",1)
      .When(:: -> "multiply by '20'")
      .Then(:: -> "The result should be higher than '100'")
      .runScenario
  }

  @Test
  def testX(): Unit ={
    testMain()
  }

  def testMain(): Unit = {
   println("done")
  }

}
