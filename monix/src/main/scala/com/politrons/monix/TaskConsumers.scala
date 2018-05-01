package com.politrons.monix

import java.util.concurrent.TimeUnit

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
class TaskConsumers {

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
    * A cancelable object it can be cancel at some point in time, if you want to cancel that process and clean the
    * resource you just need to use cancel operator.
    */
  @Test
  def cancelOnCompleteOperator(): Unit = {
    val cancelable = Task("Hello Monix Task world!")
      .map(value => value.toUpperCase)
      .delayResult(FiniteDuration.apply(1, TimeUnit.SECONDS))
      .runOnComplete {
        case Success(value) =>
          println(value)
          value
        case Failure(ex) =>
          System.out.println(s"ERROR: ${ex.getMessage}")
          ex
      }
    cancelable.cancel()
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
    val result = Await.result(cancelableFuture, 10 seconds)
    println(result)
  }

  /**
    * The runSyncMaybe is one of the most amazing operator I've ever seen. He calculate in runtime if a process
    * is already done and we don't need to wait for him, or otherwise create a CancelableFuture to be run async.
    * It return an Either[CancelableFuture[T], T]
    */
  @Test
  def runSyncMaybeOperator(): Unit = {
    val either = Task(getSentence(0.5))
      .map(value => value.toUpperCase)
      .runSyncMaybe
    either match {
      case Right(value) => println(value)
      case Left(future) => println(Await.result(future, 10 seconds))
    }
  }

  /**
    * The coeval is one of the most amazing operator I've ever seen. He calculate in runtime if a process
    * is already done and we dont need to wait for him, or otherwise create a CancelableFuture to be run async.
    * It return an Either[Throwable, Either[CancelableFuture[T], T]]
    */
  @Test
  def coevalOperator(): Unit = {
    val coeval = Task(getSentenceWithMaybeError(0.5))
      .map(value => value.toUpperCase)
      .coeval
    coeval.run match {
      case Right(either) => {
        either match {
          case Right(value) => println(s"Right value $value")
          case Left(future) => println(s"We need to run this in a future ${Await.result(future, 10 second)}")
        }
      }
      case Left(throwable) => println(s"An error just happen $throwable")
    }
  }

  def getSentenceWithMaybeError(perc: Double): String = {
    if (math.random < perc) {
      throw new NullPointerException()
    } else {
      if (math.random < perc) {
        Thread.sleep(1000)
        "This test should  be executed async"
      } else {
        "Plain sync process"
      }
    }
  }

  def getSentence(perc: Double): String = {
    if (math.random < perc) {
      Thread.sleep(1000)
      "This test should  be executed async"
    } else {
      "Plain sync process"
    }
  }

}
