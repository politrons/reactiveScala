package app.impl

import org.junit.Test


class Implicit {


  /**
    * Implicit parameter tell is used in case that there´s no parameters passed to the function,
    * the compiler will take the one implicit val defined.
    * if you define more than one implicit val the code wont compile.
    */
  @Test def implicitParameter(): Unit = {
    println(multiply)
    println(multiply(3, 3))
  }

  implicit val multiplier = 2


  implicit def multiply(implicit x: Int, y: Int) = {
    x * y
  }



}