package app.impl.scala

import org.junit.Test

class FoldFunction {


  val numbers: Seq[Double] = Seq(1.5, 2.0, 2.5)

  /**
    * As second argument we pass a function which define what to do with the previous and next item emitted
    */
  @Test def sumNumbers(): Unit = {
    val sum = numbers.fold(0.0)(_ + _)
    println(s"Sum = $sum")
  }
  
  /**
    * Here in the second argument we define a function where we concat the previous String emitted and the new one.
    */
  @Test def concatWords() = {
    val words: Seq[String] = Seq("Hello", "Fond", "Function")
    println(s"All words = ${words.fold("")((previousString, newString) => previousString + newString + " Donut ")}")
  }

  /**
    * Here we check how to use Option with fold to use similar behave as ternary operator
    */
  @Test
  def option(): Unit = {
    println(processValue(Some("test")))
    println(processValue(None))
  }

  /**
    * Here the fold function will use the defaultValue in case the option passed to the function it´s None
    *
    * @param test
    * @return
    */
  private def processValue(test: Option[Any]): String = {
    val value = test.fold("defaultValue")(s => s + " new value")
    value
  }

    val keyValue = Map("SERVICE" -> "test", "VERSION" -> "works")
    var formatPath = "/this/is/a/SERVICE/and/VERSION"

  @Test def foldLeftAndRight(): Unit ={
    val leftValueFirst = "A".foldLeft("B") {
      case (x, y) => x + y
    }
    println(leftValueFirst)
    val rightValueFirst = "A".foldRight("B") {
      case (x, y) => x + y
    }
    println(rightValueFirst)
  }

  /**
    * Using foldLeft we just specify that we want to pass in the function the left argument first.
    */
  @Test
  def foldLeftReplace() = {
    keyValue.foldLeft(formatPath) {
      case (path, (key, value)) =>
        formatPath = path.replaceAll(key, value)
        formatPath
    }
    println(formatPath)
  }

  /**
    * Using foldRight we do the other way around of the previous test.
    */
  @Test
  def foldRightReplace() = {
    keyValue.foldRight(formatPath) {
      case ((key, value), path) =>
        formatPath = path.replaceAll(key, value)
        formatPath
    }
    println(formatPath)
  }
}