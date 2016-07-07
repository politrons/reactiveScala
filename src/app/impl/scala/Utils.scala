package app.impl.scala

import app.NumberInterface
import app.impl.Generic
import org.junit.Test


/**
  */
class Utils extends Generic with NumberInterface {

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


  override def isHigherThan1(num: Int): Boolean = {
    num > 1
  }
}



