package app.impl.scalaz

import org.junit.Test

import scalaz.std.either._
import scalaz.std.list._
import scalaz.syntax.traverse._

/**
  * Created by pabloperezgarcia on 10/11/2017.
  */
class SequenceFeature {

  @Test
  def eitherSequence() {
    val xs: List[Either[String, Int]] = List(Right(1), Right(2))
    val tmp0: Either[String, List[Int]] = xs.sequenceU
    println(xs)
    println(tmp0)
  }

//  @Test
//  def optionSequence() {
//    val xs: List[Option[Int]] = List(Some(1), Some(2))
//    val tmp0: Option[List[Int]] = xs.sequenceU
//    println(xs)
//    println(tmp0)
//  }

}
