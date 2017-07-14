package app.impl.scalaz

import org.junit.Test

import scalaz.Functor
import scalaz.Scalaz._

/**
  * Created by pabloperezgarcia on 12/03/2017.
  *
  * Some of good features of ScalaZ
  */
class Features {

  @Test
  def ternaryOperator(): Unit = {
    val boolV = 10 < 5
    println(boolV ? "A" | "B")
  }

  @Test
  def validation(): Unit = {
    val validation = "6".parseInt
    println(validation.isSuccess)
    println(validation.isFailure)
    println(validation.toOption.get)
  }

  @Test
  def allParis(): Unit = {
    val pairList = List(1, 2, 3, 4).allPairs
    println(pairList)
  }

  @Test
  def some(): Unit = {
    val some = "Some value".some
    println(some.get)
  }

  @Test
  def typeClass(): Unit = {
    trait Ord[T] {
      def compare(a: T, b: T): Boolean
    }
    implicit object intOrd extends Ord[Int] {
      def compare(a: Int, b: Int): Boolean = a <= b
    }
    def areEquals[T](v1: T, v2: T)(implicit ord: Ord[T]): Boolean = {
      ord.compare(v1, v2)
    }

    println(areEquals[Int](2, 2))
  }

  /**
    * A Functor is something that can be mapped.
    * we use this mapper to transform from a value received [A]
    * into another [B] using this functor transformer function
    *
    */
  @Test
  def functor(): Unit = {
    def addHundred[F[_]](toAdd: F[Int])(implicit mapper: Functor[F]): F[Int] = {
      mapper.map(toAdd)(value => value + 100)
    }

    println(addHundred(10.some).get)

  }

  @Test
  def monoid(): Unit = {
    //Concat
    val letters = "A" |+| "B"
    println(letters)
    //Sun
    val numbers = 1 |+| 2 |+| 3
    println(numbers)
    //Merge maps
    val m1 = Map(1 -> List("A", "B", "C"), 2 -> List("AA", "BB"))
    val m2 = Map(1 -> List("Z"), 2 -> List("CC"), 3 -> List("YYY"))
    val mergedMap = m1 |+| m2
    println(mergedMap)
  }

}
