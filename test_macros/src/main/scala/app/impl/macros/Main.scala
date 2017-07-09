package app.impl.macros

import app.impl.macros.DSLValidator.{->}
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
    Macros hello "world"
    Macros Given ->("Make a request to server")
    println("End")


  }


}
