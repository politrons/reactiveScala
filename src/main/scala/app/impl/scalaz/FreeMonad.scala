package app.impl.scalaz

import scalaz.{Free, ~>}
import scalaz.Free._

/**
  * Created by pabloperezgarcia on 12/03/2017.
  *
  *
  */
object FreeMonad extends App {

  /**
    * With type we define a new class type instead have to create an object
    * class as we have to do in Java
    */
  type Id[+X] = X
  type Symbol = String
  type Response = String


  sealed trait Orders[A]

  case class Buy(stock: Symbol, amount: Int) extends Orders[Response]

  case class Sell(stock: Symbol, amount: Int) extends Orders[Response]


  /**
    * A Free monad it´s kind like an Observable,
    * where we specify the Entry type Orders, and output type A
    */
  type OrdersF[A] = Free[Orders, A]

  /**
    * With liftF we specify to introduce a function into the Free Monad
    */
  def buy(stock: Symbol, amount: Int): OrdersF[Response] = {
    liftF[Orders, Response](Buy(stock, amount))
  }

  def sell(stock: Symbol, amount: Int): OrdersF[Response] = {
    liftF[Orders, Response](Sell(stock, amount))
  }

  val flatMapThat = buy("APPL", 100)
    .flatMap(r => sell("GOOG", 100))


  /**
    * This function return a function which receive an Order type of A and return that type
    * That type it could be anything, so using the same FreeMonad DSL we can define multiple
    * implementations types.
    * @return
    */
  def orderPrinter: Orders ~> Id = new (Orders ~> Id) {
    def apply[A](order: Orders[A]): Id[A] = order match {
      case Buy(stock, amount) =>
        println(s"Buying $amount of $stock")
        "ok"
      case Sell(stock, amount) =>
        println(s"Selling $amount of $stock")
        "ok"
    }
  }

  consumeFreeMonad


  /**
    * foldMap operator will receive a transformation function
    * which will receive the items of the pipeline monad, and it will introduce
    * the business logic over the items.
    * Also, since we define a generic type for the return, this one it could be anything.
    * @return
    */
  private def consumeFreeMonad = {
    flatMapThat.foldMap(orderPrinter)
  }
}
