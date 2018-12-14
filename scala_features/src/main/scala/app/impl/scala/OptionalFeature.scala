package app.impl.scala

import org.junit.Test

class OptionalFeature {

  @Test
  def main(): Unit ={
    val value=""
    Option(value) match{
      case Some(v) => println(v)
      case None => println("none")
    }
  }

}
