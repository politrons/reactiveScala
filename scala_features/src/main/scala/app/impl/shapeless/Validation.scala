package app.impl.shapeless

import org.junit.Test
import shapeless.test.illTyped


class Validation {

  case class A()

  case class B()

  /**
    * illTyped allow us to do the opposite to check if an instance is from a type.
    * Here in case we provide the condition that an instance check with the type.
    * And it fail in compilation time if they match.
    */
  @Test
  def illTypedFeature(): Unit ={
    illTyped { """new A() : B"""} //Wrong type so its compile.
    //  illTyped { """new A() : A"""} //It´s the good type so it wont compile.
  }

}
