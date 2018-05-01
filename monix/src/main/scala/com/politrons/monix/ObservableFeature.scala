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

}
