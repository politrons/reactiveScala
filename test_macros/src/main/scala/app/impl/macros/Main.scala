package app.impl.macros

import org.junit.Test


/**
  * Created by pabloperezgarcia on 09/07/2017.
  */
class Main {

  @Test
  def helloWorld(): Unit = {

    Macros.hello("world!")

  }

  @Test
  def TestDSL(): Unit = {
    DSLValidator.Given("Make a request to server")
    DSLValidator.When("Payload code=works")
    println("Works!")

  }

}
