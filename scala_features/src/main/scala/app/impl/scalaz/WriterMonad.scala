package app.impl.scalaz

import org.junit.Test

/**
  * Created by pabloperezgarcia on 09/11/2017.
  */
class WriterMonad {

  case class Writer[A, D](value: A, diary: D)(implicit m: MonadId[D]) {

    def flatMap[B](f: A => Writer[B, D]) = f(value) match {
      case Writer(result, d) => Writer(result, m.append(diary, d))
    }

    def map[B](f: A => B) = Writer[B, D](f(value), diary)
  }

  trait MonadId[T] {
    def zero: T

    def append(a: T, b: T): T
  }

  object MonadId {
    implicit val StringMonoid = new MonadId[String] {
      override def zero = ""

      override def append(s1: String, s2: String) = s1 + s2
    }

    implicit val IntMonoid = new MonadId[Int] {
      override def zero = 0

      override def append(s1: Int, s2: Int) = s1 * s2
    }

    implicit val FooMonoid = new MonadId[Foo] {
      override def zero = Foo("")

      override def append(s1: Foo, s2: Foo) = Foo(s1.value.concat(s2.value))
    }
  }

  @Test
  def stringWriter(): Unit = {
    val result = Writer(5, "")
      .flatMap(num => Writer(num + 3, "added three"))
    println(result.value)
    println(result.diary)
  }

  @Test
  def intWriter(): Unit = {
    val result = Writer(5, 5)
      .flatMap(num => Writer(num + 5, 10))
    println(result.value)
    println(result.diary)
  }

  @Test
  def fooWriter(): Unit = {
    val result = Writer(Foo("hello"), Foo(" writer"))
      .flatMap(foo => Writer(foo, Foo(" world")))
    println(result.value)
    println(result.diary)
  }

  case class Foo(value: String)

}
