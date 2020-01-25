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
    applyDiscount("orange", applyDiscount("apple", checkoutInfo.totalPrice, checkoutInfo.apples), checkoutInfo.oranges)
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

  private def applyDiscount: (String, BigDecimal, Int) => BigDecimal = {
    (product, price, productNumber) =>
      product match {
        case  _ if product == "orange" && productNumber > 1 => price - 0.25 * (productNumber / 3)
        case  _ if product == "apple" && productNumber > 1 => price - 0.6 * (productNumber / 2)
        case _ => price
      }
  }

}
