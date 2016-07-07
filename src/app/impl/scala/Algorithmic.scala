package app.impl.scala

import app.impl.Generic
import org.junit.Test


class Algorithmic extends Generic[String, Long] {


  /**
    * Algorithm to find all duplicate numbers
    */
  @Test def duplicateNumbers(): Unit = {
    val numbers = Array[Int](1, 2, 5, 4, 3, 4)
    val highestNumber = numbers.length - 1
    val total = numbers.toStream.sum
    val duplicate = total - (highestNumber * (highestNumber + 1) / 2)
    println(duplicate)
  }

  /**
    * Algorithm to find the numbers in the string
    */
  @Test def findNumbers(): Unit = {
    val text = "ab1cx2d3b"
    val numbers = text.toCharArray.toStream
      .filter(c => c.isDigit)
      .toList
    println(numbers)
  }

  /**
    * Algorithm to find the number of spaces in String
    */
  @Test def findNumberOfSpaces(): Unit = {
    val text = "  ab1 cx2d 3b "
    val spaces = text.toCharArray.toStream
      .filter(c => c.isSpaceChar)
      .toList
      .size
    println(spaces)
  }

  /**
    * Algoerithm to find the number of words in String
    */
  @Test def findNumberOfWords(): Unit = {
    val text = "  ab1 cx2d 3b "
    val words = text.split(" ").toStream
      .filter(s => !s.isEmpty)
      .toList
      .size
    println(words)
  }

  /**
    * Algorithm to only get the unique value on the String and get rid of the duplicity
    */
  @Test def distinct(): Unit = {
    val text = "ab1bcxc2da3b"
    val list = text.toCharArray.toStream
      .map(c => List[Char](c))
      .scan(List[Char]())(distinctList).last
    println(list)
  }

  def distinctList(prevResult: List[Char], currentItem: List[Char]): List[Char] = {
    (prevResult ++ currentItem).distinct
  }

  /**
    * Sort algotihm to sort secuence of numbers increased.
    */
  @Test def bubbleSort(): Unit = {
    val numbers = Array[Int](3, 2, 1, 4, 5)
    for (i <- 0 until numbers.length - 1; j <- 0 until numbers.length - 1 - i) {
      if (numbers(j) > numbers(j + 1)) {
        val swap = numbers(j)
        numbers(j) = numbers(j + 1)
        numbers(j + 1) = swap
      }
    }
    println(numbers.toList)
  }

}