package app.impl.scala

import org.junit.Test

class OptionFeature {

  @Test
  def main(): Unit ={
    val value=""
    Option(value) match{
      case Some(v) => println(v)
      case None => println("none")
    }
  }

  @Test
  def zipFeature(): Unit ={

    val opt1 = Option(null)
    val opt2 = Option("Hello world")

    (opt1 zip opt2).headOption match {
      case Some(t) => println(s"${t._1} ${t._2}")
      case None => println("None")
    }

    val opt3 = Option("Hello world")
    val opt4 = Option("Again")

    (opt3 zip opt4).headOption match {
      case Some(t) => println(s"${t._1} ${t._2}")
      case None => println("None")
    }

  }

}
