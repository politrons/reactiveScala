package app.impl.shapeless

import org.junit.Test
import shapeless.syntax.std.tuple._

/**
  * Created by pabloperezgarcia on 08/07/2017.
  * Shapeless is another extension of Scala that provide some great features to modify
  * the scala compiler to make your code break in compilation time if you like.
  */
class Tuples {

  /**
    * Here we use an extension of tuple to allow us extra features over tuple
    *
    * - head or tail to get the values of the tuple from the bottom or the begging
    * - productElements return a HNil collection
    * - filter return all elements of type specified
    * - And all the rest of the scala functional API provide for collections
    */
  @Test
  def tuples(): Unit = {

    val tuple = (10, 100L, "hello", "world")
    println(tuple.head)
    println(tuple.tail)
    println(tuple.productElements)
    println(tuple.filter[Int])
  }




}
