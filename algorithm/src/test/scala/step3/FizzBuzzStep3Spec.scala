package step3

import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{BeforeAndAfterAll, BeforeAndAfterEach, FeatureSpec, GivenWhenThen}

class FizzBuzzStep3Spec extends FeatureSpec with GivenWhenThen with BeforeAndAfterAll with BeforeAndAfterEach with ScalaFutures {

  feature("Step 3 corner cases") {
    scenario("Number of fizz in the report") {
      Given("a range number from 1 to 20")
      val from = 1
      val to = 20
      When("i invoke fizzbuzz program")
      val output = FizzBuzzStep3(from, to)
      Then("the number of fizz in the report is 4")
      assert(output("fizz") == 4)
    }

    scenario("Number of buzz in the report") {
      Given("a range number from 1 to 20")
      val from = 1
      val to = 20
      When("i invoke fizzbuzz program")
      val output = FizzBuzzStep3(from, to)
      Then("the number of buzz in the report is 3")
      assert(output("buzz") == 3)
    }

    scenario("Number of fizzbuzz in the report") {
      Given("a range number from 1 to 20")
      val from = 1
      val to = 20
      When("i invoke fizzbuzz program")
      val output = FizzBuzzStep3(from, to)
      Then("the number of fizzbuzz in the report is 1")
      assert(output("fizzbuzz") == 1)
    }

    scenario("Number of lucky in the report") {
      Given("a range number from 1 to 20")
      val from = 1
      val to = 20
      When("i invoke fizzbuzz program")
      val output = FizzBuzzStep3(from, to)
      Then("the number of lucky in the report is 2")
      assert(output("lucky") == 2)
    }

    scenario("Number of integer in the report") {
      Given("a range number from 1 to 20")
      val from = 1
      val to = 20
      When("i invoke fizzbuzz program")
      val output = FizzBuzzStep3(from, to)
      Then("the number of integer in the report is 10")
      assert(output("integer") == 10)
    }

  }
}

