package app.impl

import org.junit.Test


class Algorithmic extends Generic[String, Long] {


  @Test def duplicate(): Unit = {
    val numbers = Array[Int](1, 2, 5, 4, 3, 4)
    val highestNumber = numbers.length - 1
    val total = numbers.toStream.sum
    val duplicate = total - (highestNumber * (highestNumber + 1) / 2)
    println(duplicate)
  }

  @Test def findNumbers(): Unit = {
    val text = "ab1cx2d3b"
    val numbers = text.toCharArray.toStream
      .filter(c => c.isDigit)
      .toList
    println(numbers)
  }

  @Test def getNumberOfScapes(): Unit = {
    val text = "  ab1 cx2d 3b "
    val spaces = text.toCharArray.toStream
      .filter(c => c.isSpaceChar)
      .toList
      .size
    println(spaces)
  }

  @Test def getNumberOfWords(): Unit = {
    val text = "  ab1 cx2d 3b "
    val words = text.split(" ").toStream
      .filter(s => !s.isEmpty)
      .toList
      .size
    println(words)
  }


}