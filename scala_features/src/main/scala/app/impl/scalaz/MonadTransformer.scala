package app.impl.scalaz

import java.util.concurrent.TimeUnit

import org.junit.Test

import scala.concurrent.Await._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration._
import scala.concurrent.{Await, Future}

/**
  * Created by pabloperezgarcia on 24/10/2017.
  *
  * Since for comprehension basically flatMap the monad that you pass, in case you need a lift or double
  * lift with flatMap with some side effects sometimes you can use Monad transformers.
  *
  */
class MonadTransformer {

  /**
    * With this Monad transform we can map and flatMap the futures in order to use just the values inside the
    * futures in our pipeline
    */
  case class FutureMonad[A](future: Future[A]) {

    def map[B](f: A => B): FutureMonad[B] = FutureMonad(future.map(value => f(value)))

    def flatMap[B](f: A => FutureMonad[B]): FutureMonad[B] = FutureMonad(future.flatMap(value => {
      val futureMonad = f(value)
      futureMonad.future
    }))

  }

  @Test
  def futureMonad = {
    val sentence = (for {
      word <- FutureMonad(hello)
      word1 <- FutureMonad(custom(word))
      sentence <- FutureMonad(world(word1))
      sentenceUpper <- FutureMonad(upper(sentence))
    } yield sentenceUpper).future
    println(result(sentence, create(10, TimeUnit.SECONDS)))
  }

  def hello = Future {
    "hello"
  }

  def custom(word: String) = Future {
    word.concat(" monad")
  }

  def world(word: String) = Future {
    word.concat(" transform world!")
  }

  def upper(sentence: String) = Future {
    sentence.toUpperCase
  }


  /**
    * This monad transformer receive a Future of Option in his constructor and implement
    * map to transform the value of the monad, and flatMap to get the value of the option.
    *
    */
  case class FutOpt[A](future: Future[Option[A]]) {

    def map[B](f: A => B): FutOpt[B] = {
      FutOpt(future.map(option => option.map(value => f(value)))
        .recoverWith {
          case e: Exception =>
            Future.successful(Option.empty)
        })
    }

    def flatMap[B](f: A => FutOpt[B]): FutOpt[B] =
      FutOpt(future.flatMap(option => option match {
        case Some(a) =>
          val futOpt = f(a)
          futOpt.future
        case None => Future.successful(None)
      }))
  }

  def findAddressByUserName(name: String): Future[Option[String]] =
    (for {
      user <- FutOpt(findUserByName(name))
      street <- FutOpt(findAddressByUser(user))
      address <- FutOpt(findNumberOfAddress(street))
    } yield address).future

  def findAddressByUserNameNoSugar(name: String): Future[Option[String]] = {
    FutOpt(findUserByName(name))
      .flatMap(user => FutOpt(findAddressByUser(user)))
      .flatMap(street => FutOpt(findNumberOfAddress(street))).future
  }

  @Test
  def main(): Unit = {
    val result = Await.result(findAddressByUserName("Paul"), create(10, TimeUnit.SECONDS))
    println(result.get)
    val result1 = Await.result(findAddressByUserNameNoSugar("Johny"), create(10, TimeUnit.SECONDS))
    println(result1.get)
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
