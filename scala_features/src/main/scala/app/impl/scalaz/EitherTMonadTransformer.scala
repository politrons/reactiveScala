

import java.util.concurrent.TimeUnit

import org.junit.Test

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}
import scala.util.Try
import scalaz.std.scalaFuture._
import scalaz.{EitherT, \/}

/**
  * Created by pabloperezgarcia on 15/10/2017.
  *
  * EitherT is a monad transformer that allow you to nest in the Combination of monad Future[\/[Left,Right]]
  * Using for comprehension or just using flatMap in the pipeline we can receive the right value of the either
  * and dont have to worry of the side effects of the right/left
  */
class EitherTMonadTransformer {

  case class Error(msg: String)

  case class User(username: String, email: String)

  def authenticate(token: String): Future[Error \/ String] =
    Future {
      if (token == "good") {
        \/.right[Error, String]("paul")
      } else if (token == "partial") {
        \/.right[Error, String]("unknown")
      } else {
        \/.left[Error, String](Error("wrong"))
      }
    }

  def getUser(username: String): Future[Error \/ User] =
    Future {
      if (username == "paul") {
        \/.right[Error, User](User("paul", "osmosis_paul@gmail.com"))
      } else {
        \/.left[Error, User](Error("Wrong username"))
      }
    }

  def putNameInUpperCase(name: String, f: String => String): Future[Error \/ String] =
    Future {
      val result = Try(f.apply(name))
      if (result.isSuccess) {
        \/.right[Error, String](result.get)
      } else {
        \/.left[Error, String](Error(result.failed.get.toString))
      }
    }

  def getEmailById(id: String): Future[\/[Error, String]] =
    (for {
      username <- EitherT(authenticate(id))
      user <- EitherT(getUser(username))
    } yield user.email).run

  def getNameAndPutInUppercase(username: String): Future[\/[Error, String]] =
    (for {
      usernameInUpperCase <- EitherT(putNameInUpperCase(username, username => username.toUpperCase()))
    } yield usernameInUpperCase).run

  @Test
  def eitherT(): Unit = {
    val result = Await.result(getEmailById("good"), Duration.create(10, TimeUnit.SECONDS))
    //All good
    println(result)
    val result1 = Await.result(getEmailById("bad"), Duration.create(10, TimeUnit.SECONDS))
    //side effect in first future
    println(result1)
    val result2 = Await.result(getEmailById("partial"), Duration.create(10, TimeUnit.SECONDS))
    //side effect in the second future
    println(result2)
  }

  @Test
  def eitherTWithFunction(): Unit = {
    val result = Await.result(getNameAndPutInUppercase("Paul"), Duration.create(10, TimeUnit.SECONDS))
    //All good
    println(result)
    val result1 = Await.result(getNameAndPutInUppercase(null), Duration.create(10, TimeUnit.SECONDS))
    //side effect in toUppercase method
    println(result1)
  }


}
