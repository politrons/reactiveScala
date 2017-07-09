package app.impl.shapeless

import org.junit.Test
import shapeless.syntax.std.tuple._
import shapeless.{::, HNil}

/**
  * Created by pabloperezgarcia on 08/07/2017.
  * Shapeless is another extension of Scala that provide some great features to modify
  * the scala compiler to make your code break in compilation time if you like.
  */
class Main {


  case class User(name: String)

  /**
    * HList is a type of collection which allow you to pick up elements from the collection by type
    * Also if you try to pick up an element type that does not exist the code wont compile.
    */
  @Test
  def hList(): Unit = {
    val multiList = 42 :: "Hello" :: User("Julien") :: User("Paul") :: HNil

    // select finds the first element of a given type in a HList
    // Note that scalac will correctly infer the type of s to be String.
    println(Console.MAGENTA + multiList.select[String]) // returns "Hello".

    println(Console.GREEN + multiList.select[User]) // returns User.
    //multiList.select[List[Int]] // Compilation error. demo does not contain a List[Int]

    // Again i is correctly inferred as Int
    println(Console.BLUE + multiList.head) // returns 42.

    // You can also us pattern matching on HList
    val i :: s :: u :: u1 :: HNil = multiList
    println(Console.CYAN + i)
    println(Console.YELLOW + s)
    println(u)
    println(u1)
  }

  @Test
  def x(): Unit = {
    var classes = List[Class[_]]()

    val sentence = "If this works OMG"

    sentence.split(" ").foreach(word => {
      classes = classes ++ List(Class.forName(word))
    })
    println(classes)

  }


}
