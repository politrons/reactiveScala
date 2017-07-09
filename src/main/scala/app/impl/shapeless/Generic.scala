package app.impl.shapeless

import org.junit.Test

/**
  * Created by pabloperezgarcia on 08/07/2017.
  * Shapeless is another extension of Scala that provide some great features to modify
  * the scala compiler to make your code break in compilation time if you like.
  */
class Generic {

  import shapeless.Generic

  case class UserWithAge(name: String, age: Int)

  @Test
  def generic(): Unit = {
    val gen = Generic[UserWithAge]
    val u = UserWithAge("Julien", 30)
    val h = gen.to(u) // returns Julien :: 30 :: HNil
    println(h)
    println(gen.from(h)) // return UserWithAge("Julien", 30)
  }




}
