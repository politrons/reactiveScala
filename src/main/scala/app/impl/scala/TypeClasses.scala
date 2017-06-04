package app.impl.scala

import org.junit.Test

import scala.annotation.implicitNotFound

/**
  * Created by pabloperezgarcia on 12/06/2017.
  *
  * Type classes patter allow us to create a member TypeClass with implementation of type T without force T
  * to implement TypeClass.
  * Using implicits then depending the argument type passes in the invocation we will be redirected to the implicit
  * type implementation
  **/
class TypeClasses {

  @Test
  def main(): Unit = {
    println(getClassName(1))
    println(getClassName("test"))
    //To show the implicitNotFound message with compiler uncomment next line
    //println(getClassName(1l))
  }

  @implicitNotFound("Hey Paul! No member of type class TypeClass in scope for ${T}")
  trait TypeClass[T] {
    def className(clazz: T): String
  }

  object TypeClass {
    def apply[T](func: T => String) = new TypeClass[T] {
      def className(value: T): String = func(value)
    }
  }

  implicit val intName: TypeClass[Int] = TypeClass(clazz => clazz.getClass.getName)

  implicit val stringName: TypeClass[String] = TypeClass(clazz => clazz.getClass.getName)


  def getClassName[T](className: T)(implicit typeClass: TypeClass[T]): String = {
    typeClass.className(className)
  }


}
