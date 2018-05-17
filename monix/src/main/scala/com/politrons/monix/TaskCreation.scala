package com.politrons.monix

import monix.eval.{MVar, Task}
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

  /**
    * Create a new Task from a previous one and make it run in another thread
    */
  @Test
  def forkOperator(): Unit = {
    val cancelableFuture = Task.fork(Task("Run task in another thread"))
      .map(value => value.toUpperCase)
      .doOnFinish(_ => {
        Task(println(Thread.currentThread().getName))
      })
      .runAsync
    val result = Await.result(cancelableFuture, 10 seconds)
    println(result)
  }

  /**
    * We can use the famous Zip operator to pass a list of Task of type T, and they it will form a List[T]
    */
  @Test
  def zipListOperator(): Unit = {
    val cancelableFuture =
      Task.zipList(Task.fromFuture(Future("Task 1")),
        Task.now("Task 2"),
        Task("Task 3"),
        Task("Task 4"))
        .map(list => list
          .map(value => value.toUpperCase))
        .runAsync
    val result = Await.result(cancelableFuture, 10 seconds)
    println(result)
  }

  /**
    * If instead of use ZipList we rather pass a specify number of Task, Task monad has up to 5 task that you can pass in
    * the operators zipN, and for every task passed it will output a scala type with the n(T)
    */
  @Test
  def zip4Operator(): Unit = {
    val cancelableFuture =
      Task.zip4(Task.fromFuture(Future("Task 1")),
        Task.now("Task 2"),
        Task("Task 3"),
        Task("Task 4"))
        .map(fourple => {
          (fourple._1.toUpperCase,
            fourple._2.toUpperCase,
            fourple._3.toUpperCase,
            fourple._4.toUpperCase)
        })
        .runAsync
    val result = Await.result(cancelableFuture, 10 seconds)
    println(result)
  }

  @Test
  def zipListOperatorRunOnComplete(): Unit = {
    val cancelableFuture =
      Task.zipList(Task.fromFuture(Future("Task 1")),
        Task.now("Task 2"),
        Task("Task 3"),
        Task("Task 4"))

    cancelableFuture.runAsync

    cancelableFuture
      .runOnComplete(tryList => tryList.get
        .map(value => println(value.toUpperCase)))

    Thread.sleep(1000)
  }

}
