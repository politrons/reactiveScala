package app.impl.scala

import org.junit.Test
import rx.lang.scala.Observable

/**
  * On Scala we define Functions defining the entry and output type
  *
  * Types:
  *
  *    Function:  Any=>Any
  *    Predicate: Any=>Boolean
  *    Consumer:  Any=>Unit
  */
class Functions {

  @Test def passFunctionAsArgument(): Unit = {
    println(s"Casting number to String ${printString(toStringFunction, 100)}")
  }

  /**
    * Here we pass as first argument of the method a function, normally you specify a function setting entry value
    * and return if it has any.
    * @param function
    * @param value
    * @return
    */
  def printString(function: Int => String, value: Int) = function.apply(value)


  def toStringFunction(v: Int) = String.valueOf(v)


  @Test def functionsZip(): Unit = {
    Observable.zip(Observable.just(1), Observable.just(2), Observable.just(3))
      .map(s => multiSum.apply(s._1, s._2, s._3))
      .subscribe(v => println(v))
  }

  @Test def functionsMultiZip(): Unit = {
    Observable.zip(Observable.just(10), Observable.just(2), Observable.just("VAL:"))
      .map(s => multiValFun.apply(s._1, s._2, s._3))
      .subscribe(v => println(v))
  }

  def multiSum: (Int, Int, Int) => String = {
    (a, b, c) => "Val:".concat(String.valueOf(a + b + c))
  }

  def multiValFun: (Int, Long, String) => String = {
    (x, y, z) => z.concat(String.valueOf(x + y))
  }

  @Test def predicateConsumerFunction(): Unit = {
    Observable.just(10)
      .filter(v => predicateFunction.apply(v, 5))
      .doOnNext(v => consumerFunction.apply(v))
      .subscribe()
  }

  def predicateFunction: (Int, Int) => Boolean = {
    (a, b) => a > b
  }

  def consumerFunction: (Int) => Unit = {
    v => println(v + " is higher")
  }

  @Test def functionOverFunction(): Unit = {
    val sum = passFunction(multiSum)
    println(sum);
  }

  def passFunction(f: (Int, Int, Int) => String): String = {
    f.apply(1, 2, 3)
  }

}