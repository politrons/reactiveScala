package com.politrons.monix

import monix.eval.Task
import monix.execution.Scheduler.Implicits.global
import org.junit.Test

import scala.concurrent.{Await, Future}
import scala.concurrent.duration._


class TaskCreation {


  /**
    * Task [now] operator it's like Observable.just or IO.now it will process the value passed into the monad
    * in the very same moment the monad is created.
    */
  @Test
  def nowOperator(): Unit = {
    var value = "process now"
    val task = Task.now(value)
      .map(value => value.toUpperCase)
    value = "process later"
    val cancelableFuture = task.runAsync
    val result = Await.result(cancelableFuture, 10 seconds)
    println(result)
  }

  /**
    * Task [eval] operator it's like Observable.defer or IO.point it will process the value passed into the monad
    * in the moment the monad is consumed.
    */
  @Test
  def evalOperator(): Unit = {
    var value = "process now"
    val task = Task.eval(value)
      .map(value => value.toUpperCase)
    value = "process later"
    val cancelableFuture = task.runAsync
    val result = Await.result(cancelableFuture, 10 seconds)
    println(result)
  }

  /**
    * With fromFuture operator we can transform a Scala future into Task obviously without any type of blocking.
    */
  @Test
  def fromFutureOperator(): Unit = {
    val cancelableFuture = Task.fromFuture(Future("Transform scala future to Task"))
      .map(value => value.toUpperCase)
      .runAsync
    val result = Await.result(cancelableFuture, 10 seconds)
    println(result)
  }


}
