package com.politrons.monix

import monix.eval.Task
import monix.execution.Ack
import monix.execution.Scheduler.Implicits.global
import monix.reactive.Observable
import org.junit.Test

import scala.concurrent.Future
import scala.concurrent.duration._

/**
  * Introduce the Rx world using more FP operators from Monix.
  * It keeps same principles of Publisher-subscriber than RX
  * In this new approach one of the ways of doing Backpressure it would be making the ACK
  * in the onNext callback, just like Flow of Java9  or Akka stream does.
  */
class ObservableFeature {

  /**
    * Just like Rx Subscriber has onNext, onError and onComplete callbacks at the end of the pipeline.
    */
  @Test
  def subscriber(): Unit = {
    Observable.now("Hi again Observable")
      .map(value => value.toUpperCase())
      .subscribe(response => {
        println(response)
        Future(Ack.Continue)
      }, t => println(t), () => println("We end the emission"))
  }

  /**
    * Just as we saw before in Task, and in Rx you can create the observable with the value passed using [now]
    * or set that value once we have a subscriber with [eval]
    */
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

  /**
    * Handy operator as we have in Rx for schedulers. It will emmit a Long value every delay time passed
    * in the creation.Just like the operator in Rx is completely async and is running in another Thread.
    */
  @Test
  def interval(): Unit = {
    Observable.interval(1 second)
      .doOnNext(value => println(s" $value"))
      .subscribe()
    Thread.sleep(5000)
  }

  /**
    * Merge operator it helps to create a chain of elements to emmit one after the other in the pipeline, in
    * the exactly same order as you define.
    */
  @Test
  def mergeOperator(): Unit = {
    val ob1 = Observable.now("Hi").doOnNext(_ => println(Thread.currentThread().getName))
    val ob2 = Observable.fork(Observable("again")).doOnNext(_ => println(Thread.currentThread().getName))
    val ob3 = Observable.fork(Observable("Observable")).doOnNext(_ => println(Thread.currentThread().getName))
    Observable.merge(ob1, ob2, ob3)
      .map(value => value.toUpperCase())
      .doOnNext(value => println(s"$value"))
      .subscribe()
    Thread.sleep(1000)
  }

  /**
    * In the other hand, if we want to wait until all elements passed are processed and emmit just one element
    * then you can use [zip] operator
    */
  @Test
  def zipOperator(): Unit = {
    val ob1 = Observable.now("Hi").doOnNext(_ => println(Thread.currentThread().getName))
    val ob2 = Observable.fork(Observable("again")).doOnNext(_ => println(Thread.currentThread().getName))
    val ob3 = Observable.fork(Observable("Observable")).doOnNext(_ => println(Thread.currentThread().getName))
    Observable.zip3(ob1, ob2, ob3)
      .map(value => {
        (value._1.toUpperCase(),
          value._2.toUpperCase(),
          value._3.toUpperCase())
      })
      .doOnNext(value => println(s"$value"))
      .subscribe()
    Thread.sleep(1000)
  }

  /**
    * We can create an Observable from a list, and here we can see how the onNext is used to do back_pressure
    * in your application. Only of element can be emitted and until we do not make the ACK the next element is
    * not consumed from our publisher.
    */
  @Test
  def fromIterableOperator(): Unit = {
    Observable.fromIterable(List(1, 2, 3, 4, 5, 6, 7, 8, 9, 10))
      .map(value => value * 100)
      .doOnNext(value => println(s"$value"))
      .subscribe(response => {
        println(s"Some business logic here to process $response")
        Future(Ack.Continue)
      }, t => println(t), () => println("We end the emission"))
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
      .subscribe(_ => {
        Future(Ack.Continue)
      }, t => println(t), () => println("We end the emission"))
    observer
      .subscribe(_ => {
        Future(Ack.Continue)
      }, t => println(t), () => println("We end the emission"))
  }

}
