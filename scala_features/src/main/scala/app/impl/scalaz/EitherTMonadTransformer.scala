

import org.junit.Test

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
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

  def authenticate(token: String): Future[Error \/ String] = Future {
    if (token == "good") {
      \/.right[Error, String]("paul")
    } else if (token == "partial") {
      \/.right[Error, String]("unknown")
    } else {
      \/.left[Error, String](Error("wrong"))
    }
  }

  def getUser(username: String): Future[Error \/ User] = Future {
    if (username == "paul") {
      \/.right[Error, User](User("paul", "osmosis_paul@gmail.com"))
    } else {
      \/.left[Error, User](Error("Wrong username"))
    }
  }

  def getEmailById(id: String): Future[\/[Error, String]] =
    (for {
      username <- EitherT(authenticate(id))
      user <- EitherT(getUser(username))
    } yield user.email).run

  @Test
  def eitherT(): Unit = {
    println(getEmailById("good"))//All good
    println(getEmailById("bad"))//It will fail the first future
    println(getEmailById("partial"))//It will fail the second future
  }


}
