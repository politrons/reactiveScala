package app.impl.scala

import scala.util.Try

/**
  * I have seen far by standing on the shoulders of giants.
  * This whole exercise was based on the RockJVM youtube post https://www.youtube.com/watch?v=Y5rPHZaUakg&t=487s
  *
  * This pattern apply the idea to concat functions like if we would doing recursive calls.
  * For example:
  * Find element after add 4 elements.  K => Boolean || K => Boolean || K => Boolean || K => Boolean || false
  * Get element after add 4 elements. K => V || K => V || K => V || K => V || IllegalAccessException
  */
object FunctionalMap extends App {

  /**
    * Contract to implement a Functional Map
    *
    * @tparam K of the map
    * @tparam V of the map
    */
  trait MapF[K, V] {

    def has(key: K): Boolean

    def add(key: K, value: V): MapF[K, V]

    def remove(key: K): MapF[K, V]

    def get(key: V): GenericMap[K, V]

  }

  /**
    * Factory companion object to create MapF
    *
    * [Add] function that receive key and value, and we create the first instance of [GenericMap]
    * passing two functions:
    * [Predicate] to be used by [has] function. Check if the key is part of the map
    * [Function1] to be used by [get] function. Get the value in the map for a key if exist, otherwise throw [IllegalAccessException] exception
    */
  object MapF {
    def add[K, V](key: K, value: V): GenericMap[K, V] = {
      GenericMap(k => k == key, k => if (k == key) value else throw new IllegalAccessException)
    }
  }

  /**
    * Implementation of [MapF] where we pass two functions in the constructor.
    * @param find predicate function to be used by [has] function. Check if the key is part of the map
    * @param get  function1 function to be used by [get] function. Get the value in the map for a key
    */
  case class GenericMap[K, V](find: K => Boolean, get: K => V) extends MapF[K, V] {

    /**
      * Using the [key] we invoke the [find] function to search the key in all the recursive predicate function
      *
      * @param key to be used by the function
      */
    override def has(key: K): Boolean = find(key)

    /**
      * Using key and value we create a new instance of [GenericMap] where we pass two functions, and part of this two functions, we use
      * the functions of the previous GenericMap[K, V] instance.
      * [findFunction] invoke the [find] function of the previous instance of [GenericMap] or apply the function e == key
      * [getFunction] check if the key is equal to k input param of the function, and if is equals return the value
      * otherwise invoke the recursive function [get(k)] of previous instance [GenericMap]
      */
    override def add(key: K, value: V): GenericMap[K, V] = {

      val findFunction: K => Boolean = {
        e => find(e) || e == key
      }

      val getFunction: K => V = {
        k => if (k == key) value else get(k)
      }

      GenericMap[K, V](findFunction, getFunction)

    }

    /**
      * Using key we create a new instance of [GenericMap] where we pass two functions, and part of this two functions, we use
      * the functions of the previous GenericMap[K, V] instance.
      * [findFunction] invoke the [find] function of the previous instance of [GenericMap] and apply the function e != key
      * [getFunction] check if the key is not equal to k input param of the function, and if is not equals invoke
      * the recursive function [get(k)] of previous instance [GenericMap] otherwise throw [IllegalAccessException]
      */
    override def remove(key: K): GenericMap[K, V] = {

      val findFunction: K => Boolean = {
        e => find(e) && e != key
      }

      val getFunction: K => V = {
        k => if (k != key) get(k) else throw new IllegalAccessException
      }

      GenericMap[K, V](findFunction, getFunction)

    }

    /**
      * invoke the function [get] passing the key and this function obtain the value for that key passing for
      * all [getFunction] concatenations that we did when we use [add] function.
      */
    override def get(key: V): GenericMap[K, V] = get(key)
  }

  /**
    * Add some entries
    */
  private val functionalMap: GenericMap[Int, String] = MapF add(1, "hello") add(2, "world") add(3, "functional") add(4, "programing") add(5, "rocks")

  println(s"Exist:${functionalMap.has(1)}")
  println(s"Value:${functionalMap.get(1)}")
  println(s"Exist:${functionalMap.has(3)}")
  println(s"Value:${functionalMap.get(3)}")
  println(s"Exist:${functionalMap.has(2)}")
  println(s"Value:${functionalMap.get(2)}")
  println(s"Exist:${functionalMap.has(100)}")
  println(s"Value:${Try(functionalMap.get(100))}")

  /**
    * Remove some elements
    */
  private val newFunctionalMap: GenericMap[Int, String] = functionalMap remove 1 remove 3

  println(s"Exist:${newFunctionalMap.has(3)}")
  println(s"Value:${Try(newFunctionalMap.get(3))}")
  println(s"Exist:${newFunctionalMap.has(1)}")
  println(s"Value:${Try(newFunctionalMap.get(1))}")
  println(s"Exist:${newFunctionalMap.has(2)}")
  println(s"Value:${Try(newFunctionalMap.get(2))}")
}
