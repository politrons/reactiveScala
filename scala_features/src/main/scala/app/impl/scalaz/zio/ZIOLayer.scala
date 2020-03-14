package app.impl.scalaz.zio

import java.util.UUID

import org.junit.Test
import zio.{Has, IO, UIO, ZIO, ZLayer}

class ZIOLayer {

  // ADT
  // ---------
  case class BasketId(uuid: String)

  case class Basket(id: BasketId, products: List[Product])

  case class Product(name: String, price: Long)

  case class BasketError(message: String)

  var baskets: Map[BasketId, Basket] = Map()

  val runtime: zio.Runtime[zio.ZEnv] = zio.Runtime.default

  /**
    * ZLayer
    * ---------
    * ZLayer is a recipe type that receive an RInput and return a ROut, with the possible effect or E
    *
    * We can also combine ZLayer to be provided as Env arg for a ZIO program using ++
    *
    * Using this pattern we can create multiple behavior of our program, and depending which
    * implementation of ZLayer we provide, the program it will behave in one way or another.
    *
    * In this example we provide two execution of the same program, passing two different ZLayer as Env argument.
    * With this pattern using [ZLayer] and [Has] we are able to create a DSL [Program] and passing different ZLayer
    * we provide different behaviors.
    */
  @Test
  def featureZLayer(): Unit = {
    val stringDependency: ZLayer[Any, Any, Has[String]] = ZLayer.succeed("This is a simple layer")
    val value100: ZLayer[Any, Any, Has[Long]] = ZLayer.succeed(100)
    val value1000: ZLayer[Any, Any, Has[Long]] = ZLayer.succeed(1000)

    val _: ZLayer[Any with Long, Any, Has[String] with Has[Long]] = stringDependency ++ value1000

    def getNumberProcess: ZIO[Has[Long], Nothing, Long] = ZIO.accessM(has => ZIO.succeed(has.get))

    val program: ZIO[Has[Long], Nothing, Unit] = for {
      number <- getNumberProcess
      _ <- ZIO.succeed(println(s"Number processed by environment: $number"))
    } yield ()

    runtime.unsafeRun(program.provideSomeLayer(value100))
    runtime.unsafeRun(program.provideCustomLayer(value1000))

  }

  /**
    * Module object that keeps all logic, and only expose a Service with the functions to be used.
    * Internally those functions implementations it will use the Env argument provided into the program
    * so basically we can change the implementation of the service if we have more than one [ZLayer]
    * implemented in the Module.
    * In this case we just implement [basketDependencies], we can implement so many as we want and being used by our
    * DSL defined as Structure
    */
  object BasketModule {

    trait Service {
      def getBasket(basketId: BasketId): ZIO[Any, BasketError, Basket]

      def createBasket(product: Product): IO[BasketError, Basket]
    }

    /**
      * Behavior of our module
      * -----------------------
      * Just like Type class pattern or Free monad, we can have multiple implementations of the Service, defined as [ZLayer]
      * We create a service dependency using [ZLayer] which provide a factory to encapsulate the implementation
      * defined before, so then it will be able to be used as Env argument provided in a ZIO program, as dependency.
      *
      * Has[A] represent a dependency of type A. We can combine multiple dependencies using ++ (Has[A] ++ Has[B]) -> Has[A] with Has[B]
      */
    val basketDependencies: ZLayer[Any, Nothing, Has[BasketModule.Service]] = ZLayer.succeed(new Service {
      override def getBasket(basketId: BasketId): ZIO[Any, BasketError, Basket] = {

        ZIO.effect {
          baskets.find(entry => entry._1 == basketId) match {
            case Some(tuple) => tuple._2
            case None => throw new NullPointerException
          }

        }.catchAll(t => ZIO.fail(BasketError(t.getMessage)))
      }

      override def createBasket(product: Product): IO[BasketError, Basket] =
        ZIO.effect(Basket(BasketId(UUID.randomUUID().toString), List(product))).catchAll(t => ZIO.fail(BasketError(t.getMessage)))
    })

    /**
      * Structure of our module
      * -------------------------
      * Access method of module. Since we use return type ZIO[Env, E, A] the compiler can infer
      * when we use [ZIO.accessM], which it will get the function argument of the Env defined in the type.
      * So we can use it, to extract that Env value and transform the value into A
      * Here as Env we pass the service we expose so we can then invoke internally his use.
      *
      * @return
      */
    def findBasketById(basketId: BasketId): ZIO[Has[BasketModule.Service], BasketError, Basket] =
      ZIO.accessM(_.get.getBasket(basketId))

    def createNewBasket(product: Product): ZIO[Has[BasketModule.Service], BasketError, Basket] =
      ZIO.accessM(_.get.createBasket(product))
  }

  object Logging {

    trait Service {
      def info(s: String): UIO[Unit]

      def error(s: String): UIO[Unit]
    }


    import zio.console.Console

    val loggerDependencies: ZLayer[Console, Nothing, Has[Logging.Service]] = ZLayer.fromFunction(console =>
      new Service {
        def info(s: String): UIO[Unit] = console.get.putStrLn(s"info - $s")

        def error(s: String): UIO[Unit] = console.get.putStrLn(s"error - $s")
      }
    )

    //accessor methods
    def info(s: String): ZIO[Has[Logging.Service], Nothing, Unit] =
      ZIO.accessM(_.get.info(s))

    def error(s: String): ZIO[Has[Logging.Service], Nothing, Unit] =
      ZIO.accessM(_.get.error(s))
  }

  @Test
  def runBasketProgram(): Unit = {

    val createBasket: ZIO[Has[Logging.Service] with Has[BasketModule.Service], BasketError, Unit] = for {
      _ <- Logging.info(s"Creating basket") // ZIO[Logging, Nothing, Unit]
      basket <- BasketModule.createNewBasket(Product("coca-cole", 2)) // ZIO[UserRepo, DBError, Unit]
      _ <- Logging.info(s"Basket created $basket") // ZIO[Logging, Nothing, Unit]
    } yield ()

    val programDependencies = BasketModule.basketDependencies ++ Logging.loggerDependencies

    val program: ZIO[zio.ZEnv, Any, Unit] = createBasket.provideCustomLayer(programDependencies)

    runtime.unsafeRun(program)

  }

}
