import java.util.concurrent.TimeUnit

import _root_.scala.concurrent.duration.{Duration, FiniteDuration}

/**
  * Created by pabloperezgarcia on 26/6/16.
  */
class Generic {

  def addHeader(header: String): Unit = {
    println("\n***********************************")
    println("\n*********" + header + "************")
    println("\n***********************************")
  }

  def createDuration(time: Long): FiniteDuration = {
    Duration.create(time, TimeUnit.MILLISECONDS)
  }

}
