package app.impl.scalaz

import scalaz.Free._
import scalaz.{Free, ~>}


/**
  * Created by pabloperezgarcia on 12/03/2017.
  *
  *
  */
object ScalaFreeMonad extends App {

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

  case class ListStocks() extends Orders[List[Symbol]]

  sealed trait Log[A]

  case class Info(msg: String) extends Log[Unit]

  case class Error(msg: String) extends Log[Unit]


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

  def listStocks(): OrdersF[List[Symbol]] = {
    liftF[Orders, List[Symbol]](ListStocks())
  }

  /**
    * As part of our DSL we might want to log the pipeline during the execution
    */
  type LogF[A] = Free[Log, A]

  def info(msg: String): LogF[Unit] = liftF[Log, Unit](Info(msg))

  def error(msg: String): LogF[Unit] = liftF[Log, Unit](Error(msg))

  val flatMapThat = listStocks()
    .map(symbols => {
      buy("done", 100)
    })
    .flatMap(r => sell("GOOG", 100))


  /**
    * This function return a function which receive an Order type of A and return that type
    * That type it could be anything, so using the same FreeMonad DSL we can define multiple
    * implementations types.
    *
    * @return
    */
  def orderInterpreter: Orders ~> Id = new (Orders ~> Id) {
    def apply[A](order: Orders[A]): Id[A] = order match {
      case ListStocks() =>
        println(s"Getting list of stocks: FB, TWTR")
        List("FB", "TWTR")
      case Buy(stock, amount) =>
        println(s"Buying $amount of $stock")
        "ok"
      case Sell(stock, amount) =>
        println(s"Selling $amount of $stock")
        "ok"
    }
  }


  /**
    * foldMap operator will receive a transformation function
    * which will receive the items of the pipeline monad, and it will introduce
    * the bussiness logic over the items.
    * Also, since we define a generic type for the return, this one it could be anything.
    *
    **/
  flatMapThat.foldMap(orderInterpreter)


}
