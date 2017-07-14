package app.impl.scala

import org.junit.Test


class Comparator {


  /**
    * Algorithm to sort array of numbers
    */
  @Test def sortNumbers(): Unit = {
    val numbers = Array[Int](5, 3, 2, 6, 1, 4)
    println(numbers.sorted.toList)
  }

  /**
    * Algorithm to sort classes by attributes
    */
  @Test def sortObjects(): Unit = {
    val men:List[Man] = List(new Man("pablo", 35), new Man("Nene", 35), new Man("Didu", 38), new Man("Bulum", 37))
    println(men.sortBy(_.name).head.name)
    println(men.sortBy(_.age).head.age)
  }


  class Man(val name: String, val age: Int) {}

}