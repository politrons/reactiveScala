package app.impl

import app.NumberInterface
import org.junit.Test

import scala.collection.immutable.HashMap


/**
  */
class Utils extends Generic with NumberInterface {

  /**
    * Arithmetic operators item by item emitted.
    */
  @Test def arithmetic(): Unit = {
    val product = List(1, 2, 3, 4, 5).toStream
      .map(n => n * 1)
      .filter(n => n >= 1).product
    println(product)

    val sum = List(1, 2, 3, 4, 5).toStream
      .map(n => n * 1)
      .filter(n => n >= 1).sum
    println(sum)

    val sub = List(1, 2, 3, 4, 5).toStream
      .map(n => n * 1)
      .filter(n => n >= 1).reduce((a, b) => a - b)
    println(sub)

  }

  /**
    * How to create and iterate a Map collection
    */
  @Test def iterateMap(): Unit = {
    val map = HashMap[String, Int]("1" -> 1, "2" -> 2, "3" -> 3)

    val sumMap = map.toStream
      .map(entry => {
        println(map.get(entry._1).get)
        entry._2
      })
      .filter(n => isHigherThan1(n))
      .sum
    println(sumMap)
  }

  //  /**
  //    * How to create and iterate a Map collection
  //    */
  //  @Test def revertMap(): Unit = {
  //    val map = HashMap[String, Int]("1" -> 1, "2" -> 2, "3" -> 3)
  //    map.toStream
  //      .map(entry => Map[Int, String](entry._2 -> entry._1))
  //      .reduce((m,m1)=>)
  //  }

  /**
    * Iterate over a collection emit the items in string and return a new collection
    */
  @Test def intToStringList(): Unit = {
    val list = List(1, 2, 3).toStream
      .map(entry => String.valueOf(entry))
      .toList
    print(list)
  }

  /**
    * How to create and iterate a Map collection
    */
  @Test def appendList(): Unit = {
    val list = List(1, 2, 3)
    val list2 = List(4, 5, 6)
    val totalList = list ++ list2
    print(totalList)
  }

  override def isHigherThan1(num: Int): Boolean = {
    num > 1
  }
}



