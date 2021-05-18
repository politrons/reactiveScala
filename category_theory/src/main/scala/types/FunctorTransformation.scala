package types

import types.FunctorTransformation.FunctorTrans

import scala.util.Try

object FunctorTransformation {

  /**
    * Using higher-kinded types we......
    */
  trait FunctorTrans[-A[_], +B[_]] {
    def apply[T](a: A[T]): B[T]
  }

  object FunctorListToOption extends FunctorTrans[List, Option] {

    override def apply[T](a: List[T]): Option[T] = a.headOption

  }

  object FunctorTryToOption extends FunctorTrans[Try, Option] {

    override def apply[T](a: Try[T]): Option[T] = a.toOption

  }

  def main(args: Array[String]): Unit = {
    println((List("hello", "category", "theory")))
    println(FunctorTryToOption(Try("hello category theory")))
  }

}
