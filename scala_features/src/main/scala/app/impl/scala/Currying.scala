package app.impl.scala

import app.impl.Generic
import org.junit.Test


/**
  * Using curried functions  you can postpone the execution of a method and instead create a function,
  * this function it can be used to fill up the rest of arguments that needs, moment where it will be executed.
  */
class Currying extends Generic {


  @Test def main(): Unit = {
    def addFunction = add(2) _
    println(addFunction)
    println(addFunction(3))

    def stringFunction = multiString("hello")("scala") _
    println(stringFunction)
    println(stringFunction("world"))
  }

  def add(x: Int)(y: Int) = x + y

  def multiString(x: String)(y: String)(z: String) = x.concat("_") + y.concat("_") + z




}



