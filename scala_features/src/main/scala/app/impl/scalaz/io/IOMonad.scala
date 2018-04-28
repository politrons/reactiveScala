package app.impl.scalaz.io

import org.junit.Test
import scalaz.ioeffect.{Fiber, IO, RTS}

import scala.concurrent.duration.Duration

import scala.concurrent.duration._

/**
  *
  * Monad IO is a monad like Observable that helps to implement pure Functional programing without side effects.
  * Everything it's typed, and is using an approach similar as the Either where you have a Left or Right type in your
  * output.
  */
class IOMonad extends RTS {


  /**
    * just like in Rx if we use now to apply a value to the monad it will be set the value at the moment of the
    * creation of the monad, but we use **point** it will evaluated the values passed in the IO monad when it
    * will be interpreter.
    */
  @Test
  def normalVsDefer(): Unit = {
    var sentence = "Hello World now"
    val deferIO: IO[Void, String] = IO.point(sentence)
    val nowIO: IO[Void, String] = IO.now(sentence)
    sentence = sentence.replace("now", "later")
    println(unsafePerformIO(deferIO))
    println(unsafePerformIO(nowIO))
  }

  @Test
  def impureCode(): Unit = {
    val nanoTime: IO[Void, Long] =
      IO.sync(System.nanoTime())
    println(unsafePerformIO(nanoTime))
  }

  /**
    * Like other monads we can just use map to transform data and flatMap to compose IOs
    */
  @Test
  def happyMapping(): Unit = {
    val sentence: IO[Throwable, String] =
      IO.point("Hello impure world")
        .map(sentence => sentence.replace("impure", "pure"))
        .map(sentence => sentence.toUpperCase())
        .flatMap(sentence => IO.point(sentence.concat("!!!!")))
    println(unsafePerformIO(sentence))
  }


  /**
    * CatchAll operator is really handy when you have to treat with unsafe code that might propaget unexpected side effect
    * in your pipeline as Throwable.
    * Since we have this catch in our pipeline whatever not expected effect it will catch and transform in the expected
    * output type of the IO
    */
  @Test
  def catchAllOperator(): Unit = {
    var value: String = null
    val errorSentence = IO.point[Throwable, String](value)
      .flatMap(value => IO.syncThrowable(value.toUpperCase()))
      .catchAll[Throwable](t => IO.now(s"Default value since $t happens"))
      .map(value => value.toUpperCase())

    println(unsafePerformIO(errorSentence))
    value = "Now it should works right?"
    println(unsafePerformIO(errorSentence))
  }

  /**
    * CatchSome operator is really handy when you have to treat with unsafe code that might propagate unexpected side effect
    * in your pipeline as Throwable.
    * Using pattern matching we can decide which type of throwable we want to catch and transform to IO
    * The behaviour is just like Observable.onErrorResumeNext operator.
    */
  @Test
  def catchSomeOperator(): Unit = {
    var value: String = null
    val errorSentence = IO.point[Throwable, String](value)
      .flatMap(value => IO.syncThrowable(value.toUpperCase())) //This line will make first test fail
      .flatMap(value => IO.syncThrowable(value.substring(30, 56))) //This line will make second test fail
      .catchSome {
      case t: NullPointerException => IO.now[Throwable, String](s"You had a $t")
      case _ => IO.now("What was that?!")
    }
    println(unsafePerformIO(errorSentence))
    value = "ArrayIndexOutOfBoundException"
    println(unsafePerformIO(errorSentence))
    value = "ArrayIndexOutOfBoundException it's not gonna happen now!"
    println(unsafePerformIO(errorSentence))
  }

  /**
    * Again, we have to realize that Pure FP in IO communications cannot exist, is impure by design, the network might fail,
    * The server you call might not be available and so on. So one more time we have to assume that some impure code
    * might fail.
    * Here Retry operator will take of retry the operation until he achieve to receive the type that IO expect for the output.
    *
    * It will retry the operator forever until achieve the result that expect
    */
  @Test
  def retryOperator(): Unit = {
    val sentence: IO[Throwable, String] =
      IO.point(getSentence)
        .flatMap(value => IO.syncThrowable(value.toUpperCase()))
        .retry
    println(unsafePerformIO(sentence))
  }

  def getSentence: String = {
    if (math.random < 0.0000001) "Hi pure functional world" else null
  }

  /**
    * Fiber is like Scala Future, the execution of the process it will executed in another thread.
    * Here the syntax it's quite clear, when we want to start the execution in a new Thread we use [[fork]]
    * operator. At that moment IO create a new output type as Fiber[L,R]
    *
    * Just like with futures after we run the execution of the IO function we will have to wait until the other
    * Thread finish. Here we just add a silly Sleep to wait for the resolution.
    */
  @Test
  def fiberFeature(): Unit = {
    println(s"Before ${Thread.currentThread().getName}")
    val ioFuture: IO[Throwable, Fiber[Throwable, String]] = IO.point[Throwable, String]("Hello async IO world")
      .map(sentence => {
        println(s"Business logic ${Thread.currentThread().getName}")
        sentence.toUpperCase()
      }).delay(1 second)
      .fork[Throwable] //This operator make the execution of the function run in another thread.

    println(s"After: ${Thread.currentThread().getName}")
    unsafePerformIO(ioFuture)
    Thread.sleep(2000)
  }

  /**
    * Using the join operator we can join the thread local values from one thread into the main returning
    * the Fiber type R form Fiber[L,R] to IO[L,R]
    */
  @Test
  def fiberAwait(): Unit = {
    println(s"Before ${Thread.currentThread().getName}")
    val ioFuture: IO[Throwable, Fiber[Throwable, String]] = IO.point[Throwable, String]("Hello async IO world")
      .delay(1 seconds)
      .map(sentence => sentence.toUpperCase())
      .fork[Throwable]

    println(s"After: ${Thread.currentThread().getName}")
    val sentence = ioFuture.flatMap(fiber => fiber.join)
    unsafePerformIO(sentence)
  }

  /**
    * IO unfortunately has no funcy operators as zip, but maybe I'm one of the fews gusy that I normslly never use
    * Zip but just flatMap for composition of Futures.
    * So here with Fibers we can do pretty much the same.
    * In this example we use some sugar to make the composition of the Fibers created by the Fork.
    */
  @Test
  def compositionOfFibersWithSugar(): Unit = {
    println(s"Before ${Thread.currentThread().getName}")

    def composition: IO[Throwable, String] = for {
      fiber <- createIO("Business logic 1").fork
      fiber1 <- createIO("Business logic 2").fork
      v2 <- fiber1.join
      v1 <- fiber.join
    } yield v1 + v2

    println(s"After: ${Thread.currentThread().getName}")
    println(unsafePerformIO(composition))
  }

  /**
    * And Here if you're a Hardcore FP same example without sugar syntax
    */
  @Test
  def compositionOfFibersNotSugar(): Unit = {
    println(s"Before ${Thread.currentThread().getName}")
    val composition: IO[Throwable, String] = createIO("Business logic 1").fork
      .flatMap(fiber => createIO("Business logic 2").fork
        .flatMap(fiber1 => fiber1.join
          .flatMap(v2 => fiber.join
            .map(v1 => v1 + v2))))
    println(s"After: ${Thread.currentThread().getName}")
    println(unsafePerformIO(composition))
  }

  private def createIO(sentence: String): IO[Throwable, String] = {
    IO.point[Throwable, String](sentence)
      .map(sentence => s" $sentence ${Thread.currentThread().getName}".toUpperCase())
      .delay(1 second)
  }


}

