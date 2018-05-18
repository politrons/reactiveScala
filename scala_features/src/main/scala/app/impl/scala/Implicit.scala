package app.impl.scala

import org.junit.Test


class Implicit {

  /**
    * Implicit parameter tell is used in case that there´s no parameters passed to the function,
    * the compiler will take the one implicit val defined.
    * if you define more than one implicit val/def with the same type the code it wont compile.
    */
  @Test def implicitParameter(): Unit = {
    println(multiply)
    println(multiply(3, 3))
  }

  implicit val multiplier: Int = 2


  implicit def multiply(implicit x: Int, y: Int) = {
    x * y
  }


  /**
    * As requirement for implicit class we need to initialize the class with def/val both use same name space
    * And the once that you mark in some method that a class type it´s implicit, the compile will automatically
    * search for a initialized implicit class of that type
    */
  implicit def initImplicitClass = HundredClass(100)

  case class HundredClass(magicNumber: Int)

  @Test def implicitClassTet(): Unit = {
    println(numberPer100(2))
  }

  def numberPer100(number: Int)(implicit implicitClass: HundredClass): Int = {
    number * implicitClass.magicNumber
  }


  /**
    * Import class implicitUtils
    *
    * An implicit class is an instance class that Scala compiler automatically initialize and attach to the object type,
    * as part of the API of the class.
    *
    * In our example those are the behaviors of implicit
    *
    * increment become part of String API
    * exponential become part of Int API
    * multiply become part of Int API
    * decrement become part of Int API
    *
    * As is logical only 1 implicit class/type bound per class is allowed
    *
    * Shall print
    * IBM
    * 4
    * 3
    * 1
    * 6
    */
  @Test def implicitClass(): Unit = {
    println("HAL".increment)
    println(2.exponential)
    println(2.increment(1))
    println(2.decrement(1))
    println(2.multiply(3))
    println("TEST".equals("-Works"))
  }

  @Test def replaceAllTest(): Unit = {
    val test = "{services}/service/{versions}/version"
    val replace = test.replaceAllLiterally("{services}", "works")
    println(replace)
  }


  implicit class StringImprovements(val s: String) {

    //    override def concat(newValue: String): String = newValue.concat("-CustomConcat-").concat(s)

    def increment = s.map(c => (c + 1).toChar)

    // Use `equals`, not `==`
    override def equals(that: Any) = that match {
      case t: String => t.s.equalsIgnoreCase(this.s)
      case _ => false
    }

    override def toString() = s

  }

  implicit class IntegerImprovements(i: Int) {

    def exponential = i * i

    def increment(n: Int) = i + n

    def decrement(n: Int) = i - n

    def multiply(n: Int) = i * n

  }


  trait Operator[T] {
    def equals(a: T, b: T): Boolean
  }

  /**
    * Type class implementation power by implicit
    */
  implicit class typeClass[T](value: T) {

    implicit def ===(newValue: T)(implicit operator: Operator[T]): Boolean = {
      operator.equals(value, newValue)
    }

    implicit def !==(newValue: T)(implicit operator: Operator[T]): Boolean = {
      !operator.equals(value, newValue)
    }
  }

  /**
    * Since the trait only have one method Scala allow you to use sugar syntax and just use a lambda since
    * it can inference which method it will be executed.
    * Just need to pass the arguments specifying the type since are generic in the function.
    */
  implicit val intOperator: Operator[Int] = (a: Int, b: Int) => a == b

  implicit val stringOperator: Operator[String] = (a: String, b: String) => a.eq(b)

  @Test
  def equalsDifferentTypes(): Unit = {
    println(1 === 1)
    println("3" === "3")
    println("3" === "4")

    println("5" !== "5")
    println("5" !== "6")

  }


}