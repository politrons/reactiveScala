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

  def findUserByName(name: String) = Future {
    Some(name)
  }

  def findAddressByUser(user: String) = Future {
    Some(s"Address of $user")
  }


  /**
    * This monad transformer receive a Future of Option in his constructor and implement
    * map to transform the value of the monad, and flatMap to get the value of the option.
    *
    * @param value
    * @tparam A
    */
  case class FutOpt[A](value: Future[Option[A]]) {

    def map[B](f: A => B): FutOpt[B] =
      FutOpt(value.map(optA => optA.map(f)))

    def flatMap[B](f: A => FutOpt[B]): FutOpt[B] =
      FutOpt(value.flatMap(opt => opt match {
        case Some(a) => f(a).value
        case None => Future.successful(None)
      }))
  }

  def findAddressByUserName(name: String): Future[Option[String]] =
    (for {
      user <- FutOpt(findUserByName(name))
      address <- FutOpt(findAddressByUser(user))
    } yield address).value

  @Test
  def main(): Unit ={
    val result = Await.result(findAddressByUserName("Paul"), Duration.create(10, TimeUnit.SECONDS))
    println(result.get)
  }

}
