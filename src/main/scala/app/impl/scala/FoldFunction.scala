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
}