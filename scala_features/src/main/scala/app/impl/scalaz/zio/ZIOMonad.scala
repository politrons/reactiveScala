package app.impl.scalaz.zio

import org.junit.Test
import scalaz.zio.{DefaultRuntime, IO, Task, ZIO}

class ZIOMonad {

  val runtime: DefaultRuntime = new DefaultRuntime {}

  //##########################//
  //         EFFECTS          //
  //##########################//

  //##################//
  //    CREATION       //
  //##################//
  /**
    * Using ZIO we can define like the monad IO from Haskell, the monad by definition it's lazy,
    * which means it respect the monad laws and it's referential transparency. Only when it's evaluated
    * using [runtime.unsafeRun(monad)] it's been we move from the pure functional realm and we go into the
    * effect world.
    * In order to control effects this monad has 3 types defined.
    * * R - Environment Type. This is the type of environment required by the effect. An effect that
    * * has no requirements can use Any for the type parameter.
    * * E - Failure Type. This is the type of value the effect may fail with. Some applications will
    * * use Throwable. A type of Nothing indicates the effect cannot fail.
    * * A - Success Type. This is the type of value the effect may succeed with. This type parameter
    * * will depend on the specific effect, but Unit can be used for effects that do not produce any useful information, while Nothing can be used for effects that run forever.
    */
  @Test
  def fromZIOSucceed(): Unit = {
    val sentenceMonad: ZIO[Any, Throwable, String] =
      ZIO
        .succeed("Hello pure functional wold" )
        .map(value => value.toUpperCase())
        .flatMap(word => ZIO.succeed(word + "!!!"))

    val succeedSentence = runtime.unsafeRun(sentenceMonad)
    println(succeedSentence)
  }

  /**
    * Using Task monad, we can no control effects since only contemplate one possible type.
    */
  @Test
  def fromTaskSucceed(): Unit = {
    val task: Task[String] =
      Task
        .succeed("Hello pure functional ")
        .flatMap(sentence => Task.succeed(sentence + " world"))
        .map(sentence => sentence.toUpperCase())

    val taskSentence = runtime.unsafeRun(task)
    println(taskSentence)
  }

  /**
    * We describe before that using IO monad the evaluation of the monad it's lazy. And it's true
    * but what it's not lazy evaluation it's the input value of the monad, in order to do have also
    * a lazy evaluation of that value, you need to use operator [succeedLazy] it will behave just like
    * Observable.defer of RxJava
    */
  @Test
  def fromZIOLazy(): Unit = {
    val lazyIputMonad = ZIO.succeedLazy(s"Monad___ ${System.currentTimeMillis()}")
    Thread.sleep(1000)
    println("No Monad " + System.currentTimeMillis())
    val laztSentence = runtime.unsafeRun(lazyIputMonad)
    println(laztSentence)
  }

  /**
    * Also we can create a monad IO with a failure that some errors were not
    * controlled properly
    */
  @Test
  def fromZIOFailure(): Unit = {
    val errorMonad: IO[String, Throwable] =
      ZIO.fail("Hello pure functional wold")
    runtime.unsafeRun(errorMonad)
  }

  /**
    * Also we can create a monad IO with a throwable propagation, expressions that some errors were not
    * controlled properly
    */
  @Test
  def fromZIOThrowable(): Unit = {
    val errorMonad1: IO[Exception, Nothing] =
      ZIO.fail(new IllegalArgumentException("Hello pure functional wold"))

    runtime.unsafeRun(errorMonad1)
  }

  //##################//
  //    CREATION       //
  //##################//

  @Test
  def main(): Unit = {

  }

}
