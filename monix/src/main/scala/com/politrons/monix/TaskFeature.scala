package com.politrons.monix

import monix.eval.Task
import monix.execution.Scheduler.Implicits.global
import org.junit.Test

import scala.concurrent.Await
import scala.util.{Failure, Success}
import scala.concurrent.duration._

/**
  * Task is another Monad solution as Observable, IO or the same Task from Scalaz.
  * The idea is simple. Separate consumer from producer. Here we can create some Producers called as [Task]
  * and that functions only it will be executed and consumed when someone use the specific operator
  */
class TaskFeature {

  //#####################
  //#     EXECUTORS     #
  //#####################

  /**
    * As we describe before Task is a Producer which has not start producing anything. He just describe what
    * will produce and what he will do with the data emitted in the pipeline.
    * Is only when one of the consumers start consuming using in this case the operator [runOnComplete] when we
    * start the execution of the Task and we receive the data.
    *
    * runOnComplete operator generate a cancelable instance, which is running in another thread, so in order to
    * wait for the complete resolution of this new thread we use a Sleep.
    */
  @Test
  def runOnCompleteOperator(): Unit = {
    Task("Hello Monix Task world!")
      .map(value => value.toUpperCase)
      .runOnComplete {
        case Success(value) =>
          println(value)
          value
        case Failure(ex) =>
          System.out.println(s"ERROR: ${ex.getMessage}")
          ex
      }
    Thread.sleep(1000)
  }

  /**
    * In case we want to execute the logic of the task in another thread, and later to join the result of the execution
    * of the task in the main thread we use the operator [runAsync] which it will return a cancelableFuture which is
    * an extension of Scala future.
    */
  @Test
  def runAsyncOperator(): Unit = {
    val cancelableFuture = Task("Hello Monix Task world!")
      .map(value => value.toUpperCase)
      .runAsync
    cancelableFuture.foreach(println)
    Await.result(cancelableFuture, 10 seconds)
  }

}
