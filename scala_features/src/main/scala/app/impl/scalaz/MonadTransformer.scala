package app.impl.scalaz

import java.util.concurrent.TimeUnit

import org.junit.Test

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}
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
class MonadTransformer {

  val helloFuture = Future {
    " hello"
  }

  val monadFuture = Future {
    " monad"
  }

  val worldFuture = Future {
    " world"
  }

  @Test
  def customMonad(): Unit = {
    val value: Id[String] = getFutureValue
    println(value)
  }

  private def getFutureValue = {
    (for {
      world <- FutureValue(worldFuture)
      monad <- FutureValueConcat(monadFuture, world)
      hello <- FutureValueConcat(helloFuture, monad)
    } yield hello).foldMap(interpreter)
  }


  type ActionMonad[A] = Free[Action, A]

  def FutureValue(action: Future[String]): ActionMonad[String] = {
    liftF[Action, String](ResolveFuture(action))
  }

  def FutureValueConcat(action: Future[String], value: String): ActionMonad[String] = {
    liftF[Action, String](ResolveFutureAndConcat(action, value))
  }

  type Id[+A] = A

  sealed trait Action[A]

  case class ResolveFuture(action: Future[String]) extends Action[String]

  case class ResolveFutureAndConcat(action: Future[String], value: String) extends Action[String]

  def interpreter: Action ~> Id = new (Action ~> Id) {
    def apply[A](a: Action[A]): Id[A] = a match {
      case ResolveFuture(action) => Await.result(action, Duration.create(5, TimeUnit.SECONDS))
      case ResolveFutureAndConcat(action, value) => Await.result(action, Duration.create(5, TimeUnit.SECONDS)) + value
    }
  }

}
