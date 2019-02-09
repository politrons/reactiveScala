package app.impl.haskell

import java.util.Calendar._

import app.impl.haskell.ScalaHaskell.{Time, Word}
import org.junit.Test
import scalaz.ioeffect.{IO, RTS}


/**
  * Just like using the Monad IO of Haskell, here we use the IO Monad for ScalaZ.
  * What really make this code pure like in Haskell is, that is not eager evaluation but lazy,
  * so just like in Haskell, if we use composition, those functions in the IO wont
  * be executed until we finish our program and we use [unsafePerformIO] which means
  * we can compose those pure functions in our program knowing that those will provide
  * same result once are executed.
  */
class ScalaHaskell extends RTS {

  /**
    * One of the best ways to emulate Haskell from Scala is using for comprehension which is
    * just like [do block] a sugar syntax to make sequential composition of functions.
    */
  @Test
  def doBlockWithIO(): Unit = {
    val io = for {
      result <- concatTwoWords(Word("hello"))(Word("world"))
      result <- appendValue(result)
      result <- transformToUpperCase(result)
    } yield result

    println(unsafePerformIO(io))

  }

  /**
    * Here we can see an example of pure referential transparency where our
    * function receive Word => Word and we always return IO[Throwable, Word]
    * of that value once is evaluated.
    */
  def concatTwoWords: Word => Word => IO[Throwable, Word] =
    word => word1 => IO.point(Word(s"${word.value} - ${word1.value}"))

  def appendValue: Word => IO[Throwable, Word] =
    word => IO.point(Word(word.value.concat("!!!!")))

  def transformToUpperCase: Word => IO[Throwable, Word] =
    word => IO.point(Word(word.value.toUpperCase))


  /**
    * In this example we show how using IO monad like in Haskell we can have pure Functional programing
    * using Referential transparency, we are doing composition of this three functions, and it does not
    * matter how many times we compose those functions, since are lazy evaluated we know that function
    * will give same result in time for example in here.
    * We create the IO and even waiting 2 seconds before evaluate the io and go through pure to impure
    * we see the time it never was set in place when we were composing the functions.
    */
  @Test
  def lazyEvaluation(): Unit = {
    val io = for {
      time <- pureTimeFunction(Time(""))
      time <- pureTimeFunction(time)
      time <- pureTimeFunction(time)
    } yield time
    val now = getInstance()
    println(s"Time before we start the evaluation: ${now.get(MINUTE)} - ${now.get(SECOND)}")
    Thread.sleep(2000)
    println(unsafePerformIO(io))
  }

  def pureTimeFunction: Time => IO[Throwable, Time] =
    time => IO.point {
      val now = getInstance()
      Time(s"${time.value} : ${now.get(MINUTE)} - ${now.get(SECOND)}")
    }

}

object ScalaHaskell {

  case class Word(value: String) extends AnyVal

  case class Time(value: String) extends AnyVal

}
