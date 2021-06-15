package com.features.zio

import com.twitter.util.{Await, Future => TwitterFuture}
import zio.{Task, ZIO}

import scala.concurrent.{ExecutionContext, Promise, Future => ScalaFuture}

object ZIOTwitter extends App {

  val main: zio.Runtime[zio.ZEnv] = zio.Runtime.default

  object ZIOTwitter {

    def fromFuture[A](future: TwitterFuture[A]): Task[A] = {
      ZIO.fromFuture(ec => transformFutureTwitterToScalaFuture(ec, future))
    }

    def transformFutureTwitterToScalaFuture[A](ec: ExecutionContext, future: TwitterFuture[A]): ScalaFuture[A] = {
      implicit val executionContext: ExecutionContext = ec
      val promise: Promise[A] = Promise()
      future.onSuccess(a => promise.success(a))
      future.onFailure(t => promise.failure(t))
      promise.future
    }
  }

  private val output: String = main.unsafeRun(
    ZIOTwitter
      .fromFuture(TwitterFuture {
        println(s"Running this effect in ${Thread.currentThread().getName}")
        "Hello from Twitter"
      }).map(e => e.toUpperCase))

  println(output)


//  private val future: TwitterFuture[String] = TwitterFuture {
//    println(s"Running this effect out of zio in ${Thread.currentThread().getName}")
//    Thread.sleep(2000)
//    "Hello from Twitter"
//  }
//
//  println("Present")
//  Await.result(future)

}
