package app.impl.patterns.behavioral

import org.junit.Test


class TypeClass {

  implicit class EqualOps[T](any: T) {
    def ===(value: T)(implicit instance: EqualTypeClass[T]): Boolean = {
      instance.equal(value, any)
    }
  }

  /**
    * Contract to be implemented by type classes
    * @tparam T generic type to be implemented for each different type
    */
  trait EqualTypeClass[T] {
    def equal(a: T, b: T): Boolean
  }

  /**
    * Implementation of interface
    */
  implicit val intEqual: EqualTypeClass[Int] = new EqualTypeClass[Int]() {
    override def equal(a: Int, b: Int): Boolean = a == b
  }

  /**
    * Sugar syntax
    */
  implicit val stringEqual: EqualTypeClass[String] = (a: String, b: String) => a.eq(b)


  implicit val longEqual: EqualTypeClass[Long] = (a: Long, b: Long) => a == b

  @Test
  def testEquals() = {
    println(1 === 2)
    println("1" === "3")
    println("1" === "1")
    println(1l === 1l)
  }


}
