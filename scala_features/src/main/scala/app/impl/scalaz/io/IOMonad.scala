package app.impl.scalaz.io

import java.io.File

import org.junit.Test
import scalaz.ioeffect.{Fiber, IO, RTS}


/**
  *
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

  @Test
  def happyMapping(): Unit = {
    val sentence: IO[Throwable, String] =
      IO.point("Hello pure world")
        .map(sentence => sentence.toUpperCase())
        .flatMap(sentence => IO.point(sentence.concat("!!!!")))
    println(unsafePerformIO(sentence))
  }


  @Test
  def catchAllOperator(): Unit = {

    //    val value = IO.point[Throwable, File](openFile)
    //      .map(file => file.getAbsolutePath)
    //      .catchAll(_ => IO.point[Throwable, String]("No file name"))
    //    println(unsafePerformIO(value))

    //    val errorSentence = IO.point[Throwable, String](null)
    //      .flatMap(value => IO.syncThrowable(value.toUpperCase()))
    //      .catchAll(_ => IO.now[Throwable, String]("Default value"))
    //    println(unsafePerformIO(errorSentence))


    //    val value = IO.point[Throwable, String]("Hello world")
    //      .map(_ => new NullPointerException)
    //      .catchAll {
    //        case value: BusinessError => IO.now[Throwable, String]("Strint")
    //        case e: Throwable => IO.now[Throwable, String]("sdddd")
    //        case _ => IO.now[Throwable, String]("sdddd")
    //      }
    //    println(unsafePerformIO(value))


    //    val fa: IO[Throwable, String] =
    //
    //      fa.catchAll {
    //        case e: MyCustomError => IO.now(Response(e.getMessage, status = BadRequest))
    //        case e: Throwable => IO.syncThrowable(myLogger.info(e)("Caught unhandled error in application")) *> IO.now(Response(status = InternalServerError))
    //      }
  }

  case class BusinessError() extends Exception


  def openFile: File = {
    new File("bla.json")
  }

  @Test
  def catchSomeOperator(): Unit = {

    val errorSentence = IO.point[Throwable, String](null)
      .flatMap(value => IO.syncThrowable(value.toUpperCase()))
      .catchSome {
        case _: NullPointerException => IO.now("Default value")
      }
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

