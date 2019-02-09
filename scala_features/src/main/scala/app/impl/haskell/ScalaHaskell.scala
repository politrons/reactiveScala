package app.impl.haskell

import app.impl.haskell.ScalaHaskell.Word
import org.junit.Test
import scalaz.ioeffect.{IO, RTS}


/**
  * Just like using the Monad IO of Haskell, here we use the IO Monad for ScalaZ.
  * What really make this code pure like in Haskell is, that is not eager evaluation but lazy,
  * so just like in Haskell, if we use composition, those functions in the IO wont
  * be executed until we finish our program and we use [unsafePerformIO]
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

    println(unsafePerformIO(io)) //We pass from lazy to eager

  }

  def concatTwoWords: Word => Word => IO[Throwable, Word] =
    word => word1 => IO.point(Word(s"${word.value} - ${word1.value}"))

  def appendValue: Word => IO[Throwable, Word] =
    word => IO.point(Word(word.value.concat("!!!!")))

  def transformToUpperCase: Word => IO[Throwable, Word] =
    word => IO.point(Word(word.value.toUpperCase))


  @Test
  def lazyEvaluation(): Unit = {

  }

}

object ScalaHaskell {

  case class Word(value: String) extends AnyVal

}
