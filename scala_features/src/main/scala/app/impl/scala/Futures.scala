package app.impl.scala

import org.junit.Test

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent._


/**
  * Created by pabloperezgarcia on 18/8/16.
  */
class Futures {


  @Test def testFuture(): Unit = {
    future.onComplete(x => println(s"Value emitted:${x.get}"))
    println(s"Main thread:${Thread.currentThread().getName}")
    Thread.sleep(1000)
  }

  @Test def testFallback(): Unit = {
    errorFuture
      .fallbackTo(future)
      .onComplete(x => println(s"Value emitted:${x.get}"))

    println(s"Main thread:${Thread.currentThread().getName}")
    Thread.sleep(1000)
  }

  val future: Future[String] = Future {
    println(s"Future thread:${Thread.currentThread().getName}")
    "result"
  }

  val errorFuture: Future[String] = Future {
    throw new NullPointerException
  }


}
