package app.impl.shapeless

import org.junit.Test
import shapeless.{:+:, CNil, Coproduct}


/**
  * Coproduct unlike product is a type where can have multiple types.
  * So for instance here a Fruit type can have subtypes Apple, banana, mango and so on.
  * Using this approach you can avoid heritage between your classes to share same Type.
  */
class CoproductFeature {

  case class Apple()

  case class Mango()

  case class Banana()

  type Fruit = Apple :+: Mango :+: Banana :+: CNil

  @Test
  def main(): Unit = {
    val fruit = Coproduct[Fruit](Apple())
    println(fruit.select[Apple])
    println(fruit.select[Mango])
    val banana = Coproduct[Fruit](Banana())
    println(banana.select[Banana])
  }

}
