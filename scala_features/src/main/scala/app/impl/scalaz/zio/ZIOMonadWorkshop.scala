package app.impl.scalaz.zio

import org.junit.Test
import scalaz.zio.{DefaultRuntime, ZIO}

import scala.util.{Failure, Success, Try}

class ZIOMonadWorkshop {

  val main: DefaultRuntime = new DefaultRuntime {}

  @Test
  def mainProgramWithEffects(): Unit = {
    val maybeUser = login("politrons") match {
      case Some(user) => updateAge(user) match {
        case Success(u) => findUserAsEmployee(u) match {
          case Right(employee) => employee.copy(status = "online")
          case Left(t) => UserError(s"Error employee not found. caused by ${t.getCause}")
        }
        case Failure(t) => UserError(s"Error updating entry time. caused by ${t.getCause}")
      }
      case None => UserError("Not username provided")
    }
    println(maybeUser)

  }

  def login(username: String): Option[User] = {
    if (username == null) None else Option(User(username, 1535234551))
  }

  def updateAge(user: User): Try[User] = Try(user.copy(time = user.time + 1))

  def findUserAsEmployee(user: User): Either[Throwable, User] = Right(user)

  def maybe(): Option[String] = None

  def eitherValue(): Either[Int, String] = Left(100)

  def rightValue(): Either[Int, String] = Right("This monad was Success!")

  case class User(name: String, time: Int, status: String = "offline")

  case class UserError(desc: String) extends Exception


  //  @Test
  //  def mainProgramWithEffects(): Unit = {
  //    val value: ZIO[Any, Nothing, Either[UserError, User]] = (for {
  //      user <- ZIO.fromOption(login(null)).catchAll(_ => ZIO.fail(UserError("Not username provided")))
  //      user1 <- ZIO.fromTry(updateAge(user))
  //      employee <- ZIO.fromEither(findUserAsEmployee(user1))
  //      employee1 <- ZIO.succeed(employee.copy(status = "online")).either
  //    } yield employee1).catchAll(t => ZIO.succeed(Left(UserError(t.getMessage))))
  //    val either = main.unsafeRun(value)
  //    println(either)
  //  }

}
