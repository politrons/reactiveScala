package com.politrons.monix

import monix.execution.Ack
import monix.execution.Scheduler.Implicits.global
import monix.reactive.Observable
import org.junit.Test

import scala.concurrent.Future
import scala.concurrent.duration._

class ObservableFeature {

  @Test
  def subscriber(): Unit = {
    Observable.now("Hi again Observable")
      .map(value => value.toUpperCase())
      .subscribe(response => {
        println(response)
        Future(Ack.Continue)
      }, t => println(t), () => println("We end the emission"))
  }

  @Test
  def nowVsDefer(): Unit = {
    var value = "Hi Now Again Rx!"
    Observable.now(value)
      .map(value => value.toUpperCase())
      .doOnNext(value => println(s"We check the value here during the emission $value"))
      .subscribe()

    value = "Hi Later Again Rx!"
    Observable.eval(value)
      .map(value => value.toUpperCase())
      .doOnNext(value => println(s"We check the value here during the emission $value"))
      .subscribe()
  }

  @Test
  def interval(): Unit = {
    Observable.interval(1 second)
      .doOnNext(value => println(s" $value"))
      .subscribe()
  }

  @Test
  def mergeOperator(): Unit = {
    Observable.merge(Observable.now("Hi"), Observable.now("again"), Observable.now("Observable"))
      .map(value => value.toUpperCase())
      .doOnNext(value => println(s"$value"))
      .subscribe()
  }

  @Test
  def zipOperator(): Unit = {
    Observable.zip3(Observable.now("Hi"), Observable.now("again"), Observable.now("Observable"))
      .map(value => {
        (value._1.toUpperCase(),
          value._2.toUpperCase(),
          value._3.toUpperCase())
      })
      .doOnNext(value => println(s"$value"))
      .subscribe()
  }

  @Test
  def fromIterableOperator(): Unit = {
    Observable.fromIterable(List(1,2,3,4))
      .map(value => value * 100)
      .doOnNext(value => println(s"$value"))
      .subscribe()
  }

  /**
    * Very interesting operator that we don't have in RxJava AFAIN, basically we cache the output of the observable
    * emitted and the second time that a subscriber subscribe we dont process the element in the pipeline, but
    * give the output elements from the first emission.
    */
  @Test
  def cacheOperator(): Unit = {
    val observer = Observable.fromIterable(List(1, 2, 3, 4))
      .map(value => value * 100)
      .doOnNext(value => println(s"processing $value"))
      .cache
    observer
      .subscribe(response => {
        Future(Ack.Continue)
      }, t => println(t), () => println("We end the emission"))
    observer
      .subscribe(response => {
        Future(Ack.Continue)
      }, t => println(t), () => println("We end the emission"))
  }

}
