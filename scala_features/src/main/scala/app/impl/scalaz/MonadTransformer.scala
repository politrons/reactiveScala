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
  */
class MonadTransformer {

  val future = Future {
    "hello"
  }

  @Test
  def customMonad(): Unit = {
    val value: Id[Any] = getFutureValue
    println(value)
  }

  private def getFutureValue = {
    (for {
      value <- FutureValue(future)
    } yield value).foldMap(interpreter)
  }

  type Id[+A] = A

  sealed trait Action[A]

  case class _Action(action: Future[Any]) extends Action[Any]

  type ActionMonad[A] = Free[Action, A]

  def FutureValue(action: Future[Any]): ActionMonad[Any] = {
    liftF[Action, Any](_Action(action))
  }

  def interpreter: Action ~> Id = new (Action ~> Id) {
    def apply[A](a: Action[A]): Id[A] = a match {
      case _Action(action) => Await.result(action, Duration.create(5, TimeUnit.SECONDS))
    }
  }

}
