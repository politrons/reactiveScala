package com.politrons.tagless

import scala.util.Try

object TaglessFinal extends App {

  /**
    * Domain
    * -----------
    */
  case class Product(id: String, description: String)

  case class ShoppingCart(id: String, products: List[Product])

  /**
    * [ADT] Algebra Data Types
    * -------------------------
    * We describe the DSL that we will use in our program. We will expose to the client only this contract
    * and we will make as abstract as possible the details of the implementation.
    * The idea is to hide the specific effect system we will use.
    * That's the reason to use Higher Kinded Types F[_]
    * This is normally consider the Behavior of the program.
    */
  trait ShoppingCarts[F[_]] {
    def create(id: String): F[Unit]

    def find(id: String): F[ShoppingCart]

    def add(sc: ShoppingCart, product: Product): F[ShoppingCart]
  }

  trait ShoppingCartDSL[ScalaValue] {
    def ~>[F[_]](implicit interpreter: ShoppingCarts[F]): F[ScalaValue]
  }

  def createShoppingCart(id: String) = new ShoppingCartDSL[Unit] {
    override def ~>[F[_]](implicit interpreter: ShoppingCarts[F]): F[Unit] = interpreter.create(id)
  }

  def findShoppingCart(id: String) = new ShoppingCartDSL[ShoppingCart] {
    override def ~>[F[_]](implicit interpreter: ShoppingCarts[F]): F[ShoppingCart] = interpreter.find(id)
  }

  def addInShoppingCart(sc: ShoppingCart, product: Product) = new ShoppingCartDSL[ShoppingCart] {
    override def ~>[F[_]](implicit interpreter: ShoppingCarts[F]): F[ShoppingCart] = interpreter.add(sc, product)
  }

  /**
    * Interpreters
    * -------------
    */

  val tryInterpreter: ShoppingCarts[Try] = new ShoppingCarts[Try] {

    var shoppingCartMap: Map[String, ShoppingCart] = Map()

    override def create(id: String): Try[Unit] = Try {
      shoppingCartMap = Map(id -> ShoppingCart(id, List())) ++ shoppingCartMap
    }

    override def find(id: String): Try[ShoppingCart] = Try {
      shoppingCartMap(id)
    }

    override def add(sc: ShoppingCart, product: Product): Try[ShoppingCart] = Try {
      sc.copy(products = product +: sc.products)
    }
  }

  /**
    * Program
    * ----------
    */

  private val shoppingCartProgram: Try[ShoppingCart] = for {
    _ <- createShoppingCart("1981") ~> tryInterpreter
    sc <- findShoppingCart("1981") ~> tryInterpreter
    sc <- addInShoppingCart(sc, Product("111", "Coca-cola")) ~> tryInterpreter
  } yield sc

  println(shoppingCartProgram)

}
