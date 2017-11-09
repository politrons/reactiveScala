package app.impl.scalaz

import org.junit.Test

/**
  * Created by pabloperezgarcia on 09/11/2017..
  *
  * With Scalaz writer patter basically we can create our own builder patter.
  *
  * Applying the flatMap function we receive a type class which we append the old value with the new value, giving us
  * a last type class with all appended values.
  *
  * We create our monoids which using also type class pattern we can give the implementation depending the type class we use.
  */
class WriterMonad {

  case class Writer[A, D](value: A, appendValue: D)(implicit m: Monoid[D]) {

    def flatMap[B](f: A => Writer[B, D]) = f(value) match {
      case Writer(result, newAppendValue) =>
        Writer(result, m.append(appendValue, newAppendValue))
    }

    def map[B](f: A => B) = Writer[B, D](f(value), appendValue)
  }

  trait Monoid[T] {
    def zero: T

    def append(a: T, b: T): T
  }

  object Monoid {
    implicit val StringMonoid = new Monoid[String] {
      override def zero = ""

      override def append(s1: String, s2: String) = s1 + s2
    }

    implicit val IntMonoid = new Monoid[Int] {
      override def zero = 0

      override def append(s1: Int, s2: Int) = s1 + s2
    }

    implicit val FooMonoid = new Monoid[Foo] {
      override def zero = Foo("")

      override def append(s1: Foo, s2: Foo) = Foo(s1.value + s2.value)
    }
  }

  @Test
  def stringWriter(): Unit = {
    val result = Writer(0, "")
      .flatMap(num => Writer(num + 1, " page 1"))
      .flatMap(num => Writer(num + 1, " page 2"))
      .flatMap(num => Writer(num + 1, " page 3"))
    println(s"Total number of pages:${result.value}")
    println(result.appendValue)
  }

  @Test
  def intWriter(): Unit = {
    val result = Writer(1, 1)
      .flatMap(num => Writer(num + 1, 10))
      .flatMap(num => Writer(num + 1, 20))
      .flatMap(num => Writer(num + 1, 30))
      .flatMap(num => Writer(num + 1, 40))
    println(result.value)
    println(result.appendValue)
  }

  @Test
  def fooWriter(): Unit = {
    val result = Writer(Foo(""), Foo(""))
      .flatMap(foo => Writer(foo, Foo(" hello")))
      .flatMap(foo => Writer(foo, Foo(" writer")))
      .flatMap(foo => Writer(foo, Foo(" monad")))
      .flatMap(foo => Writer(foo, Foo(" world")))
    println(result.value)
    println(result.appendValue)
  }

  case class Foo(value: String)

}
