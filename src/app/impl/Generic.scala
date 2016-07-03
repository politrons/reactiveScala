package app.impl

import java.util.concurrent.TimeUnit

import _root_.scala.concurrent.duration.{Duration, FiniteDuration}

/**
  * Created by pabloperezgarcia on 26/6/16.
  */
class Generic[T, L <: Long] {

  protected def addHeader(header: T): Unit = {
    println("\n" + header)
    println("\n***********************************")

  }

  protected def createDuration(time: L): FiniteDuration = {
    Duration.create(time, TimeUnit.MILLISECONDS)
  }

}
