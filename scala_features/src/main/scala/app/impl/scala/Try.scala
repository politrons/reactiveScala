package app.impl.scala

import org.junit.Test

import scala.util.Try


/**
  * Try give you the feature to wrap a functionality into this operator, and this one will return success wrapping the result
  * or Failure wrapping the exception and description.
  */
class Try {


  @Test def trySuccess(): Unit = {
    val list = List("1", "2", "3", "4", "5")

    val result = for {
      number <- Try(list.map(number => Integer.parseInt(number)))
    } yield number

    println(result)
  }

  @Test def tryFailure(): Unit = {
    val list = List("1", "2", "3", "4", "ups", "5")

    val result = for {
      number <- Try(list.map(number => Integer.parseInt(number)))
    } yield number

    println(result.isFailure)
    println(result)
  }

  @Test def tryCustomThrowable(): Unit = {
    val list = List("1", "2", "3", "4", "200", "5")
    val result = for {
      number <- Try(list.map(number => extractNumber(number)))
    } yield number
    println(result)
  }

  private def extractNumber(sNumber: String):Int=  {
    val number = Integer.parseInt(sNumber)
    if (number > 100) {
      throw BigNumberException(s"Error with big number $number")
    }
    number
  }

  case class BigNumberException(message: String) extends Exception(message)


}
