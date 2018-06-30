package app.impl.scala

import app.impl.scala.PhantomTypes.{CircuitBreaker, Close, HalfOpen}
import org.junit.Test

/**
  * Phantom type pattern it can be very handy if we want to implement a State machine.
  * We can implement one if we create this case class [CircuitBreaker[State] with a State type
  * declared that it seems not being used.
  * But then we create the state classes which extends State class.
  * After that we create extensions methods using implicits for every specific type of the State.
  * And now voila, our CircuitBreaker class with a specific type only can access to the extended methods
  * defined in the implicit class
  */
object PhantomTypes {

  sealed trait State

  case class Open() extends State

  case class HalfOpen() extends State

  case class Close() extends State

  /**
    * Main class to keep the state of the Circuit breaker
    */
  case class CircuitBreaker[State]()

  /**
    * All this implicit classes are defined for a particular type. So once we have our instance of
    * CircuitBreaker it will only possible access to those extended methods associated with their
    * particular type.
    */

  /**
    * Close function only is accessible if the instance of CircuitBreaker for a State type Open
    */
  implicit class circuitBreakerOpen(cb: CircuitBreaker[Open]) {

    def close: CircuitBreaker[Close] = {
      println("Circuit breaker open to close")
      CircuitBreaker[Close]()
    }
  }

  /**
    * Open function only is accessible if the instance of CircuitBreaker for a State type Close
    */
  implicit class circuitBreakerClose(cb: CircuitBreaker[Close]) {

    def open: CircuitBreaker[Open] = {
      println("Circuit breaker close to open")
      CircuitBreaker[Open]()
    }
  }

  /**
    * Open and close function only is accessible if the instance of CircuitBreaker for a State type HalfOpen
    */
  implicit class circuitBreakerHalfOpen(cb: CircuitBreaker[HalfOpen]) {

    def open: CircuitBreaker[Open] = {
      println("Circuit breaker halfOpen to  open")
      CircuitBreaker[Open]()
    }

    def close: CircuitBreaker[Close] = {
      println("Circuit breaker halfOpen to close")
      CircuitBreaker[Close]()
    }
  }

}

class PhantomTypes {

  @Test
  def circuitBreakerMain(): Unit = {
    val cbClose = CircuitBreaker[Close]()
    val cbOpen = cbClose.open //Only open can be invoked
    cbOpen.close //Only close can be invoked

    val halfOpen = CircuitBreaker[HalfOpen]()
    val cbClose2 = halfOpen.close //Only open/close can be invoked
    cbClose2.open //Only open can be invoked

    val cbOpen2 = halfOpen.open //Only open/close can be invoked
    cbOpen2.close //Only close can be invoked
  }

}
