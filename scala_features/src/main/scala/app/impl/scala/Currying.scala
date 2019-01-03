package app.impl.scala

import app.impl.Generic
import org.junit.Test


/**
  * Using curried functions  you can postpone the execution of a method and instead create a function,
  * this function it can be used to fill up the rest of arguments that needs, moment where it will be executed.
  *
  * In case you dont define your def as function return type, you need to pass a _ as the missing parameter
  *
  */
class Currying extends Generic {


  @Test
  def curryFunction(): Unit = {
    val curryA = funcA _ compose funcB apply funcA
    println(curryA)

    val curryB = funcB _ andThen  funcA apply funcA
    println(curryB)
  }

  def funcA(value: String):String = value.toUpperCase

  def funcB(func: String => String):String = func.apply("hello") + "-" + func.apply("world")


  @Test def main(): Unit = {
    def addFunction = add(2) _ //In case you dont define your def as function return type, you need to pass a _ as the missing parameter

    println(addFunction)
    println(addFunction(3))
    //Without sugar
    //println(addFunction.apply(3))

    def multiString(x: String)(y: String)(z: String) = x.concat("_") + y.concat("_") + z

    def stringFunction: String => String = multiString("hello")("scala")

    println(stringFunction)
    println(stringFunction("world"))

    def mergeFunction: String => String = mergeAction("Hello ")

    println(mergeFunction)
    println(mergeFunction("Again"))
  }

  def add(x: Int)(y: Int) = x + y

  def mergeAction(value: String) = (value2: String) => {
    value.concat(value2)
  }


}



