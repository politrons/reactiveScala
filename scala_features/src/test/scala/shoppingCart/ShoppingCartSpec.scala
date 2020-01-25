package shoppingCart

import app.impl.shoppingCart.ShoppingCart
import org.scalatest._

class ShoppingCartSpec extends FeatureSpec with GivenWhenThen {

  info("This test has as requirement to test.....")


  feature("Make checkout feature") {

    scenario("Add one product into basket") {
      Given("A shopping cart")
      val shoppingCart = ShoppingCart()
      When("I add a product")
      val products = shoppingCart.addProduct("1", "coca-cola", 2.0)
      Then("I receive the list of products with the new product")
      assert(products.length == 1)
    }

    scenario("Remove one product from basket") {
      Given("a shopping cart")
      When("I add a product into basket")
      When("I remove the product")
      Then("the list of products is empty")
    }

    scenario("Add two apple total price is just one apple price") {
      Given("A shopping cart")
      val shoppingCart = ShoppingCart()
      When("I add an apple")
      shoppingCart.addProduct("1", "apple", 0.6)
      And("I add an apple")
      shoppingCart.addProduct("1", "apple", 0.6)
      Then("total price is just one apple")
      assert(shoppingCart.checkout() == 0.6)
    }

    scenario("Add three apple total price is just two apple price") {
      Given("A shopping cart")
      val shoppingCart = ShoppingCart()
      When("I add an apple")
      shoppingCart.addProduct("1", "apple", 0.6)
      And("I add an apple")
      shoppingCart.addProduct("1", "apple", 0.6)
      And("I add an apple")
      shoppingCart.addProduct("1", "apple", 0.6)
      Then("total price is just one apple")
      assert(shoppingCart.checkout() == 1.2)
    }

    scenario("Add 4 apple total price is two apple price") {
      Given("A shopping cart")
      val shoppingCart = ShoppingCart()
      When("I add an apple")
      shoppingCart.addProduct("1", "apple", 0.6)
      And("I add an apple")
      shoppingCart.addProduct("1", "apple", 0.6)
      And("I add an apple")
      shoppingCart.addProduct("1", "apple", 0.6)
      And("I add an apple")
      shoppingCart.addProduct("1", "apple", 0.6)
      Then("total price is just one apple")
      assert(shoppingCart.checkout() == 1.2)
    }

    scenario("Add two orange total price is two  orange price") {
      Given("A shopping cart")
      val shoppingCart = ShoppingCart()
      When("I add an apple")
      shoppingCart.addProduct("1", "orange", 0.25)
      And("I add an apple")
      shoppingCart.addProduct("1", "orange", 0.25)
      Then("total price is just one apple")
      assert(shoppingCart.checkout() == 0.50)
    }

    scenario("Add three orange total price is two  orange price") {
      Given("A shopping cart")
      val shoppingCart = ShoppingCart()
      When("I add an apple")
      shoppingCart.addProduct("1", "orange", 0.25)
      And("I add an apple")
      shoppingCart.addProduct("1", "orange", 0.25)
      And("I add an apple")
      shoppingCart.addProduct("1", "orange", 0.25)
      Then("total price is just two oranges")
      assert(shoppingCart.checkout() == 0.50)
    }

    scenario("Add 7 orange total price is 5  orange price") {
      Given("A shopping cart")
      val shoppingCart = ShoppingCart()
      When("I add an apple")
      shoppingCart.addProduct("1", "orange", 0.25)
      And("I add an apple")
      shoppingCart.addProduct("1", "orange", 0.25)
      And("I add an apple")
      shoppingCart.addProduct("1", "orange", 0.25)
      And("I add an apple")
      shoppingCart.addProduct("1", "orange", 0.25)
      And("I add an apple")
      shoppingCart.addProduct("1", "orange", 0.25)
      And("I add an apple")
      shoppingCart.addProduct("1", "orange", 0.25)
      And("I add an apple")
      shoppingCart.addProduct("1", "orange", 0.25)
      Then("total price is just two oranges")
      assert(shoppingCart.checkout() == 1.25)
    }

    scenario("checkout basket") {
      Given("A shopping cart")
      val shoppingCart = ShoppingCart()
      When("I add a product")
      shoppingCart.addProduct("1", "coca-cola", 2.0)
      shoppingCart.addProduct("2", "salad", 5.0)
      And("I add made checkout")
      Then("I have total price of the basket")
      val totalPrice = shoppingCart.checkout()
      assert(totalPrice == 7.0)
    }


  }
}