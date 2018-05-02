package com.politrons.monix

import monix.eval.Task
import monix.execution.Scheduler.Implicits.global
import org.junit.Test

import scala.concurrent.duration._
import scala.concurrent.{Await, Future}


class TaskTransforming {

  /**
    * In case of error we retry the whole emission until the specific number we pass to the operator
    */
  @Test
  def onErrorRestartOperator(): Unit = {
    val cancelableFuture = Task(getSentence(0.05))
      .map(value => {
        println(s"Process value: $value")
        value.toUpperCase
      })
      .onErrorRestart(20)
      .runAsync
    val result = Await.result(cancelableFuture, 10 seconds)
    println(result)
  }

  /**
    * Operator onErrorRestartIf only it will retry in case the Throwable passed is the same type as you specify
    * in your predicate function.
    */
  @Test
  def onErrorRestartIfOperator(): Unit = {
    val cancelableFuture = Task(getSentence(0.05))
      .map(value => {
        println(s"Process value: $value")
        value.toUpperCase()
      })
      .onErrorRestartIf(t => t.isInstanceOf[NullPointerException])
      .runAsync
    val result = Await.result(cancelableFuture, 10 seconds)
    println(result)
  }

  def getSentence(perc: Double): String = {
    if (math.random < perc) "Hi Task functional world" else null
  }

}
