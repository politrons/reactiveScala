package app.impl.patterns.creational

/**
  * Lazy operator ensure that the initialization of a variable it happens only
  * when the variable it´s consumed and not when it´s created
  */
object Lazy extends App {

  var x = {
    println("initialized x")
    10
  }

  lazy val y = {
    println("initialized y")
    x + 10
  }

  println("================")
  x = 20
  println(s"X $x and Y $y")

}
