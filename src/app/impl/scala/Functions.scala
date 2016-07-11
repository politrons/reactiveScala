package app.impl.scala

import org.junit.Test
import rx.lang.scala.Observable

class Functions {

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

}