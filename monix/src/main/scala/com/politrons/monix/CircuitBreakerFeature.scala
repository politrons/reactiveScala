package com.politrons.monix

import monix.eval.{Task, TaskCircuitBreaker}
import monix.execution.Scheduler.Implicits.global
import org.junit.Test

import scala.concurrent.duration._

/**
  * Circuit breaker of Monix is not inventing nothing new that we have not seen in other Circuit breakers
  * implementations.
  * Three states, open, half-open and close
  *
  * Close: Initial State all request are allowed to go.
  *
  * Open: State that it will be set once we reach the maxFailures number. Moment where we will start failing fast
  * until resetTimeout pass and we move to half-open state.
  *
  * Half-open: State in which the CB will allow us just to throw a request to the source and in case the result
  * is success, we will set the state as success, otherwise it will be set as open again.
  *
  */
class CircuitBreakerFeature {

  val breaker = TaskCircuitBreaker(
    maxFailures = 3,
    resetTimeout = 5.seconds
  )

  /**
    * In this example we have a 60% of probability to have a NullPointerException, and we configure
    * our CB to open if we have 3 errors during 5 seconds.
    */
  @Test
  def circuitBreaker(): Unit = {
    val callbackCB = breaker.doOnOpen(Task(println("Circuit breaker change state to open")))
      .doOnHalfOpen(Task(println("Circuit breaker change state to half-open")))
      .doOnClosed(Task(println("Circuit breaker change state to close")))


    val task = Task(getSentence(0.4))
      .map(value => {
        Thread.sleep(1000)
        println(s"Value processed:${value.toUpperCase()}")
      })
    0 to 1000 foreach { _ =>
      callbackCB.protect(task).runAsync
      println(s"State:${breaker.state}")
      Thread.sleep(1000)
    }
  }

  def getSentence(perc: Double): String = {
    if (math.random < perc) "Hi Task functional world" else null
  }


}
