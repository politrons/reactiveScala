package app.impl.shapeless

import org.junit.Test
import shapeless.HNil

/**
  * Created by politrons.
  *
  * Shapeless is another extension of Scala that provide some great features to modify
  * the scala compiler to make your code break in compilation time if you like.
  *
  * The most famous feature of shapeless is Generic which applied to a class create a list of Multiple Types
  *
  *
  */
class Generic {

  import shapeless.Generic

  case class UserWithAge(name: String, age: Int)

  /**
    * Using Genetic we can create a factory class from a case class to be created later on
    * form another generic or passing arguments.
    */
  @Test
  def generic(): Unit = {
    val genericUserWithAge = Generic[UserWithAge]
    val paul = genericUserWithAge.from("Paul" :: 36 :: HNil)
    println(paul)
  }

  /**
    * Using Generic of Shapeless is a powerful tool to create new case class instances from
    * others with same types.
    * Even if the classes are not alike of what they neant to do, if they share same types,
    * sometimes it could be useful share the data.
    */
  @Test def copyCaseClass() {
    case class Person(name: String, age: Int)
    case class Fruit(name: String, age: Int)

    val person = Person("Paul", 36)
    val fruit = Fruit("Mango", 10)

    val genericPerson: shapeless.::[String, shapeless.::[Int, HNil]] = Generic[Person].to(person)
    val genericFruit = Generic[Fruit].to(fruit)

    val fruitWithPersonNameAndAge: Fruit = Generic[Fruit].from(genericPerson)
    println(genericPerson)
    println(genericFruit)
    println(fruitWithPersonNameAndAge)
  }

}
