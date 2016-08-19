package app.impl.scala

import org.junit.Test

/**
  * Created by pabloperezgarcia on 17/8/16.
  */
class Comprenhensions {


  @Test def comprenhension(): Unit = {
    println(even(0, 10))
    println(toList(0, 10))
  }

  /**
    * using yield we can accumulate the items from the for iterated,
    * and return a list with those items that pass the filter
    */
  def even(from: Int, to: Int): List[Int] = for (i <- List.range(from, to) if i % 2 == 0) yield i

  /**
    * Every item emitted in the for is added into a List using yield
    * @param from
    * @param to
    * @return
    */
  def toList(from: Int, to: Int): List[Int] = for (i <- List.range(from, to)) yield i
}