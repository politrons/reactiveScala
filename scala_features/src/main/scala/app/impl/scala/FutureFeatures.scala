package app.impl.scala

import com.twitter.util.{Future => TwitterFuture}
import org.junit.Test

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * Future of Scala it´s far more advance that Java 7 Future class, it´s a monad so you can mutate or compose futures
  * using Map and FlatMap, also add callbacks functions to let you know when future are resolved without have to block the
  * thread waiting for the resolution as Java Future does.
  * The only thing quite similar, and as usual more verbose is CompletableFuture of Java 8 (Jesus even the name is Verbose XD!)
  *
  * Sorry to do not use Await but in some examples I have several futures and.... well I´m lazy
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
    * Create a new future form the preivous one and check with the partial function if the value inside the future is
    * what you expect. Otherwise throw a [NoSuchElementException]
    */
  @Test def collect(): Unit = {
    Future("Hello collect operator")
      .collect(isStringPartialFunction)
      .onComplete(value => println(value))

    Future(1)
      .collect(isStringPartialFunction)
      .onComplete(value => println(value))
    Thread.sleep(1000)
  }

  /**
    * You can combine so many futures as you want using flatMap.
    */
  @Test def flatMap2(): Unit = {

    val future1 = Future("hello")
    val future2 = Future(" combining")
    val future3 = Future(" world")

    val flatMap3 = future1
      .flatMap(word1 => future2
        .flatMap(word2 => future3
          .map(word3 => word1.concat(word2).concat(word3))))
    flatMap3.onComplete(sentence => println(sentence))
    Thread.sleep(1000)
  }

  private val isStringPartialFunction: PartialFunction[Any /*Entry type*/ , String /*Output type*/ ] = {

    case input if input.isInstanceOf[String] => input.asInstanceOf[String]

  }

  //######################################
  //####### From Future factory ##########
  //######################################


  /**
    * Success factory operator create a promise from the type T that you pass and return a future.
    */
  @Test
  def success(): Unit = {
    Future.successful("This future sentence always will work")
      .onComplete(sentence => println(sentence))

    Future.successful(() => "This future function always will work")
      .map(func => func.apply())
      .onComplete(sentence => println(sentence))

    Future.successful("Success future")
      .onSuccess(isStringPF)

    Future.successful(1)
      .onSuccess(isStringPF)

    Thread.sleep(1000)
  }

  private val isStringPF = new PartialFunction[Any /*Entry type*/ , Unit] {
    def apply(d: Any) = println(d)

    def isDefinedAt(d: Any) = d.isInstanceOf[String]
  }

  /**
    * Reduce operator, just works like in any other monad, we pass an initial TraversableOnce and per iteration
    * we have a bifunction with the previous iteration value [previous] and the new one [current] to interact to each other.
    * In this exmaple we decide to combine all them in a unique sentence, but the most common use it´s for arithmetic operations.
    */
  @Test
  def reduce(): Unit = {
    val futures = List(Future("welcome"), Future("to"), Future("the"), Future("future"))
    Future.reduce(futures)((previous, current) => previous + " " + current)
      .map(sentence => sentence.toUpperCase)
      .onComplete(value => println(value.get))
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

}