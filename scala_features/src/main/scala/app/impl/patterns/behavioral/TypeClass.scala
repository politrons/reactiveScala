package app.impl.patterns.behavioral

import org.junit.Test


class TypeClass {

  implicit class EqualOps[T](any: T) {
    def ===(value: T)(implicit instance: EqualTypeClass[T]): Boolean = {
      instance.equal(value, any)
    }
  }

  trait EqualTypeClass[T] {
    def equal(a: T, b: T): Boolean
  }

  object EqualTypeClass {
    def apply[T](func: (T, T) => Boolean) = new EqualTypeClass[T] {
      def equal(a: T, b: T): Boolean = func.apply(a, b)
    }
  }

  implicit val intEqual: EqualTypeClass[Int] = new EqualTypeClass[Int]() {
    override def equal(a: Int, b: Int): Boolean = a == b
  }

  implicit val stringEqual: EqualTypeClass[String] = EqualTypeClass((a, b) => a.eq(b))

  implicit val longEqual: EqualTypeClass[Long] = EqualTypeClass((a, b) => a == b)

  @Test
  def testEquals() = {
    println(1 === 2)
    println("1" === "3")
    println("1" === "1")
    println(1l === 1l)
  }


}
