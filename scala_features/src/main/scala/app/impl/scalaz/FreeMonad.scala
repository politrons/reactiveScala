package app.impl.scalaz

import java.util.concurrent.TimeUnit

import org.junit.Test

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}
import scala.util.Random
import scalaz.Free._
import scalaz.{Free, ~>}

/**
  * Created by pabloperezgarcia on 15/10/2017.
  *
  * Using ScalaZ we can create our own monad transformer, as a monad have to respect the 3 monad laws.
  * We will create a type of monad using the Free[IN, OUT] and then we define our monad transformers just
  * creating functions that implement liftF and return that type.
  * once we define our monad transformers we need to create an interpreter to interact with the monad that we
  * just create. As usual if we use the monad transformer in a for comprehension they must being of the same type.
  */
class FreeMonad {

  val helloFuture = Future {
    " hello"
  }

  val monadFuture = Future {
    if (new Random().nextBoolean()) {
      Some(" monad")
    } else {
      None
    }
  }

  val worldFuture = Future {
    " world"
  }

  @Test
  def customMonad(): Unit = {
    val value: Id[String] = getFutureValue
    println(value)
    val value1: Id[String] = getFutureValuePipeline
    println(value1)
  }

  private def getFutureValue = {
    (for {
      world <- FutureValue(worldFuture)
      monad <- FutureOptionValueConcat(monadFuture, world)
      hello <- FutureValueConcat(helloFuture, monad)
    } yield hello).foldMap(interpreter)
  }

  private def getFutureValuePipeline = {
    FutureValue(worldFuture)
      .ConcatFutureOption(monadFuture)
      .ConcatFuture(helloFuture)
      .runPipeline
  }

  type ActionMonad[A] = Free[Action, A]

  def FutureValue(action: Future[String]): ActionMonad[String] = {
    liftF[Action, String](ResolveFuture(action))
  }

  def FutureValueConcat(action: Future[String], value: String): ActionMonad[String] = {
    liftF[Action, String](ResolveFutureAndConcat(action, value))
  }

  def FutureOptionValueConcat(action: Future[Option[String]], value: String): ActionMonad[String] = {
    liftF[Action, String](ResolveFutureOptionAndConcat(action, value))
  }

  implicit class customFree(free: Free[Action, String]) {

    def ConcatFuture(action: Future[String]): ActionMonad[String] = {
      free.flatMap(value => liftF[Action, String](ResolveFutureAndConcat(action, value)))
    }

    def ConcatFutureOption(action: Future[Option[String]]): ActionMonad[String] = {
      free.flatMap(value => liftF[Action, String](ResolveFutureOptionAndConcat(action, value)))
    }

    def runPipeline = free.foldMap(interpreter)

  }

  /**
    * In scalaz interpreters are the milestone of the monad transformer, is the function that give the monad transformer
    * a behave, so we could reuse the same monad transformer with so many behaves as interpreters we have.
    * That even include the return type value.
    **/
  def interpreter: Action ~> Id = new (Action ~> Id) {
    def apply[A](a: Action[A]): Id[A] = a match {
      case ResolveFuture(action) => Await.result(action, Duration.create(5, TimeUnit.SECONDS))
      case ResolveFutureAndConcat(action, value) => Await.result(action, Duration.create(5, TimeUnit.SECONDS)) + value
      case ResolveFutureOptionAndConcat(action, value) =>
        val result = Await.result(action, Duration.create(5, TimeUnit.SECONDS))
        if (result.isEmpty) " end of sentence" else result.get + value
    }
  }

  type Id[+A] = A

  sealed trait Action[A]

  case class ResolveFuture(action: Future[String]) extends Action[String]

  case class ResolveFutureAndConcat(action: Future[String], value: String) extends Action[String]

  case class ResolveFutureOptionAndConcat(action: Future[Option[String]], value: String) extends Action[String]


}
