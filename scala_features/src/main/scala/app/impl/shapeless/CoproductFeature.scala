package app.impl.shapeless

import org.junit.Test
import shapeless.{:+:, CNil, Coproduct, Poly1}


/**
  *
  */
class CoproductFeature {

  case class Name(value: String)

  case class Age(value: Int)

  case class Sex(value: String)

  type Person = Name :+: Age :+: Sex :+: CNil

  @Test
  def main(): Unit = {
    val person = Coproduct[Person](Name("Paul"))
    println(person.select[Name])
    println(person.select[Age])
    println(person.select[Sex])
  }

}
