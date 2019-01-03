package app.impl.scala

import org.junit.Test
import rx.lang.scala.Observable

import scala.util.Try

/**
  * On Scala we define Functions defining the entry and output type as in Java give it functions types:
  *
  * Types:
  * Supplier () => Any
  * Function:  Any=>Any
  * Predicate: Any=>Boolean
  * Consumer:  Any=>Unit
  */
class Functions {

  case class First(value:String)

  case class Foo(value: String)

  case class Last(value: String)

  /**
    * Since we compose three functions and in [Referential Transparency] we start from [First] in thr
    * first function, and we end with [Bla] in the last function so the [composedFunc] has the type
    * First => Bla
    */
  @Test
  def compositionFunc(): Unit = {
    val func1: First => String = first => first.value.toUpperCase
    val func2: String => Foo = a => Foo(a)
    val func3: Foo => Last = foo => Last(foo.value)

    val composedFunc = func1 andThen func2 andThen func3
    println(composedFunc(First("Hello composition function world")))
  }

  @Test def passFunctionAsArgument(): Unit = {
    println(s"Casting number to String ${printString(toStringFunction, 100)}")
  }

  /**
    * Here we pass as first argument of the method a function, normally you specify a function setting entry value
    * and return if it has any.
    *
    * @param function
    * @param value
    * @return
    */
  def printString(function: Int => String, value: Int) = function.apply(value)


  def toStringFunction(v: Int) = String.valueOf(v)

  @Test def functionOverFunction(): Unit = {
    val sum = highOrderFunction(multiSumFunction)
    println(sum.apply())
  }

  /**
    * An order high fuction it´s function that recieve a function or return a function as result.
    * In this case we do both. We receive a Function3 and we return a consumer function
    */
  def highOrderFunction(f: (Int, Int, Int) => String): () => String = {
    () => f.apply(1, 2, 3)
  }

  def multiSumFunction: (Int, Int, Int) => String = {
    (a, b, c) => "Val:".concat(String.valueOf(a + b + c))
  }

  /**
    * using concat function we can trick to pass multiple arguments in several functions invocations.
    */
  @Test
  def concatFunctionsTest(): Unit = {
    val concat = concatFunction("hello")("functional", "world")("!!!!!")
    println(concat)
  }

  def concatFunction: String => (String, String) => String => String = {
    a => (b, c) => d => a.concat("-").concat(b.concat("-").concat(c)).concat(d)
  }

  @Test def functionsZip(): Unit = {
    Observable.zip(Observable.just(1), Observable.just(2), Observable.just(3))
      .map(values => multiSumFunction.apply(values._1, values._2, values._3))
      .subscribe(v => println(v))
  }

  @Test def functionsMultiZip(): Unit = {
    Observable.zip(Observable.just(10), Observable.just(2), Observable.just("VAL:"))
      .map(values => multiValFun.apply(values._1, values._2, values._3))
      .subscribe(v => println(v))
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

  @Test def functionError(): Unit = {
    val response = Try(errorThrow("it´s gonna break!"))
    println(s"Response ${response.failed}")
  }

  def errorThrow: (Any) => Int = {
    (a) => a.asInstanceOf[Int]
  }

  val function: (String, String) => (Int, Int) = {
    (i, i1) => (i.toInt, i1.toInt)
  }

  def multiLevelFunction: String => Int => Long => Double = {
    s => i => l => s.toDouble + i.toDouble + l.toDouble
  }


  @Test
  def threeLevelFunction(): Unit = {
    val value = multiLevelFunction("1")(2)(3L)
    println(value)
  }

  def crazyFunction1: (String, String) => (() => (() => (Double, Double))) = {
    (s, s1) => {
      val i = s.toInt
      val i1 = s1.toInt
      () => {
        val d = i.toLong
        val d1 = i1.toLong
        () => {
          (d.toDouble, d1.toDouble)
        }
      }
    }

  }

  @Test
  def mainCrazyFunction1(): Unit = {
    val tuple = crazyFunction1.apply("1", "2")()()
    println(tuple)
  }


}