package app.impl.scalaz

import org.junit.Test

import scalaz.Reader


/**
  * Created by pabloperezgarcia on 01/11/2017.
  *
  * Using Reader monad is a very cool way for DI, since you can always pass
  * as the parameter between functions the element that you pass as argument in the run(Value)
  * so if you have N functions that need that argument X(1981) you can just make that
  * all your functions return a Reader where left type is the DI value, and the right type is the
  * return type of the function
  */
class ReaderMonad {

  @Test
  def dependecyInjection(): Unit = {
    println(runAllFunctions(1).run("1981"))
  }

  def runAllFunctions(value: Int) = {
    for {
      sumValue <- sumValues(value)
      concatValue <- concatValues(sumValue)
      sentence <- wrapValues(concatValue)
    } yield sentence
  }

  def sumValues(value: Int) = Reader[String, Int] {
    diValue => value + diValue.toInt
  }

  def concatValues(value: Int) = Reader[String, String] {
    diValue => s"${value.toString} | ${diValue.toString}"
  }

  def wrapValues(value: String) = Reader[String, String] {
    diValue => s"[[$diValue | ${value.toString}]]"
  }

  @Test
  def main(): Unit = {
    val f: Int => String = _.toString
    val reader = Reader(f)
    println(reader.run(123))
  }

}
