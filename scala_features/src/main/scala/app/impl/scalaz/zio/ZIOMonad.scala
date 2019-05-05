package app.impl.scalaz.zio

import org.junit.Test
import scalaz.zio.{DefaultRuntime, IO, Task, ZIO}

import scala.concurrent.Future
import scala.util.Try

class ZIOMonad {

  val main: DefaultRuntime = new DefaultRuntime {}

  //##########################//
  //         CREATION         //
  //##########################//

  /**
    * Using ZIO we can define like the monad IO from Haskell, the monad by definition it's lazy,
    * which means it respect the monad laws and it's referential transparency. Only when it's evaluated
    * using [runtime.unsafeRun(monad)] it's been we move from the pure functional realm and we go into the
    * effect world.
    * In order to control effects this monad has 3 types defined.
    * * R - Environment Type. This is the type of environment required by the effect. An effect that
    * * has no requirements can use Any for the type parameter.
    * * E - Failure Type. This is the type of value the effect may fail with. Some applications will
    * * use Throwable. A type of Nothing indicates the effect cannot fail.
    * * A - Success Type. This is the type of value the effect may succeed with. This type parameter
    * * will depend on the specific effect, but Unit can be used for effects that do not produce any useful information, while Nothing can be used for effects that run forever.
    */
  @Test
  def fromZIOSucceed(): Unit = {
    val sentenceMonad: ZIO[Any, Throwable, String] =
      ZIO
        .succeed("Hello pure functional wold")
        .map(value => value.toUpperCase())
        .flatMap(word => ZIO.succeed(word + "!!!"))

    val succeedSentence = main.unsafeRun(sentenceMonad)
    println(succeedSentence)
  }

  /**
    * Using Task monad, we can no control effects since only contemplate one possible type.
    */
  @Test
  def fromTaskSucceed(): Unit = {
    val task: Task[String] =
      Task
        .succeed("Hello pure functional ")
        .flatMap(sentence => Task.succeed(sentence + " world"))
        .map(sentence => sentence.toUpperCase())

    val taskSentence = main.unsafeRun(task)
    println(taskSentence)
  }

  /**
    * We describe before that using IO monad the evaluation of the monad it's lazy. And it's true
    * but what it's not lazy evaluation it's the input value of the monad, in order to do have also
    * a lazy evaluation of that value, you need to use operator [succeedLazy] it will behave just like
    * Observable.defer of RxJava
    */
  @Test
  def fromZIOLazy(): Unit = {
    val lazyIputMonad = ZIO.succeedLazy(s"Monad___ ${System.currentTimeMillis()}")
    Thread.sleep(1000)
    println("No Monad " + System.currentTimeMillis())
    val lazySentence = main.unsafeRun(lazyIputMonad)
    println(lazySentence)
  }

  /**
    * Also we can create a monad IO with a failure that some errors were not
    * controlled properly
    */
  @Test
  def fromZIOFailure(): Unit = {
    val errorMonad: IO[String, Throwable] =
      ZIO.fail("Hello pure functional wold")
    main.unsafeRun(errorMonad)
  }

  /**
    * Also we can create a monad IO with a throwable propagation, expressions that some errors were not
    * controlled properly
    */
  @Test
  def fromZIOThrowable(): Unit = {
    val errorMonad1: IO[Exception, Nothing] =
      ZIO.fail(new IllegalArgumentException("Hello pure functional wold"))

    main.unsafeRun(errorMonad1)
  }

  //##########################//
  //         EFFECTS          //
  //##########################//

  /**
    * In case we want to create a IO from an option effect, we use [fromOption] operator.
    * In order to get the effect to None we use the operator [catchAll] which in case
    * of None it will invoke the function with unit type.
    */
  @Test
  def optionZIO(): Unit = {
    val monadSome: ZIO[Any, Unit, String] =
      ZIO.fromOption(Some("Hello"))
        .catchAll(unit => ZIO.succeed(s"No data in Option"))
        .flatMap(word => ZIO.succeed(word + " maybe"))
        .map(sentence => sentence.toUpperCase)
    val value = main.unsafeRun(monadSome)
    println(value)

    val monadNone =
      ZIO.fromOption(maybe())
        .catchAll(unit => ZIO.succeed(s"No data in Option"))
        .map(sentence => sentence.toUpperCase)

    val none = main.unsafeRun(monadNone)
    println(none)
  }

  /**
    * In case we want to create a IO from an Try effect, we use [fromTry] operator.
    * In order to get the effect to Value or Throwable we use the operator [catchAll] which in case
    * of Try Failure it will invoke the function with the Throwable type.
    */
  @Test
  def tryZIO(): Unit = {
    val trySuccess =
      ZIO.fromTry(Try(1981))
        .map(number => number + 100)
        .catchAll(t => ZIO.succeed(s"I catch an error from Try $t"))
    val goodValue = main.unsafeRun(trySuccess)
    println(goodValue)

    val tryError =
      ZIO.fromTry(Try(42 / 0))
        .map(number => number + 100)
        .catchAll(t => ZIO.succeed(s"I catch an error from Try $t"))
    val value = main.unsafeRun(tryError)
    println(value)
  }

  /**
    * In case we want to create a IO from an Either effect, we use [fromEither] operator.
    * In order to get the effect of Left or Right type, we use the operator [catchAll] which in case
    * of Either Left it will invoke the function with the Left type defined in Either.
    */
  @Test
  def eitherZIO(): Unit = {
    val rightMonad: ZIO[Any, Int, String] =
      ZIO.fromEither(rightValue())
        .map(sentence => sentence + " !!!")
        .map(sentence => sentence.toUpperCase)
        .catchAll(leftSide => ZIO.succeed(s"I catch the left side of the either $leftSide"))

    val value = main.unsafeRun(rightMonad)
    println(value)

    val leftMonad: ZIO[Any, Int, String] =
      ZIO.fromEither(eitherValue())
        .map(sentence => sentence + " !!!")
        .map(sentence => sentence.toUpperCase)
        .catchAll(leftSide => ZIO.succeed(s"I catch the left side of the either $leftSide"))

    val leftValue = main.unsafeRun(leftMonad)
    println(leftValue)
  }

  /**
    * In case we want to create a IO from a Future execution, we use [fromFuture] operator.
    * ZIO provide the option to zip the futures and just like Scala future zip it return a Tuple
    * of the results.
    */
  @Test
  def futureZIO(): Unit = {
    val monadFuture: Task[String] =
      ZIO.fromFuture { implicit ec =>
        Future("I'll see you in the future")
      }
        .zip(ZIO.fromFuture({ implicit ec =>
          Future(", for sure!")
        }))
        .map(sentence => (sentence._1 + sentence._2).toUpperCase)

    val value = main.unsafeRun(monadFuture)
    println(value)
  }

  //##########################//
  //         PROGRAM          //
  //##########################//

  /**
    * Main program that compose all ZIO monads created with effects.
    * To be more Haskell style we use for comprehension emulating the
    * [do block] style of Haskell
    */
  @Test
  def mainProgramWithEffects(): Unit = {
    val userMonad = for {
      user <- login("politrons")
      user <- updateAge(user)
      user <- findUserAsEmployee(user)
      user <- persistEmployeeInDataBase(user)
    } yield user

    val value = main.unsafeRun(userMonad)
    println(value)
  }

  def login(username: String): ZIO[Any, Throwable, User] =
    ZIO.fromOption(Some(User("politrons", 38)))
      .catchAll(unit => ZIO.succeed(User("No user", 0)))

  def updateAge(user: User): ZIO[Any, Throwable, User] =
    ZIO.fromTry(Try(user.copy(age = user.age + 1)))

  def findUserAsEmployee(user: User): ZIO[Any, Throwable, User] =
    ZIO.fromEither(Right(user))

  def persistEmployeeInDataBase(user: User): ZIO[Any, Throwable, User] =
    ZIO.fromFuture {
      implicit ec => Future(user)
    }


  def maybe(): Option[String] = None

  def eitherValue(): Either[Int, String] = Left(100)

  def rightValue(): Either[Int, String] = Right("This monad was Success!")

  case class User(name: String, age: Int)

  //##########################//
  //         ENVIRONMENT      //
  //##########################//

  /**
    * Just like Monad Reader, we can use a environment type, that can be used internally in your program.
    * This is really handy when you want to have different values per environment, or you just want
    * to apply Module pattern to make your DSL completely abstract of the implementation passed per environment.
    *
    * You just need to access to the Environment arguments using [access] operator, which it's
    * a function that pass the environment type, so you can access to the functions that the env type provide.
    *
    * Once you evaluate your program you need to pass the implementation of the environment using [provide] operator.
    */
  @Test
  def environmentType(): Unit = {

    val userMonad: ZIO[User, Nothing, String] =
      for {
        name <- ZIO.access[User](env => env.name)
        age <- ZIO.access[User](_.age)
      } yield s"Name: $name, age: $age"

    val info = main.unsafeRun(userMonad.provide(User("Politrons", 38)))
    println(info)

  }

  /**
    * Using the environment we're able to use a pipeline, extracting the monads from environment type
    * allowing polymorphism in the implementation of that environment.
    *
    * Here [CustomLogic] it's a trait that have two implementations, one for DEV environment and another
    * for production environment. Using the [accessM] operator we can extract the value from the monad
    * from the implementations of the trait function, in PROD env it will return something different from
    * the DEV env, but our for comprehension remain the same without have to being refactor.
    */
  @Test
  def environmentModulePattern(): Unit = {

    val userMonad: ZIO[CustomLogic, Nothing, String] =
      for {
        server <- ZIO.accessM[CustomLogic](env => env.getAddress)
        port <- ZIO.accessM[CustomLogic](_.getPort)
      } yield s"Server: $server, port: $port"

    val devInfo = main.unsafeRun(userMonad.provide(CustomDevLogic))
    println(devInfo)

    val realInfo = main.unsafeRun(userMonad.provide(CustomLiveLogic))
    println(realInfo)

  }

  trait CustomLogic {
    def getAddress: ZIO[Any, Nothing, String]

    def getPort: ZIO[Any, Nothing, Int]
  }

  object CustomDevLogic extends CustomLogic {

    override def getAddress: ZIO[Any, Nothing, String] = ZIO.succeed("Dev socket address")

    override def getPort: ZIO[Any, Nothing, Int] = ZIO.succeed(666)
  }

  object CustomLiveLogic extends CustomLogic {

    override def getAddress: ZIO[Any, Nothing, String] = ZIO.succeed("Real socket address")

    override def getPort: ZIO[Any, Nothing, Int] = ZIO.succeed(1981)
  }


}
