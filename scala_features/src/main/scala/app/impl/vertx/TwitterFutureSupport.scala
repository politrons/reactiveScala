package app.impl.vertx

import com.twitter.util.{Future => TwitterFuture}
import scala.concurrent.{Future => ScalaFuture, Promise => ScalaPromise}

object TwitterFutureSupport {
  /**
  Implicit conversion from a Twitter Future to a Scala Future
    **/
  implicit def twitterFutureToScalaFuture[T](twitterF: TwitterFuture[T]): ScalaFuture[T] = {
    val scalaPromise = ScalaPromise[T]
    twitterF.onSuccess { r: T =>
      scalaPromise.success(r)
    }
    twitterF.onFailure { e: Throwable =>
      scalaPromise.failure(e)
    }
    scalaPromise.future
  }
}