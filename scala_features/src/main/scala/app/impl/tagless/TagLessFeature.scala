package app.impl.tagless

import java.util.concurrent.TimeUnit

import org.junit.Test

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}
import scala.concurrent.ExecutionContext.Implicits.global

class TagLessFeature {

  // ################
  // #   Algebras   #
  // ################
  /**
    * Here we define the Algebras or actions that our DSL will be able to use.
    * This Algebras are meant to be used only by the bridges which you can see in
    * the next section, which are the code that the consumer will use as DSL
    */
  trait MyDSL[Action[_]] {

    def number(v: Int): Action[Int]

    def add(a: Action[Int], b: Action[Int]): Action[Int]

    def text(v: String): Action[String]

    def concat(a: Action[String], b: Action[String]): Action[String]

  }

  // ################
  // #   Bridges    #
  // ################
  /**
    * Bridges aka DSL is the Domain specific Language that our consumers will use.
    * Bridges use Scala Types class pattern to be a bridge between the value passed and the implementation defined in the Algebra.
    * As Type class patter provide, depending the type passed as the implicit the implementation can be one or another.
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

  implicit class customBridge(bridge: MyBridge[Int]) {

    def appendNumber(number: Int) = new MyBridge[Int] {
      override def ~>[Action[_]](implicit interpreter: MyDSL[Action]): Action[Int] = {
        interpreter.add(interpreter.number(number), bridge.~>)
      }
    }
  }

  //
  // ####################
  // #   Interpreters   #
  // ####################
  /**
    * The interpreter implement the DSL/Algebras to give a implementation with the values that we receive.
    * As you can see here one of the biggest benefits of use Tagless or Free are that we can have multiples
    * implementations which are really easy to plug and play as you can see in the execution of the examples.
    */
  type Id[ScalaValue] = ScalaValue

  val interpret = new MyDSL[Id] {

    override def number(v: Int): Id[Int] = v

    override def add(a: Id[Int], b: Id[Int]): Id[Int] = a + b

    override def text(v: String): Id[String] = v

    override def concat(a: Id[String], b: Id[String]): Id[String] = a + " " + b

  }

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

  }

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

  }


  @Test def mainInterpreter(): Unit = {
    println(createNumber(1) ~> interpret)
    println(sumNumbers(10, 10) ~> interpret)
    println(createText("Hello") ~> interpret)
    println(concatText("Hello", " Tagless") ~> interpret)
  }

  @Test def mainInterpreterAsOption(): Unit = {
    println(createNumber(1) ~> interpretOption)
    println(sumNumbers(10, 10) ~> interpretOption)
    println(createText("Hello") ~> interpretOption)
    println(concatText("Hello", " Tagless") ~> interpretOption)
  }

  @Test def mainInterpreterFuture(): Unit = {
    val future = createNumber(100) ~> interpretFuture
    println(Await.result(future, Duration.create(10, TimeUnit.SECONDS)))
  }

  @Test def mainDSL(): Unit = {
    val value = createNumber(10)
      .appendNumber(20)
      .appendNumber(20)
      .appendNumber(20) ~> interpret
    println(value)
  }

}
