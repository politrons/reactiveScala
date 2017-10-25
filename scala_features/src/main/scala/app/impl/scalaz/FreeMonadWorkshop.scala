package app.impl.scalaz

import scalaz.Free._
import scalaz.{Free, ~>}


/**
  * Created by pabloperezgarcia on 12/03/2017.
  *
  *
  */
object FreeMonadWorkshop extends App {

  /**
    * With type we define a new class type instead have to create an object
    * class as we have to do in Java
    */
  type Id[+X] = X
  type Symbol = String
  type Response = Any
  type Pair = (String, Int)

  /**
    * The next classes are our ADT algebraic data types
    */

  sealed trait Orders[A]

  case class _ListStocks() extends Orders[Any]

  case class _Buy(stock: Symbol, amount: Int) extends Orders[Any]

  case class _Sell(stock: Symbol, amount: Int) extends Orders[Any]

  /**
    * A Free monad it´s kind like an Observable,
    * where we specify the Entry type Orders, and output type A
    */
  type OrdersFree[A] = Free[Orders, A]

  def ListStocks(): OrdersFree[Any] = liftF[Orders, Any](_ListStocks())

  /**
    * With liftF we specify to introduce a function into the Free Monad
    */
  def BuyStock(stock: Symbol, amount: Int): OrdersFree[Any] = liftF[Orders, Any](_Buy(stock, amount))

  def SellStock(stock: Symbol, amount: Int): OrdersFree[Any] = liftF[Orders, Any](_Sell(stock, amount))


  val freeMonad =
    ListStocks()
      .map(symbols => {
        var value = ""
        var amount = 100
        try {
          value = symbols.asInstanceOf[List[Symbol]]
            .filter(symbol => symbol.eq("FB"))
            .head
        } catch {
          case e: Exception =>
            value = s"ERROR $e"
            amount = 0
        }
        BuyStock(value, amount)
      })
      .flatMap(pair => {
        SellStock(s"GOOG ${pair.asInstanceOf[Pair]._1}", pair.asInstanceOf[Pair]._2 + 100)
      })

  implicit class customFree(free: Free[FreeMonadWorkshop.Orders, Any]) {

    def run() = free.foldMap(orderInterpreter1)

    def Buy(value:String):Free[FreeMonadWorkshop.Orders, Any] = free.map(value => BuyStock(value.asInstanceOf[List[Symbol]].head, 1))

    def Sell(value:String):Free[FreeMonadWorkshop.Orders, Any] = free.map(pair => SellStock(s"GOOG ${pair.asInstanceOf[Pair]._1}", pair.asInstanceOf[Pair]._2 + 100))

  }

  /**
    * This function return a function which receive an Order type of A and return that type
    * That type it could be anything, so using the same FreeMonad DSL we can define multiple
    * implementations types.
    *
    * @return
    */
  def orderInterpreter1: Orders ~> Id = new (Orders ~> Id) {
    def apply[A](order: Orders[A]): Id[A] = order match {
      case _ListStocks() =>
        println(s"Getting list of stocks: FB, TWTR")
        List("FB", "TWTR")
      case _Buy(stock, amount) =>
        println(s"Buying $amount of $stock")
        new Pair(stock, amount)
      case _Sell(stock, amount) =>
        println(s"Selling $amount of $stock")
        "done interpreter 1"
    }
  }

  /**
    * Thanks to the free monads defined, now using another interpreter we can create a corner case
    * to see how our monad behave for instances against a NullPointerException
    *
    * @return
    */
  def orderInterpreter2: Orders ~> Id = new (Orders ~> Id) {
    def apply[A](order: Orders[A]): Id[A] = order match {
      case _ListStocks() =>
        println(s"Getting list of stocks: FB, TWTR")
        null
      case _Buy(stock, amount) =>
        println(s"Buying $amount of $stock")
        new Pair(stock, amount)
      case _Sell(stock, amount) =>
        println(s"Selling $amount of $stock")
        "done interpreter 2"
    }
  }

  def orderInterpreter3: Orders ~> Id = new (Orders ~> Id) {
    def apply[A](order: Orders[A]): Id[A] = order match {
      case _ListStocks() =>
        println(s"Getting list of stocks: FB, TWTR")
        List("TWTR")
      case _Buy(stock, amount) =>
        println(s"Buying $amount of $stock")
        new Pair(stock, amount)
      case _Sell(stock, amount) =>
        println(s"Selling $amount of $stock")
        "done interpreter 3"
    }
  }


  /**
    * foldMap operator will receive a transformation function
    * which will receive the items of the pipeline monad, and it will introduce
    * the bussiness logic over the items.
    * Also, since we define a generic type for the return, this one it could be anything.
    *
    **/
  freeMonad.foldMap(orderInterpreter1)
  println("###############################")
  freeMonad.foldMap(orderInterpreter2)
  println("###############################")
  freeMonad.foldMap(orderInterpreter3)


}
