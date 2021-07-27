package com.politrons.cats

import cats.effect.IO
import cats.effect.IO.async_
import cats.effect.std.{CountDownLatch, Semaphore}
import cats.effect.unsafe.IORuntime
import cats.implicits._

import java.util.UUID
import scala.concurrent.duration.DurationInt
import scala.util.{Random, Try}

object CatsEffects {

  implicit val runtime: IORuntime = IORuntime.global

  def main(args: Array[String]): Unit = {
    monadIO()
    asyncIO()
    memorize()
    fiber()
    countDownLatch()
    race()
    semaphore()
  }

  /**
    * IO monad to introduce Pure functional programing in Cats.
    * Providing referential transparency, lazy evaluation, and the possibility to use Fibers
    * instead OS Threads.
    */
  def monadIO(): Unit = {
    val program: IO[Unit] =
      for {
        _ <- IO.println("Hello")
        _ <- IO.println("Pure")
        _ <- IO.println("Functional")
        _ <- IO.println("World")
      } yield ()
    program.unsafeRunSync()
  }

  /**
    * AsyncIO allow us same than IO but running in another thread.
    * Once the evaluation of the program finish the change og contect from the
    * thread to the main thread happens
    */
  def asyncIO(): Unit = {

    val program = for {
      value <- async_[String](eitherFunc =>
        eitherFunc(Right(s"hello world in Thread:${Thread.currentThread().getName}")))
      value <- async_[String](eitherFunc =>
        eitherFunc(Right(value + "!!!ª")))
    } yield value.toUpperCase()

    val output = program.unsafeRunSync()
    println(output)

    val errorProgram = async_[String] { eitherFunc =>
      eitherFunc(Left(new IllegalArgumentException()))
    }

    val error = Try(errorProgram.unsafeRunSync())
    println(error.failed.get)

  }

  /**
    * Using operator [memorize] we're able to wrap an effect, and memorize the output, so
    * the next time is invoked, we wont run the effect again, and we will just return the output
    * of the effect the first time we evaluate.
    */
  def memorize(): Unit = {
    val effect: IO[String] = IO(s"Value: ${UUID.randomUUID()} \n")
    val program: IO[String] = for {
      memoized <- effect.memoize
      out1 <- memoized
      out2 <- memoized
      out3 <- memoized

    } yield out1 ++ out2 ++ out3
    val output = program.unsafeRunSync()
    println(output)
  }

  /**
    * Fiber are lightweight threads also knows as Green threads. Are threads created in the JVM
    * instead in the OS.
    * Using IO, we can make the execution of the effect run in a Fiber using [start] operator.
    * We receive then a Fiber instance, and to pass the output value to the main thread, we
    * have to use [join]
    */
  def fiber(): Unit = {
    val program =
      for {
        fiber1 <- IO(s"Task 1 in Thread:${Thread.currentThread().getName}\n").start
        fiber2 <- IO(s"Task 2 in Thread:${Thread.currentThread().getName}\n").start
        out1 <- fiber1.joinWithNever
        out2 <- fiber2.joinWithNever
      } yield out1 + out2
    println(program.unsafeRunSync())
  }

  /**
    * [CountDownLatch] allow us await an effect until the countdown of the number passed in the constructor  is 0.
    * To reduce the number in the CountDownLatch we use [release]
    */
  def countDownLatch(): Unit = {
    val program =
      for {
        cdl <- CountDownLatch[IO](3)
        f <- (cdl.await >> IO.println(s"After release all latch running final action in Fiber: ${Thread.currentThread().getName}")).start
        _ <- cdl.release
        _ <- IO.println("Running action 1")
        _ <- cdl.release
        _ <- IO.println("Running action 2")
        _ <- cdl.release
        _ <- f.join
      } yield ()
    program.unsafeRunSync()
  }

  /**
    * [Race] Operator allow us make a race between two effects and return an Either with the left or right wich
    * is the winner of the race.
    */
  def race(): Unit = {
    val program = IO.race(
      IO {
        Thread.sleep(new Random().nextInt(1000))
        "Task1"
      }, IO {
        Thread.sleep(new Random().nextInt(1000))
        "Task2"
      })
    println(program.unsafeRunSync())
  }

  /**
    * [Semaphore] provide the feature to only allow access to one specific part of the effect to the number
    * of programs that we specify in the constructor.
    * Each time we use [acquire] we reduce that number, and in case there's still no zero, we can proceed,
    * otherwise we will wait, until we use the other operator [release] which it will increase the semaphore number.
    * Finally [available] will let us know how many semaphore number are availables.
    */
  def semaphore(): Unit = {
    val program =
      for {
        semaphore <- Semaphore[IO](2)
        f <- runTask(semaphore).start
        f1 <- runTask(semaphore).start
        f2 <- runTask(semaphore).start
        f3 <- runTask(semaphore).start
        _ <- f.join
        _ <- f1.join
        _ <- f2.join
        _ <- f3.join
      } yield ()

    program.unsafeRunSync()
  }

  def runTask(semaphore: Semaphore[IO]): IO[Unit] = {
    for {
      x <- semaphore.available
      _ <- IO.println(s"Preparing to work. Spot available $x")
      _ <- semaphore.acquire
      y <- semaphore.available
      _ <- IO.println(s"Working...  Spot available $y")
      _ <- semaphore.release.delayBy(2.seconds)
    } yield ()
  }

}
