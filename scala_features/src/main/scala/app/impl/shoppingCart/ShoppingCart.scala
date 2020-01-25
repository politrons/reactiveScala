package app.impl.shoppingCart

case class ShoppingCart() {

  var basket: Basket = Basket(List())

  //Domain

  case class Product(productId: String, description: String, price: BigDecimal)

  case class Basket(products: List[Product])

  case class CheckoutInfo(totalPrice: BigDecimal, applesAndOranges: (Int, Int))

  def addProduct(productId: String, description: String, price: BigDecimal): List[Product] = {
    basket = basket.copy(products = basket.products ++ List(Product(productId, description, price)))
    basket.products
  }

  def checkout(): BigDecimal = {
    val checkoutInfo = getCheckoutInfo
    getOrangeDiscount(getAppleDiscount(checkoutInfo.totalPrice, checkoutInfo.applesAndOranges._1), checkoutInfo.applesAndOranges._2)
  }

  private def getCheckoutInfo: CheckoutInfo = {
    basket.products.foldRight(CheckoutInfo(BigDecimal(0.0), (0, 0)))((product, checkout) => {
      val tuple = product.description match {
        case "apple" => (checkout.applesAndOranges._1 + 1, checkout.applesAndOranges._2)
        case "orange" => (checkout.applesAndOranges._1, checkout.applesAndOranges._2 + 1)
        case _ => checkout.applesAndOranges
      }
      CheckoutInfo(product.price + checkout.totalPrice, tuple)
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
