package app.impl.scala

import app.impl.Generic
import org.junit.Test


/**
  * Using compound types you can create a class that extend another class,
  * and also add an extra class extension using "with"
  * This combination will provide you a multi hierarchy in your class
  *
  * Biggest differences between abstract class and traits are that traits allow  you have multi hierarchy
  */
class CompoundTypes extends Generic {


  @Test def cloneAndResetClassTest(): Unit = {
    val firstClass = new MyCloneableClass("Hello Scala world")
    println("First class:" + firstClass.hashCode())
    val newClass = cloneAndReset(firstClass)
    println("Second class:" + newClass.hashCode())
    println(newClass.asInstanceOf[MyCloneableClass].printArg())
  }

  /**
    * This method expect to receive a class that extends Cloneable and also Reseteable
    * @param obj
    * @return
    */
  def cloneAndReset(obj: Cloneable with Resetable): Cloneable = {
    val cloned = obj.clone()
    obj.reset
    cloned
  }

  class MyCloneableClass(arg: String) extends Cloneable with Resetable {
    def printArg() = println(arg)

    override def reset: Unit = {
      println("class reset")
    }
  }

  trait Cloneable extends java.lang.Cloneable {
    override def clone(): Cloneable = {
      super.clone().asInstanceOf[Cloneable]
    }
  }

  trait Resetable {
    def reset: Unit
  }

}


