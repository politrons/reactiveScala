package app.impl.scala

import org.junit.Test

class FoldFunction {


  val numbers: Seq[Double] = Seq(1.5, 2.0, 2.5)

  case class StringValue(msg: String)

  case class FoldClass(sv: StringValue) {

    def upperCase(sv: StringValue): FoldClass = {
      FoldClass(StringValue(sv.msg.toUpperCase))
    }

    def getMsg(): String = {
      sv.msg
    }
  }

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
    * In this test since we pass an option with some value, we pass to the second argument of the fold function
    * which it will use the value from the option.
    */
  @Test
  def foldWithSomeClass(): Unit = {
    val mainClass = FoldClass(StringValue("default value"))
    val someValue = Some(StringValue("override value"))
    val value = getOptionClassMsg(mainClass, someValue)
    println(value)
  }

  /**
    * In this test since we pass a None value, fold detect that and just use the default value, instead use
    * the function which it will use the value of the NONE, this use a mechanism similar to ternary operator
    */
  @Test
  def foldWithNoneClass(): Unit = {
    val mainClass = FoldClass(StringValue("default value"))
    val value = getOptionClassMsg(mainClass, None)
    println(value)
  }



  /**
    * If someValue it´s empty fold will return the Value, otherwise it will execute the second function
    * @param mainClass
    * @param someValue
    * @return
    */
  private def getOptionClassMsg(mainClass: FoldClass, someValue: Option[StringValue]) = {
    someValue.fold(mainClass)(mainClass.upperCase).getMsg()
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

  @Test def foldLeftAndRight(): Unit = {
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