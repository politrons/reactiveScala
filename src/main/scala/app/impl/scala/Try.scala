package app.impl.scala

import org.junit.Test

import scala.util.Try


/**
  * Try give you the feature to wrap a functionality into this operator, and this one will return success wrapping the result
  * or Failure wrapping the exception and description.
  */
class Try {


  @Test def trySuccess(): Unit = {
    val list = List("1", "2", "3", "4",  "5")

    val result = for {
      number <- Try(list.map(number => Integer.parseInt(number)))
    } yield number

    println(result)
  }

  @Test def tryFailure(): Unit = {
    val list = List("1", "2", "3", "4","ups",  "5")

    val result = for {
      number <- Try(list.map(number => Integer.parseInt(number)))
    } yield number

    println(result)
  }
}
