package com.politrons.cats

import cats.effect.IO
import cats.effect.IO.{async, async_}
import cats.effect.unsafe.IORuntime

import java.util.UUID
import scala.util.Try

object CatsEffects {

  implicit val runtime: IORuntime = IORuntime.global

  def main(args: Array[String]): Unit = {
    monadIO()
    asyncIO()
    memorize()
  }

  /**
    * IO monad to introduce Pure functional programing in Cats.
    * Providing referential transparency, lazy evaluation, and the possibility to use Fibers
    * instead OS Threads.
    */
  def monadIO(): Unit = {
    val program: IO[Unit] =
      for {
        _ <- printPure("Hello")
        _ <- printPure("Pure")
        _ <- printPure("Functional")
        _ <- printPure("World")
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

  def printPure(msg: String): IO[Unit] = IO {
    println(msg)
  }

}
