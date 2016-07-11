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
    * Format style for foreach loop + plus use of interpolation
    */
  @Test def foreach(): Unit = {
    List(1, 2, 3, 4, 5) foreach { element =>
      println(element)
    }

    Map(1 -> "one", 2 -> "two", 3 -> "three") foreach { entry =>
      println(s"key:${entry._1} --> val:${entry._2}")
    }
  }

  @Test def foreach2(): Unit = {
    val x = 0
    val list = List(1,2,3,4,5)
    (x to list.size-1) foreach (e => {
      println(list.apply(e))
    })
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


}



