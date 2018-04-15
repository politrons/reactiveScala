package app.impl.tagless

import java.util.concurrent.TimeUnit

import org.junit.Test

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}
import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Pablo Perez Garcia https://github.com/politrons
  *
  * In this new code example I explain in code how Tagless final works and how having some Algebras, bridges and
  * Interpreters we can have this Free structure code reusable for multiple types.
  */
class TagLessFeature {

  // ################
  // #   Algebras   #
  // ################


  /**
    * Here we define the Algebras or actions that our DSL will be able to use.
    * It can be consider as interface that our interpreters need to implement.
    * This Algebras are meant to be used only by the bridges which you can see in
    * the next section, which are the code that the consumer will use as DSL.
    */
  trait MyDSL[Action[_]] {

    def number(v: Int): Action[Int]

    def add(a: Action[Int], b: Action[Int]): Action[Int]

    def multiply(a: Action[Int], b: Action[Int]): Action[Int]

    def text(v: String): Action[String]

    def concat(a: Action[String], b: Action[String]): Action[String]

  }

  // ################
  // #   Bridges    #
  // ################
  /**
    * Bridges aka DSL is the Domain specific Language that our consumers will use.
    * Bridges use Scala Types class pattern to be a bridge between the value passed and how the implementation defined
    * in the Algebra is using it.
    * As Type class pattern provide, depending the type[Action] passed in the implicit, the implementation
    * can be one or another.
    * The implementation it will ve provided by the interpreters in the code below.
    */
  trait MyBridge[ScalaValue] {
    def ~>[Action[_]](implicit interpreter: MyDSL[Action]): Action[ScalaValue]
  }

  def createNumber(a: Int) = new MyBridge[Int] {
    override def ~>[Action[_]](implicit interpreter: MyDSL[Action]): Action[Int] = {
      interpreter.number(a)
    }
  }

  def sumNumbers(a: Int, b: Int) = new MyBridge[Int] {
    override def ~>[Action[_]](implicit interpreter: MyDSL[Action]): Action[Int] = {
      interpreter.add(interpreter.number(a), interpreter.number(b))
    }
  }

  def multiplyNumbers(a: Int, b: Int) = new MyBridge[Int] {
    override def ~>[Action[_]](implicit interpreter: MyDSL[Action]): Action[Int] = {
      interpreter.multiply(interpreter.number(a), interpreter.number(b))
    }
  }

  def createText(text: String) = new MyBridge[String] {
    override def ~>[Action[_]](implicit interpreter: MyDSL[Action]): Action[String] = {
      interpreter.text(text)
    }
  }

  def concatText(str: String, str1: String) = new MyBridge[String] {
    override def ~>[Action[_]](implicit interpreter: MyDSL[Action]): Action[String] = {
      interpreter.concat(interpreter.text(str), interpreter.text(str1))
    }
  }

  /**
    * We can also use implicit for sugar syntax to glue multiple bridges as pipelines.
    */
  implicit class customBridge(bridge: MyBridge[Int]) {

    def sumNumber(number: Int) = new MyBridge[Int] {
      override def ~>[Action[_]](implicit interpreter: MyDSL[Action]): Action[Int] = {
        interpreter.add(interpreter.number(number), bridge.~>)
      }
    }

    def multiplyNumber(number: Int) = new MyBridge[Int] {
      override def ~>[Action[_]](implicit interpreter: MyDSL[Action]): Action[Int] = {
        interpreter.add(interpreter.number(number), bridge.~>)
      }
    }
  }

  // ####################
  // #   Interpreters   #
  // ####################
  /**
    * The interpreter implement the DSL/Algebras to give an implementation with the values that we receive.
    * As you can see here one of the biggest benefits of use Tagless or Free are that we can have multiples
    * implementations which are really easy to plug and play as you can see in the execution of the examples,
    * and the different implementations that we have for the same DSL.
    *
    * Basically having the DSL now with the interpreters we can define the output type that we want for the inputs
    * received from the DSL.
    * In this examples we can see how using the same MyDSL[Type] we can have multiple Types of outputs as
    * Scala types, Option[Type], Future[Type], Future[Either[Throwable,Type]
    */

  /**
    * This first interpreter does not being wrapped by any monad and is just scala values.
    */
  type Id[ScalaValue] = ScalaValue

  val interpret = new MyDSL[Id] {

    override def number(v: Int): Id[Int] = v

    override def add(a: Id[Int], b: Id[Int]): Id[Int] = a + b

    override def multiply(a: Id[Int], b: Id[Int]): Id[Int] = a * b

    override def text(v: String): Id[String] = v

    override def concat(a: Id[String], b: Id[String]): Id[String] = a + " " + b

  }

  /**
    * For this interpreter we define that we wrap the scala value into an Option.
    * So now in our Algebras the Action it will be [Option]
    */
  type MyOption[ScalaValue] = Option[ScalaValue]

  val interpretOption = new MyDSL[MyOption] {

    override def number(a: Int): MyOption[Int] = Some(a)

    override def add(a: MyOption[Int], b: MyOption[Int]): MyOption[Int] = {
      a.flatMap(value => b.map(value2 => value + value2))
    }

    override def text(str: String): MyOption[String] = Some(str)

    override def concat(str: MyOption[String], str1: MyOption[String]): MyOption[String] = {
      str.flatMap(value => str1.map(value2 => value + value2))
    }

    override def multiply(a: MyOption[Int], b: MyOption[Int]): MyOption[Int] = {
      a.flatMap(value => b.map(value2 => value * value2))
    }
  }

  /**
    * For this interpreter we define that we wrap the scala value into a Future.
    * So now in our Algebras the Action it will be [Future]
    */
  type MyFuture[ScalaValue] = Future[ScalaValue]

  val interpretFuture = new MyDSL[MyFuture] {

    override def number(a: Int): MyFuture[Int] = Future(a)

    override def add(a: MyFuture[Int], b: MyFuture[Int]): MyFuture[Int] = {
      a.flatMap(value => b.map(value2 => value + value2))
    }

    override def text(str: String): MyFuture[String] = Future(str)

    override def concat(str: MyFuture[String], str1: MyFuture[String]): MyFuture[String] = {
      str.flatMap(value => str1.map(value2 => value + value2))
    }

    override def multiply(a: MyFuture[Int], b: MyFuture[Int]): MyFuture[Int] = {
      a.flatMap(value => b.map(value2 => value * value2))
    }
  }

  /**
    * For this interpreter we define that we wrap the scala value into a Future[Either].
    * So now in our Algebras the Action it will be [Future[Either]
    */
  type MyFutureEither[ScalaValue] = Future[Either[Throwable, ScalaValue]]

  val interpretFutureOfEither = new MyDSL[MyFutureEither] {

    override def number(a: Int): MyFutureEither[Int] = Future(Right(a))

    override def add(a: MyFutureEither[Int], b: MyFutureEither[Int]): MyFutureEither[Int] = {
      a.flatMap(value => b.map(value2 => Right(value.right.get + value2.right.get)))
    }

    override def text(str: String): MyFutureEither[String] = Future(Right(str))

    override def concat(str: MyFutureEither[String], str1: MyFutureEither[String]): MyFutureEither[String] = {
      str.flatMap(value => str1.map(value2 => Right(value.right.get + value2.right.get)))
    }

    override def multiply(a: MyFutureEither[Int], b: MyFutureEither[Int]): MyFutureEither[Int] = {
      a.flatMap(value => b.map(value2 => Right(value.right.get * value2.right.get)))
    }
  }

  // ####################
  // #      Testing     #
  // ####################

  @Test def mainInterpreter(): Unit = {
    println(createNumber(1) ~> interpret)
    println(sumNumbers(10, 10) ~> interpret)
    println(multiplyNumbers(10, 10) ~> interpret)
    println(createText("Hello") ~> interpret)
    println(concatText("Hello", " Tagless") ~> interpret)
  }

  @Test def mainInterpreterAsOption(): Unit = {
    println(createNumber(1) ~> interpretOption)
    println(sumNumbers(10, 10) ~> interpretOption)
    println(multiplyNumbers(10, 10) ~> interpretOption)
    println(createText("Hello") ~> interpretOption)
    println(concatText("Hello", " Tagless") ~> interpretOption)
  }

  @Test def mainInterpreterFuture(): Unit = {
    val future = createNumber(100) ~> interpretFuture
    println(Await.result(future, Duration.create(10, TimeUnit.SECONDS)))
    val future1 = sumNumbers(10, 10) ~> interpretFuture
    println(Await.result(future1, Duration.create(10, TimeUnit.SECONDS)))
  }

  @Test def mainInterpreterFutureOfEither(): Unit = {
    val future = createNumber(100) ~> interpretFutureOfEither
    println(Await.result(future, Duration.create(10, TimeUnit.SECONDS)))
    val future1 = sumNumbers(10, 10) ~> interpretFutureOfEither
    println(Await.result(future1, Duration.create(10, TimeUnit.SECONDS)))
  }

  @Test def mainDSL(): Unit = {
    val value = createNumber(10)
      .sumNumber(20)
      .multiplyNumber(100)
      .sumNumber(20) ~> interpret
    println(value)
  }

  @Test def mainDSLOfFuture(): Unit = {
    val future = createNumber(10)
      .sumNumber(20)
      .multiplyNumber(100)
      .sumNumber(20) ~> interpretFutureOfEither
    println(Await.result(future, Duration.create(10, TimeUnit.SECONDS)))
  }

}
