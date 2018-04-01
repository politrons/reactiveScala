package app.impl.shapeless

import org.junit.Test
import shapeless.syntax.std.tuple._

/**
  * Created by politrons.
  *
  * Here we use an extension of tuple to allow us extra features over tuple.
  * In order to use it, you just need to add the implicit adding this import:
  *
  * [[ shapeless.syntax.std.tuple._]]
  */
class TuplesExtension {

  val tuple: (Int, Long, String, String) = (10, 100L, "hello", "world")

  /**
    * head or tail to get the values of the tuple from the bottom or the begging
    * and all the rest of the scala functional API provide for collections
    */
  @Test
  def listFeature(): Unit = {
    println(tuple.head)
    println(tuple.tail)
  }

  /**
    * productElements return a HNil collection which you can use all validation and other features of it.
    */
  @Test
  def hListFeature(): Unit = {
    val hList = tuple.productElements
    hList.select[String]
    // hList.select[Double]//Double is not in the list it Wont compile
    println(hList)
  }

  /**
    * filter operator allow you to filter the elements that you want to extract from your hList
    */
  @Test
  def internalFeatures(): Unit = {
    val tuple = ("hello", 10, 100L, "shapeless", "world")
    println(tuple.filter[String])
    println(tuple.filterNot[String])

  }


}
