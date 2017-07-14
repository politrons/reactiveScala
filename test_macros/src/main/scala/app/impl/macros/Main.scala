package app.impl.macros

import app.impl.macros.Macros.hello
import app.impl.scalaz.TestDSL
import org.junit.Test

/**
  * Created by pabloperezgarcia on 09/07/2017.
  */
class Main extends TestDSL{

  @Test
  def helloWorld(): Unit = {
    hello("world!")
  }

  @Test
  def TestDSL(): Unit = {
    hello("world")
    Given(:: -> "Make a request to server")
    println("End")

  }


  def Given(message: String) = {
    println(message)
  }

  def When(message: String) = {
    println("When")
  }


}
