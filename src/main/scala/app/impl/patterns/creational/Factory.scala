package app.impl.patterns.creational

import org.junit.Assert._
import org.junit.Test

/**
  * Using static object class as Factory and trait to return a common type class we can create
  * a Factory class.
  */
class Factory {

  trait Car {}

  case class AstonMartin() extends Car {}

  case class Renault() extends Car {}

  object CarFactory {

    def create(car: String): Car = {
      car match {
        case "AstonMartin" => AstonMartin()
        case "Renault" => Renault()
        case _ => throw new IllegalArgumentException("No car found")
      }
    }
  }

  @Test
  def testFactory(): Unit = {
    assertTrue(CarFactory.create("AstonMartin").isInstanceOf[AstonMartin])
    assertTrue(CarFactory.create("Renault").isInstanceOf[Renault])
  }

}
