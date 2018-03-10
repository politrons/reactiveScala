package app.impl.scala

import org.junit.Test

import scala.concurrent.Future

import scala.concurrent.ExecutionContext.Implicits.global
import com.twitter.util.{Future => TwitterFuture}
import scala.concurrent.{Future => ScalaFuture, Promise => ScalaPromise}

/**
  * Future of Scala it´s far more advance that Java 7 Future class, it´s a monad so you can mutate or compose futures
  * using Map and FlatMap, also add callbacks functions to let you know when future it´´ resolved without have to block the
  * thread waiting for the resolution as Java Future does.
  * The only thing quite similar, and as usual more verbose is CompletableFuture of Java 8 (Jesus even the name is Verbose XD!)
  */
class FutureFeatures {


  @Test def testFuture(): Unit = {
    future.onComplete(x => println(s"Value emitted:${x.get}"))
    println(s"Main thread:${Thread.currentThread().getName}")
    Thread.sleep(1000)
  }

  /**
    * Fallback future in case you´´e familiarize with RxJava as onErrorResumeNext giving you the chance in case the
    * element you expect fails to be obtained you can try to get another future.
    * Here first future throw an exception so we decide to try with another future.
    */
  @Test def testFallback(): Unit = {
    errorFuture
      .fallbackTo(future)
      .onComplete(x => println(s"Value emitted:${x.get}"))

    println(s"Main thread:${Thread.currentThread().getName}")
    Thread.sleep(1000)
  }

  val future: Future[String] = Future {
    Thread.sleep(500)
    println(s"Future thread:${Thread.currentThread().getName}")
    "result"
  }

  val errorFuture: Future[String] = Future {
    throw new NullPointerException
  }

  case class Account(status: String)

  var futureList: Future[List[Account]] = Future {
    List(Account("test"), Account("future"), Account("sequence"))
  }


  /**
    * Sequence operator allow you to extract the value from a future without block the thread once it´s resolved
    * Here for instance we pass to have a List[Future[T]] to have a List[T]
    */
  @Test
  def sequence(): Unit = {
    val eventualEventualAccounts = futureList.map(list => {
      list.map(account => {
        transform(account.status).map(value => Account(value))
      })
    }).flatMap(futureOfList => {
      Future.sequence(futureOfList)
    })
    eventualEventualAccounts.foreach(account => println(account))
  }

  def transform(s: String): Future[String] = {
    Future {
      s.toUpperCase
    }
  }

  /**
    * Conversion from a Twitter Future to a Scala Future
    **/
  def twitterFutureToScalaFuture[T](twitterF: TwitterFuture[T]): ScalaFuture[T] = {
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