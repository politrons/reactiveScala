package app.impl.shoppingCart

case class ShoppingCart() {

  var basket: Basket = Basket(List())
  val AppleType = "apple"
  val OrangeType = "orange"

  //  ADT
  // ------
  sealed trait DiscountProduct

  case class Apple(amount: Int) extends DiscountProduct

  case class Orange(amount: Int) extends DiscountProduct

  case class Product(productId: String, description: String, price: BigDecimal)

  case class Basket(products: List[Product])

  case class CheckoutInfo(totalPrice: BigDecimal, apples: Apple, oranges: Orange)

  // Logic
  // -------

  def addProduct(productId: String, description: String, price: BigDecimal): List[Product] = {
    basket = basket.copy(products = basket.products ++ List(Product(productId, description, price)))
    basket.products
  }

  def checkout(): BigDecimal = {
    val checkoutInfo = getCheckoutInfo
    applyDiscount(applyDiscount(checkoutInfo.totalPrice, checkoutInfo.apples), checkoutInfo.oranges)
  }

  private def getCheckoutInfo: CheckoutInfo = {
    basket.products.foldRight(CheckoutInfo(BigDecimal(0.0), Apple(0), Orange(0)))((product, checkout) => {
      product.description match {
        case AppleType => checkout.copy(product.price + checkout.totalPrice, Apple(checkout.apples.amount + 1), checkout.oranges)
        case OrangeType => checkout.copy(product.price + checkout.totalPrice, checkout.apples, Orange(checkout.oranges.amount + 1))
        case _ => checkout.copy(product.price + checkout.totalPrice)
      }
    })
  }

  private def applyDiscount: (BigDecimal, DiscountProduct) => BigDecimal = {
    (price, discountProduct) =>
      discountProduct match {
        case apple: Apple if apple.amount > 1 => price - 0.6 * (apple.amount / 2)
        case orange: Orange if orange.amount > 1 => price - 0.25 * (orange.amount / 3)
        case _ => price
      }
  }

}
