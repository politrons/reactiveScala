package app.impl.scala

import org.junit.Test

import scala.concurrent._

import ExecutionContext.Implicits.global


/**
  * Created by pabloperezgarcia on 18/8/16.
  */
class Futures {


  @Test def testFuture(): Unit ={
    future.onComplete(x=> println(s"List emitted:${x.get}"))
    println(s"Main thread:${Thread.currentThread().getName}")
  }
  val future: Future[List[Int]] = Future {
    println(s"Future thread:${Thread.currentThread().getName}")
    List[Int](1,2,3)
  }



}
