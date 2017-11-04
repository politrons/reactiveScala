package app.impl.scalaz

import java.util.concurrent.TimeUnit

import org.junit.Test

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

/**
  * Created by pabloperezgarcia on 24/10/2017.
  *
  * Since for comprehension basically flatMap the monad that you pass, in case you need a double
  * flatMap with side effects you can use Monad transformers.
  *
  */
class MonadTransformer {

  /**
    * This monad transformer receive a Future of Option in his constructor and implement
    * map to transform the value of the monad, and flatMap to get the value of the option.
    *
    */
  case class FutOpt[A](value: Future[Option[A]]) {

    def map[B](f: A => B): FutOpt[B] =
      FutOpt(value.map(optA => optA.map(f)))

    def flatMap[B](f: A => FutOpt[B]): FutOpt[B] =
      FutOpt(value.flatMap {
        case Some(a) => f(a).value
        case None => Future.successful(None)
      })
  }

  def findAddressByUserName(name: String): Future[Option[String]] =
    (for {
      user <- FutOpt(findUserByName(name))
      street <- FutOpt(findAddressByUser(user))
      address <- FutOpt(findNumberOfAddress(street))
    } yield address).value

  @Test
  def main(): Unit = {
    val result = Await.result(findAddressByUserName("Paul"), Duration.create(10, TimeUnit.SECONDS))
    println(result.get)
  }

  def findUserByName(name: String) = Future {
    Some(name)
  }

  def findAddressByUser(user: String) = Future {
    Some(s"address of $user is Zenon")
  }

  def findNumberOfAddress(address: String) = Future {
    Some(s"$address and number 2")
  }


}
