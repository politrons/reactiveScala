package app.impl.scalaz

import org.junit.Test

import scalaz.{Scalaz, State}

/**
  * Created by pabloperezgarcia on 05/11/2017.
  *
  * ScalaZ State is a monad to offer State machine patter. In order to use it, you just need to define your functions
  * where we need to Specify that the return of that function is a Scalaz.modify[Type].
  *
  * Once in the function you receive your state class to modify his state.
  * In order to do that is important use case class since we need to use copy to create new copy of the current state
  * of our class adding the new changes.
  *
  * Now using the state monad in our pipeline, it will propagate the state of our object.
  *
  * Since our pipeline return an State in the last step, to use that State monad we need to use exec(init_value)
  * which it will start the pipeline with an initial state that you difine.
  */
class StateMonad {

  case class Product(name: String, cost: Int)

  case class Basket(totalCost: Int, products: List[Product])

  object Basket {
    val init = Basket(0, List())
  }

  /**
    * Modify our basket which is a state machine, to add new products
    */
  def add(product: Product): State[Basket, Unit] = Scalaz.modify[Basket] {
    basket => basket.copy(totalCost = calcNewTotalPrice(basket) + product.cost, products = addProduct(product, basket))
  }

  /**
    * Modify our basket which is a state machine, to subtract products
    */
  def remove(product: Product): State[Basket, Unit] = Scalaz.modify[Basket] {
    basket => basket.copy(totalCost = calcNewTotalPrice(basket) - product.cost, products = removeProduct(product, basket))
  }

  private def addProduct(product: Product, basket: Basket) = {
    basket.products ++ List(product)
  }

  private def removeProduct(newProduct: Product, basket: Basket) = {
    basket.products.filter(product => product != newProduct)
  }

  private def calcNewTotalPrice(basket: Basket) = {
    basket.products.map(product => product.cost).sum
  }

  def shopping = for {
    _ <- add(cocaCola)
    _ <- add(ham)
    _ <- add(milk)
    _ <- add(cornFlakes)
    - <- remove(cocaCola)
    _ <- remove(ham)
  } yield ()

  @Test
  def main(): Unit = {
    val res = shopping.exec(Basket.init)//Exec run the State machine with an initial state
    println(res)
    assert(res.totalCost == 4)
    assert(res.products.length == 2)
  }

  val cocaCola = Product("coca-cola", 2)
  val ham = Product("ham", 1)
  val cornFlakes = Product("cornflakes", 3)
  val milk = Product("milk", 1)

}
