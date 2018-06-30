package app.impl.scala

import org.apache.commons.lang.StringUtils
import org.junit.Test

import scala.util.Try

/**
  * Partial function define tow arguments type, the input and output
  * By default you can implement apply function and isDefinedAt, and once you invoke this function
  * both function will be executed
  */
class PartialFunctions {

  @Test
  def partialFunctionCompose() {
    val pfFunction1 = pf1.orElse(pf2).orElse(pfDefault)
    println(pfFunction1("3"))
    println(pfFunction1("4"))
    println(pfFunction1("foo"))
  }

  val pf1: PartialFunction[String /*Entry type*/ , String /*Output type*/ ] = {
    case "3" => "Got 3"
  }

  val pf2: PartialFunction[String /*Entry type*/ , String /*Output type*/ ] = {
    case "4" => "Got 4"
  }

  val pfDefault: PartialFunction[String /*Entry type*/ , String /*Output type*/ ] = {
    case _ =>"Nothing founded"
  }

  /**
    * Also with partial function you can use pattern matching to execute one function or another
    * depending the input
    */
  @Test def partialFunctionString(): Unit = {
    val pf: PartialFunction[Int /*Entry type*/ , String /*Output type*/ ] = {
      case input if input > 100 => "higher"
    }
    val myPartialFunction: (Int => String) = pf orElse { case _ => "lower" }
    println(myPartialFunction(101))
    println(myPartialFunction(99) )
  }

  @Test
  def partialFunction() {
    assert(Try(fraction(1)).isSuccess)
    assert(Try(fraction(0)).isFailure)
  }

  val fraction = new PartialFunction[Int /*Entry type*/ , Int /*Output type*/ ] {
    def apply(d: Int) = 42 / d

    def isDefinedAt(d: Int) = d != 0
  }

  @Test def upperCase(): Unit = {
    println(upperCaseIfString.isDefinedAt("hello scala world"))
    val triedUpperCase = Try(upperCaseIfString("hello scala world"))
    assert(triedUpperCase.isSuccess)
    println(triedUpperCase.get)
    val orElseFunction: (String => String) = upperCaseIfString orElse makeNumberAlphanumeric
    assert(Try(orElseFunction("0")).isSuccess)
    println(Try(orElseFunction("0")).get)
  }

  val upperCaseIfString: PartialFunction[String, String] = {
    case input if !StringUtils.isNumeric(input) => input.asInstanceOf[String].toUpperCase()
  }

  val makeNumberAlphanumeric: PartialFunction[String, String] = {
    case input if input == "0" => "zero"
    case input if input == "1" => "one"
    case input if input == "2" => "two"
    case input if input == "3" => "three"
  }

}
