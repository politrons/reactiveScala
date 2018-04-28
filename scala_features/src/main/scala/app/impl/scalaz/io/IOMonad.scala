package app.impl.scalaz.io

import java.io.File

import org.junit.Test
import scalaz.ioeffect.{Fiber, IO, RTS}


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
    var value:String=null
    val errorSentence = IO.point[Throwable, String](value)
      .flatMap(value => IO.syncThrowable(value.toUpperCase()))
      .catchAll[Throwable](t => IO.now(s"Default value since $t happens"))
      .map(value => value.toUpperCase())

    println(unsafePerformIO(errorSentence))
    value="Now it should works right?"
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
      case t: NullPointerException => IO.now[Throwable, String]("You had a NPE")
      case _ => IO.now("What was that?!")
    }
    println(unsafePerformIO(errorSentence))
    value = "ArrayIndexOutOfBoundException"
    println(unsafePerformIO(errorSentence))
    value = "ArrayIndexOutOfBoundException it's not gonna happen now!"
    println(unsafePerformIO(errorSentence))
  }

  /**
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
    * Fiber is like Scala Future, the execution of the process it will executed in another thread,
    */
  @Test
  def fiberFeature(): Unit = {
    println(s"Before ${Thread.currentThread().getName}")
    val ioFuture: IO[Throwable, Fiber[Throwable, String]] = IO.point[Throwable, String]("Hello async IO world")
      .map(sentence => {
        Thread.sleep(5000)
        println(s"Business logic ${Thread.currentThread().getName}")
        sentence.toUpperCase()
      })
      .fork[Throwable]

    val sentence = ioFuture.flatMap(fiber => fiber.join)

    println(s"After: ${Thread.currentThread().getName}")
    println(unsafePerformIO(sentence))
  }


}

