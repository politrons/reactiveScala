package app.impl.shapeless

import org.junit.Test
import shapeless.HNil


/**
  * Created by politrons.
  *
  * Shapeless is type class library extension of Scala that provide some great features to use Strong type or make
  * the scala compiler break in compilation time.
  *
  * One of the most famous feature of shapeless is Generic which applied to a class create a Factory which provide multiple
  * extra features.
  *
  * Using Genetic[Class].from(HList) it will create a new instance with the values present
  * Using Generic[Class].to(Instance class) it will return a HList with all attributes of the class in the list.
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
    * Using the Generic[Class].to(Instance class) we have a HList which as you could see in the example class
    * it can be used as a Scala collection and also to validate that some types are in the list.
    */
  @Test
  def validation(): Unit = {
    val hList = Generic[UserWithAge].to(UserWithAge("Paul", 36))
    hList.select[String]
//    hList.select[Long]//Type not pressent in class it Wont compile
    println(hList)
    println(hList.head)
    println(hList.tail)
  }

  /**
    * Using Generic of Shapeless is a powerful tool to create new case class instances from
    * others with same types.
    * Even if the classes are not alike of what they neant to do, if they share same types,
    * sometimes it could be useful share the data.
    *
    * Doing this we can avoid use copy form another class and have to specify attribute by attribute.
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
