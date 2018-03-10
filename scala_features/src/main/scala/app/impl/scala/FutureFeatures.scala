package app.impl.scala

import org.junit.Test

import scala.concurrent.Future

import scala.concurrent.ExecutionContext.Implicits.global
import com.twitter.util.{Future => TwitterFuture}
import scala.concurrent.{Future => ScalaFuture, Promise => ScalaPromise}

/**
  * Future of Scala it´s far more advance that Java 7 Future class, it´s a monad so you can mutate or compose futures
  * using Map and FlatMap, also add callbacks functions to let you know when future are resolved without have to block the
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

  /**
    * Future is a Functor/Monad so implement Map operator, where we can mutate the value that is being resolved in the pipeline.
    */
  @Test def mapFutures(): Unit = {
    Future("hello|future|world")
      .map(s => s.replace("|", " "))
      .map(s => s.toUpperCase)
      .onComplete(sentence => println(sentence))
    Thread.sleep(1000)
  }

  /**
    * Just like all monads you can use flatMap operator to add a new monad embedded in the other one.
    */
  @Test def flatFutures(): Unit = {
    Future("Hello")
      .flatMap(s => Future(s + " Future")
        .flatMap(s1 => Future(s1 + " World")))
      .onComplete(sentence => println(sentence))
    Thread.sleep(1000)
  }

  /**
    * Zip is one of the most value operators in Future API, allowing you to compose in parallel multiples futures which
    * make a big difference when we talk about performance in our systems.
    * Every zip create a tuple between the two calls, so as you can imagine if I zip two elements, it become
    * (T,T) and if I zip that tuple I would have a tuple of T and tuple (T,(T,T)) and so on.
    *
    */
  @Test
  def zipFutures(): Unit = {
    Future("This")
      .zip(Future("is"))
      .zip(Future("a"))
      .zip(Future("race"))
      .map(tuple => {
        val compose = tuple._1._1._1 + " " + tuple._1._1._2 + " " + tuple._1._2 + " " + tuple._2
        compose
      })
      .map(sentence => sentence.toUpperCase)
      .onComplete(sentence => println(sentence))
    Thread.sleep(1000)
  }

  /**
    * Creates a new future by applying the left function to the successful result
    * or the right function to the failed result.
    */
  @Test
  def transformFutures(): Unit = {
    Future("Let´s|transform|this|future")
      .map(sentence => sentence.replace("|", " "))
      .transform(sentence => sentence.toUpperCase, f => CustomException(f.getMessage))
      .onComplete(sentence => println(sentence))

    Future("Let´s transform this future")
      .map(sentence => {
        sentence.asInstanceOf[Integer]
        sentence
      })
      .transform(sentence => sentence.toUpperCase, _ => CustomException("Error during transformation"))
      .onComplete(sentence => println(sentence.failed.get))
    Thread.sleep(1000)
  }

  /**
    * Sequence operator allow you to extract the value from a future without block the thread once it´s resolved
    * Here for instance we pass to have a List[Future[T]] to have a List[T]
    */
  @Test
  def sequence(): Unit = {
    val eventualEventualAccounts = futureList.map(list => {
      list.map(account => {
        upperCaseFuture(account.status).map(value => Account(value))
      })
    }).flatMap(futureOfList => {
      Future.sequence(futureOfList)
    })
    eventualEventualAccounts.foreach(account => println(account))
  }

  case class CustomException(message: String) extends Exception

  case class Account(status: String)

  var futureList: Future[List[Account]] = Future {
    List(Account("test"), Account("future"), Account("sequence"))
  }

  def upperCaseFuture(s: String): Future[String] = {
    Future {
      s.toUpperCase
    }
  }

  /**
    * Traverse operator allow you to create a Future from one initial TraversableOnce type as subtype list[A]
    * into a Future[List[B]] after you apply a function to create a Future of type B  Future[B]
    */
  @Test
  def traverse(): Unit = {
    val primitiveNumberList = List(1, 2, 3, 4, 5)
    val numberList = Future.traverse(primitiveNumberList)(x => Future(Number(x)))
    Thread.sleep(1000)
    println(numberList)

    val listOfAny = Future.traverse(List("This", "is", 1, "awesome", 2))(x => Future(toUpperCaseString(x)))
    Thread.sleep(1000)
    println(listOfAny)

  }

  private def toUpperCaseString(x: Any) = {
    x match {
      case str: String => str.toUpperCase
      case _ => "EJEM"
    }
  }

  case class Number(value: Int)

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