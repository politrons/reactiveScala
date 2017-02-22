package app.impl.scala

import org.junit.Test

class Interpolation {


  @Test def basic(): Unit = {
    val word = "Scala"
    val word1 = "World"
    println(s"Hello $word $word1")
  }

  @Test def advance(): Unit = {
    val word = "Scala"
    val word1 = "World"
    println(s"HELLO ${new StringBuilder(word).append(" ").append(word1).toString() toUpperCase}")
  }

  @Test def toJson(): Unit = {
    new StringContext("{'id':'val'}")
  }

}