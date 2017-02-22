package app.impl.scala

import org.junit.Test

/**
  * In scala, when we use generic type, it not necessary specify the type when a method is invoked.
  * You can do it if you want, but it´ redundant since the compiler detect the type in the context of the call
  */
class Generics {


  @Test def noTypeRequired2(): Unit = {
    println(genericType[Int](3))
    println(genericType("three"))
    println(genericType(1.7))

  }

  def genericType[T](x: T): String = {
    String.valueOf(x)
  }

  @Test def noTypeRequired(): Unit = {
    println(dup[Int](3, 4))
    println(dup("three", 3))
  }

  def dup[T](x: T, n: Int): List[T] = {
    if (n == 0)
      Nil
    else
      x :: dup(x, n - 1)
  }

  /**
    * When we talk about traits extension it´s a different story.
    * There you must specify the type, just like  Java does when you create a class that extends the trait.
    */
  @Test def typeRequired(): Unit = {
    val string = new StringType()
    val number = new IntType()
    println(string.returnStringValue("paul"))
    println(number.returnStringValue(1))
  }

  class StringType(name: String = "Paul") extends GenericType[String] {
    returnStringValue(name)
  }

  class IntType(value: Int = 1) extends GenericType[Int] {
    returnStringValue(value)
  }

  trait GenericType[T <: Any] {

    def returnStringValue(value: T): String = {
      String.valueOf(value)
    }

  }


}
