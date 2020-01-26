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

  case class CheckoutInfo(totalPrice: BigDecimal, discountProducts: List[DiscountProduct])

  // Logic
  // -------

  def addProduct(productId: String, description: String, price: BigDecimal): List[Product] = {
    basket = basket.copy(products = basket.products ++ List(Product(productId, description, price)))
    basket.products
  }

  def checkout(): BigDecimal = applyDiscount(getCheckoutInfo)

  private def getCheckoutInfo: CheckoutInfo = {
    basket.products.foldRight(CheckoutInfo(BigDecimal(0.0), List()))((product, checkoutInfo) => {
      product.description match {
        case AppleType => createNewCheckoutInfo(product, checkoutInfo, Apple(getDiscountProductsAmount(checkoutInfo)) :: filterProductsWithDiscountType(checkoutInfo))
        case OrangeType => createNewCheckoutInfo(product, checkoutInfo, Orange(getDiscountProductsAmount(checkoutInfo)) :: filterProductsWithDiscountType(checkoutInfo))
        case _ => checkoutInfo.copy(product.price + checkoutInfo.totalPrice)
      }
    })
  }

  private def createNewCheckoutInfo(product: Product, checkoutInfo: CheckoutInfo, discountProducts: List[DiscountProduct]): CheckoutInfo = {
    checkoutInfo.copy(product.price + checkoutInfo.totalPrice, discountProducts)
  }

  private def getDiscountProductsAmount[T <: DiscountProduct](checkoutInfo: CheckoutInfo): Int = {
    checkoutInfo.discountProducts.find(discountProduct => discountProduct.isInstanceOf[T]) match {
      case Some(apple: Apple) => apple.amount + 1
      case Some(orange: Orange) => orange.amount + 1
      case None => 1
    }
  }

  private def filterProductsWithDiscountType[T <: DiscountProduct](checkoutInfo: CheckoutInfo): List[DiscountProduct] = {
    checkoutInfo.discountProducts.filter(discountProduct => !discountProduct.isInstanceOf[T])
  }

  private def applyDiscount: CheckoutInfo => BigDecimal = {
    checkoutInfo =>
      checkoutInfo.discountProducts.foldRight(checkoutInfo.totalPrice)((discountProduct, discountPrice) =>
        discountProduct match {
          case apple: Apple if apple.amount > 1 => discountPrice - 0.6 * (apple.amount / 2)
          case orange: Orange if orange.amount > 1 => discountPrice - 0.25 * (orange.amount / 3)
          case _ => discountPrice
        })
  }

}
