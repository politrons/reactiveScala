package app.impl.zio

import java.util.UUID

import org.junit.Test
import zio._

/**
  * ZLayer is a recipe type that receive an RInput and return a ROut, with the possible effect or E
  */
class ZIOLayer {

  val runtime: zio.Runtime[zio.ZEnv] = zio.Runtime.default

  /**
    * Structures/DSL
    * ---------------
    * This patter is quite similar to type classes, we have an trait and multiple implementation, and depending which one
    * we pass as Env arg to the program we will have one behavior or another.
    */
  trait CalcNumber {
    def multiplyBy(number: Long): UIO[Long]
  }

  def getNumberProcess(number: Long): ZIO[Has[CalcNumber], Nothing, Long] = ZIO.accessM(_.get.multiplyBy(number))

  /**
    * Behaviors
    * ---------
    * Using this pattern we can create multiple behavior of our program, and depending which
    * implementation of ZLayer we provide to the program when we run it, the program it will behave in one way or another.
    */
  val numberMultiply100: ZLayer[Any, Nothing, Has[CalcNumber]] = ZLayer.succeed(new CalcNumber {
    override def multiplyBy(number: Long): UIO[Long] = ZIO.succeed(number * 100)
  })

  val numberMultiply1000: ZLayer[Any, Nothing, Has[CalcNumber]] = ZLayer.succeed(new CalcNumber {
    override def multiplyBy(number: Long): UIO[Long] = ZIO.succeed(number * 1000)
  })

  val stringValue: ZLayer[Any, Any, Has[String]] = ZLayer.succeed("This is a simple layer")

  /**
    * Using ++ operator we can combine multiple layers to have multi dependency to be passed as Env argument.
    */
  val multipleZLayer: ZLayer[Any, Any, Has[CalcNumber] with Has[String]] = numberMultiply100 ++ stringValue

  /**
    * In this example we provide two execution of the same program, passing two different ZLayer as Env argument
    * [numberMultiply100] and [numberMultiply1000].
    * With this pattern using [ZLayer] and [Has] we are able to create a DSL [Program] and passing different ZLayer
    * we provide different behaviors.
    */
  @Test
  def programWithOneZLayer(): Unit = {
    // Unique program definition
    // --------------------------
    val program: ZIO[Has[CalcNumber], Nothing, Unit] = for {
      number <- getNumberProcess(10)
      _ <- ZIO.succeed(println(s"Number processed by environment: $number"))
    } yield ()

    // Multiple program executions behaviors
    // --------------------------------------
    runtime.unsafeRun(program.provideSomeLayer(numberMultiply100))
    runtime.unsafeRun(program.provideCustomLayer(numberMultiply1000))
  }

  /**
    * In this example we provide a program with multi Has[A] layers in the Env type.
    * Having this we can use this two Has layers in our program using [ZIO.accessM]
    */
  @Test
  def programWithMultipleZLayer(): Unit = {
    // Unique program definition
    // --------------------------
    val program: ZIO[Has[CalcNumber] with Has[String], Any, Unit] = for {
      value <- ZIO.accessM[Has[String]](has => ZIO.succeed(has.get))
      _ <- ZIO.succeed(println(s"Value extracted from environment: $value"))
      number <- getNumberProcess(1981)
      _ <- ZIO.succeed(println(s"Number processed by environment: $number"))
    } yield ()

    // Multiple program executions behaviors
    // --------------------------------------
    runtime.unsafeRun(program.provideCustomLayer(multipleZLayer))
  }

  /**
    * Multi module Basket ZLayer example
    * ----------------------------------
    */
  case class BasketId(uuid: String)

  case class Basket(id: BasketId, products: List[Product])

  case class Product(name: String, price: Long)

  case class BasketError(message: String)

  var baskets: Map[BasketId, Basket] = Map()

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

      def createBasket(product: Product): IO[BasketError, BasketId]
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

      override def createBasket(product: Product): IO[BasketError, BasketId] =
        ZIO.effect {
          val basket = Basket(BasketId(UUID.randomUUID().toString), List(product))
          baskets = baskets ++ Map(basket.id -> basket)
          basket.id
        }.catchAll(t => ZIO.fail(BasketError(t.getMessage)))
    })

    /**
      * Structure of our module
      * -------------------------
      * Access method of module. Since we use return type ZIO[Env, E, A] the compiler can infer
      * when we use [ZIO.accessM], which it will get the function argument of the Env defined in the type.
      * So we can use it, to extract that Env value and transform the value into A
      * Here as Env we pass the service we expose so we can then invoke internally his use.
      */
    def findBasketById(basketId: BasketId): ZIO[Has[BasketModule.Service], BasketError, Basket] = {
      ZIO.accessM(_.get.getBasket(basketId))
    }

    def createNewBasket(product: Product): ZIO[Has[BasketModule.Service], BasketError, BasketId] = {
      ZIO.accessM(_.get.createBasket(product))
    }
  }

  /**
    * Product module where we encapsulate the logic of Products
    */
  object ProductModule {

    trait Service {
      def createProduct(name: String, price: Long): UIO[Product]
    }

    /**
      * Behavior of our module
      * -----------------------
      * We define the logic of the [Product service] like if a type class is.
      * Once we have the [ZLayer it can be used to be passed as Env type to a program to provide behavior]
      */
    val productDependencies: ZLayer[Any, Nothing, Has[ProductModule.Service]] = ZLayer.succeed(new Service {
      override def createProduct(name: String, price: Long): UIO[Product] = ZIO.succeed(Product(name, price))
    })

    /**
      * Structure of our module
      * -----------------------
      * The DSL of Product module. Using the Env Has[A] invoke the implementation for that environment
      */
    def createProduct(name: String, price: Long): ZIO[Has[ProductModule.Service], Nothing, Product] =
      ZIO.accessM(_.get.createProduct(name, price))
  }

  @Test
  def runBasketProgram(): Unit = {
    val createBasket: ZIO[Has[ProductModule.Service] with Has[BasketModule.Service], BasketError, Unit] = for {
      product <- ProductModule.createProduct(s"Coke-cola", 100)
      _ <- ZIO.succeed(println(s"Product created $product"))
      basketId <- BasketModule.createNewBasket(product)
      _ <- ZIO.succeed(println(s"Basket created with id:$basketId"))
      basket <- BasketModule.findBasketById(basketId)
      _ <- ZIO.succeed(println(s"Basket found $basket"))
    } yield ()

    val programDependencies: ZLayer[Any, Any, Has[BasketModule.Service] with Has[ProductModule.Service]] =
      BasketModule.basketDependencies ++ ProductModule.productDependencies

    val program: ZIO[zio.ZEnv, Any, Unit] = createBasket.provideCustomLayer(programDependencies)

    runtime.unsafeRun(program)

  }

}
