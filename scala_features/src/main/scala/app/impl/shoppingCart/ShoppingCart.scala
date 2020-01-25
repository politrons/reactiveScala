package app.impl.shoppingCart

case class ShoppingCart() {

  var basket: Basket = Basket(List())

  //Domain

  case class Product(productId: String, description: String, price: BigDecimal)

  case class Basket(products: List[Product])

  case class CheckoutInfo(totalPrice: BigDecimal, apples: Int, oranges: Int)

  def addProduct(productId: String, description: String, price: BigDecimal): List[Product] = {
    basket = basket.copy(products = basket.products ++ List(Product(productId, description, price)))
    basket.products
  }

  def checkout(): BigDecimal = {
    val checkoutInfo = getCheckoutInfo
    getOrangeDiscount(getAppleDiscount(checkoutInfo.totalPrice, checkoutInfo.apples), checkoutInfo.oranges)
  }

  private def getCheckoutInfo: CheckoutInfo = {
    basket.products.foldRight(CheckoutInfo(BigDecimal(0.0), 0, 0))((product, checkout) => {
      product.description match {
        case "apple" => checkout.copy(product.price + checkout.totalPrice, checkout.apples + 1, checkout.oranges)
        case "orange" => checkout.copy(product.price + checkout.totalPrice, checkout.apples, checkout.oranges + 1)
        case _ => checkout.copy(product.price + checkout.totalPrice)
      }
    })
  }

  private def getOrangeDiscount: (BigDecimal, Int) => BigDecimal = {
    (price, oranges) =>
      if (oranges > 1) {
        val discount = oranges / 3
        price - 0.25 * discount
      } else {
        price
      }
  }

  private def getAppleDiscount: (BigDecimal, Int) => BigDecimal = {
    (price, apples) =>
      if (apples > 1) {
        val discount = apples / 2
        price - 0.6 * discount
      } else {
        price
      }
  }
}
