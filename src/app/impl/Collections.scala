package app.impl

import app.NumberInterface
import org.junit.Test

import scala.collection.immutable.HashMap


/**
  */
class Collections extends Generic with NumberInterface {


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

//    /**
//      * How to create and iterate a Map collection
//      */
//    @Test def revertMap(): Unit = {
//      val map = HashMap[String, Int]("1" -> 1, "2" -> 2, "3" -> 3)
//      Observer.from(List(map.toStream))
//        .map(entry => Map[Int, String](entry._2 -> entry._1))
//        .reduce((m,m1)=>)
//    }

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
    * Iterate over a collection emit the items and return a new collection
    */
  @Test def intToNewIntList(): Unit = {
    val list = List(1, 2, 3).toStream
      .flatMap(entry => List(entry * 100))
      .toList
    print(list)
  }

  /**
    * How to create and iterate a Map collection
    */
  @Test def appendList(): Unit = {
    val list = List(1, 2, 3)
    val list2 = List(4, 5, 6)
    val totalList = mergeList(list, list2)
    print(totalList)
  }


  /**
    * Scan operator allow us in an iterator get the previous emitted item and the new one and do an operation.
    */
  @Test def scanIntegers(): Unit = {
    print(List(1, 2, 3).scan(0)(sumIntegerFunction)
    )
  }

  def sumIntegerFunction: (Int, Int) => Int = {
    (previousItem, newItem) => previousItem + newItem
  }

  /**
    * In this case we define as initial value an empty list, then in the function mergeList we merge the previous list
    * and the new one. Finally we just return the last item emitted.
    */
  @Test def scanList(): Unit = {
    val newList = List(1, 2, 3)
      .map(n => List(n * 100))
      .scan(List())(mergeList).last
    println(newList)
  }

  def mergeList(prevResult: List[Int], currentItem: List[Int]): List[Int] = {
    prevResult ++ currentItem
  }


  override def isHigherThan1(num: Int): Boolean = {
    num > 1
  }


}



