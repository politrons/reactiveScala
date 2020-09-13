package step1

import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{BeforeAndAfterAll, BeforeAndAfterEach, FeatureSpec, GivenWhenThen}

class FizzBuzzStep1Spec extends FeatureSpec with GivenWhenThen with BeforeAndAfterAll with BeforeAndAfterEach with ScalaFutures {

  feature("Step 1 corner cases") {

    scenario("positive numbers fizz for numbers that are multiply of 3") {
      Given("a range number from 2 to 16")
      val from = 2
      val to = 16
      When("i invoke fizzbuzz program")
      val output = FizzBuzzStep1(from, to)
      Then("the number of fizz are 4")
      assert(output.count(element => element == "fizz") == 4)
    }

    scenario("positive numbers buzz for numbers that are multiply of 5") {
      Given("a range number from 2 to 16")
      val from = 2
      val to = 16
      When("i invoke fizzbuzz program")
      val output = FizzBuzzStep1(from, to)
      Then("the number of fizz are 2")
      assert(output.count(element => element == "buzz") == 2)
    }

    scenario("positive numbers fizzbuzz for numbers that are multiply of 5") {
      Given("a range number from 2 to 16")
      val from = 2
      val to = 16
      When("i invoke fizzbuzz program")
      val output = FizzBuzzStep1(from, to)
      Then("the number of fizz are 1")
      assert(output.count(element => element == "fizzbuzz") == 1)
    }

    scenario("negative numbers fizz for numbers that are multiply of 3") {
      Given("a range number from 2 to 16")
      val from = -3
      val to = -1
      When("i invoke fizzbuzz program")
      val output = FizzBuzzStep1(from, to)
      Then("the number of fizz are 1")
      assert(output.count(element => element == "fizz") == 1)
    }

    scenario("negative numbers buzz for numbers that are multiply of 5") {
      Given("a range number from 2 to 16")
      val from = -16
      val to = -2
      When("i invoke fizzbuzz program")
      val output = FizzBuzzStep1(from, to)
      Then("the number of fizz are 2")
      assert(output.count(element => element == "buzz") == 2)
    }

    scenario("negative numbers fizzbuzz for numbers that are multiply of 5") {
      Given("a range number from 2 to 16")
      val from = -16
      val to = -2
      When("i invoke fizzbuzz program")
      val output = FizzBuzzStep1(from, to)
      Then("the number of fizz are 1")
      assert(output.count(element => element == "fizzbuzz") == 1)
    }
  }
}

