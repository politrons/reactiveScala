package app.impl.scala

import app.impl.Generic
import org.junit.Test


/**
  */
class Utils extends Generic {

  /**
    * Arithmetic operators item by item emitted.
    */
  @Test def arithmetic(): Unit = {
    val product = List(1, 2, 3, 4, 5).toStream
      .map(n => n * 1)
      .filter(n => n >= 1)
      .product
    println(product)

    val sum = List(1, 2, 3, 4, 5).toStream
      .map(n => n * 1)
      .filter(n => n >= 1)
      .sum
    println(sum)

    val sub = List(1, 2, 3, 4, 5).toStream
      .map(n => n * 1)
      .filter(n => n >= 1)
      .reduce((a, b) => a - b)
    println(sub)

  }

  /**
    * Option is a collection that is used to wrap your object and void null returns,
    * in case that you want to return an old fashion null, you can use None.
    * Also ypi wrap your value in Some, which represent the class value
    */
  @Test def options(): Unit = {
    var username: Option[String] = None
    var finalVal = getUsername(username)
    println(finalVal)

    username = Some("pablo")
    finalVal = getUsername(username)
    println(finalVal)

    finalVal = getUsername(null)
    println(finalVal)
  }

  def getUsername(username: Option[String]): String = {
    username match {
      case None => "None"
      case Some(value) => username.get
      case _ => null
    }
  }

  @Test def partialFunction(): Unit = {
    val pf: PartialFunction[Int /*Entry type*/ , String /*Output type*/ ] = {
      case b if b > 100 => "higher"
    }
    val orElseFunction: (Int => String) = pf orElse { case _ => "lower" }
    println(orElseFunction(101))
    println(orElseFunction(99))
  }


  /**
    * Using multi method param list you can pospone the execution of a method and instead create a function,
    * this function it can be used to fill up the rest of arguments that needs, moment where it will be executed.
    */
  @Test def multiMethodParams(): Unit = {
    def addFunction = add(2) _
    println(addFunction(3))

    def stringFunction = multiString("hello")("scala") _
    println(stringFunction)
    def string2 = stringFunction("world")
    println(string2)
  }

  def add(x: Int)(y: Int) = x + y

  def multiString(x: String)(y: String)(z: String) = x.concat("_") + y.concat("_") + z



}



