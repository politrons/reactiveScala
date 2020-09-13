package step2

import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{BeforeAndAfterAll, BeforeAndAfterEach, FeatureSpec, GivenWhenThen}

class FizzBuzzStep2Spec extends FeatureSpec with GivenWhenThen with BeforeAndAfterAll with BeforeAndAfterEach with ScalaFutures {

  feature("Step 2 corner cases ") {
    scenario("If the number contains a 3 you must output the text 'lucky'") {
      Given("a range number from 2 to 16")
      val from = 2
      val to = 14
      When("i invoke fizzbuzz program")
      val output = FizzBuzzStep2(from, to)
      Then("the number of lucky are 2")
      assert(output.count(element => element == "lucky") == 2)
    }
  }

  feature("Step 1 corner cases still working in Step2") {

    scenario("positive numbers fizz for numbers that are multiply of 3") {
      Given("a range number from 2 to 16")
      val from = 2
      val to = 16
      When("i invoke fizzbuzz program")
      val output = FizzBuzzStep2(from, to)
      Then("the number of fizz are 3")
      assert(output.count(element => element == "fizz") == 3)
    }

    scenario("positive numbers buzz for numbers that are multiply of 5") {
      Given("a range number from 2 to 16")
      val from = 2
      val to = 16
      When("i invoke fizzbuzz program")
      val output = FizzBuzzStep2(from, to)
      Then("the number of fizz are 2")
      assert(output.count(element => element == "buzz") == 2)
    }

    scenario("positive numbers fizzbuzz for numbers that are multiply of 5") {
      Given("a range number from 2 to 16")
      val from = 2
      val to = 16
      When("i invoke fizzbuzz program")
      val output = FizzBuzzStep2(from, to)
      Then("the number of fizz are 1")
      assert(output.count(element => element == "fizzbuzz") == 1)
    }

    scenario("negative numbers fizz for numbers that are multiply of 3") {
      Given("a range number from 2 to 16")
      val from = -16
      val to = -2
      When("i invoke fizzbuzz program")
      val output = FizzBuzzStep2(from, to)
      Then("the number of fizz are 1")
      assert(output.count(element => element == "fizz") == 3)
    }

    scenario("negative numbers buzz for numbers that are multiply of 5") {
      Given("a range number from 2 to 16")
      val from = -16
      val to = -2
      When("i invoke fizzbuzz program")
      val output = FizzBuzzStep2(from, to)
      Then("the number of fizz are 2")
      assert(output.count(element => element == "buzz") == 2)
    }

    scenario("negative numbers fizzbuzz for numbers that are multiply of 5") {
      Given("a range number from 2 to 16")
      val from = -16
      val to = -2
      When("i invoke fizzbuzz program")
      val output = FizzBuzzStep2(from, to)
      Then("the number of fizz are 1")
      assert(output.count(element => element == "fizzbuzz") == 1)
    }
  }
}


