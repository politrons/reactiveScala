package app.impl.scalaz.zio

import org.junit.Test
import scalaz.zio.{DefaultRuntime, Fiber, IO, ZIO}

import scala.concurrent.Future
import scala.util.{Failure, Random, Success, Try}

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
  //    val car1 = createCar("Audi")
  //    val car2 = createCar("BMW")
  //    val car3 = createCar("Renault")
  //    val car4 = createCar("Honda")
  //    val winnerOfRace: ZIO[Any, Throwable, String] = for {
  //      winner <- car1.race(car2).race(car3).race(car4)
  //      result <- ZIO.succeed(winner)
  //    } yield result
  //    val value = main.unsafeRun(winnerOfRace)
  //    println(value)
  //  }

  //  private def createCar(car: String): ZIO[Any, Throwable, String] =
  //    IO.fromFuture(implicit ec => Future {
  //      Thread.sleep((Math.random * 1500).toInt)
  //      println(s"$car running in ${Thread.currentThread().getName}")
  //      s" $car win!"
  //    })

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


  @Test
  def foreachZIO(): Unit = {
    val strings = main.unsafeRun((for {
      xx <- ZIO.foreach(List[String]("hello", "zio", "world"))(value => {
        ZIO.effect(value.toUpperCase)
      })

    } yield xx).catchAll(t => ZIO.succeed(List(t.getMessage))))
    println(strings)
  }

  @Test
  def foreachZIOWithError(): Unit = {
    val strings = main.unsafeRun((for {
      xx <- ZIO.foreach(List[String]("hello", null, "world"))(value => {
        ZIO.effect(value.toUpperCase)
      })

    } yield xx).catchAll(t => ZIO.succeed(List("Some effect might happens"))))
    println(strings)
  }

  @Test
  def zioWhen(): Unit = {
    main.unsafeRun((for {
      _ <- ZIO.when(true)(ZIO.effect(println("Hello if condition")))
    } yield ()).catchAll(_ => ZIO.succeed("Else condition here")))

    main.unsafeRun(for {
      _ <- ZIO.when(false)(ZIO.effect(println("Hello if condition")))
    } yield ())


    main.unsafeRun(for {
      _ <- ZIO.whenM(ZIO.effect(true))(ZIO.effect(println("Hello if effect condition")))
    } yield ())

  }

  @Test
  def zioCatchRecover: Unit = {
    val output = main.unsafeRun {
      (for {
        value <- ZIO.effect(null)
        compose <- ZIO.effect(value.toString.toUpperCase()).catchAll(_ => ZIO.succeed("Hello world"))
        upper <- ZIO.effect(compose.toUpperCase())
      } yield upper).catchAll(t => ZIO.succeed("Error error and error"))
    }
    println(output)
  }


}
