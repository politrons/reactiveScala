package app.impl.shapeless

import org.junit.Test
import shapeless.HMap

/**
  * Using type class for the implicit and HMap shapeless allow us to generate Map in a generic way without contracts
  * of the types of key and value, as long as we add the implciit for the tuple of key - > value.
  *
  * Here using the same HMap defined we can add an Int and String Key for the same Map without have to use Any type
  * as you should use in Scala which is not Tyoe safe.
  */
class HMapFeature {

  class GenericMap[K, V]

  implicit val intString: GenericMap[Int, String] = new GenericMap[Int, String]
  implicit val stringInt: GenericMap[String, Int] = new GenericMap[String, Int]
//  implicit val intInt: GenericMap[Long, Int] = new GenericMap[Long, Int] // Uncomment if you want to add a new tuple type in the map

  @Test
  def main(): Unit = {
    val hm = HMap[GenericMap](36 -> "Paul", "Politrons" -> 13)
//    val hm2 = HMap[GenericMap](23 -> "foo", 23L -> 13) // Wont compile unless you uncomment the Long implicit
    println(hm)
    println(hm.get(36))

  }

}
