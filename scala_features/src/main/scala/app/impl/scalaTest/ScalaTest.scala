package app.impl.scalaTest

import org.scalatest.{FeatureSpec, GivenWhenThen}

/**
  * Created by pabloperezgarcia on 08/07/2017.
  */
class ScalaTest extends FeatureSpec with GivenWhenThen {


  info("This test has as requirement to test mock connector")
  feature("Mock connector") {
    scenario("Create entity using mock connector") {
      Given("a Musin message")
      When("I make a request to f2e")
      Then("The entity it´s created successfully")
    }
  }

}
