package app.impl.types


import eu.timepit.refined._
import eu.timepit.refined.api.Refined
import eu.timepit.refined.auto._
import eu.timepit.refined.boolean.{And, Not}
import eu.timepit.refined.numeric._
import eu.timepit.refined.string._
import org.junit.Test

/**
 * All this examples are done on top of this awesome library [Refined] https://github.com/fthomas/refined#using-refined
 *
 * Refined library used with numbers allow us wrap and ensure in compilation time that numbers has the value as we
 * expect.
 * When we use [refineMV] passing as type the rule we want to apply[Positive, Negative, Greater, Less] and then the value we ensure in compilation time
 * the value follow that rule.
 *
 * Another cool thing about Refined, is once you access to a Refined type value, he automatically unbox the type to the value.
 *
 * In case we don't have the control of the creation of the number, using [refineV] we cannot break the program in compilation time
 * but we can control effect receiving an Either that tell us if the rule was applied.
 */
class RefinedFeature {

  /**
   * Examples creating positive and negative numbers checking the rules to break in compilation time otherwise.
   */
  @Test
  def positiveNegativeNumbers(): Unit = {
    val positiveNumber: Int Refined Positive = 1981
    //val negativeNumber: Int Refined Positive = -1981 --> It wont compile
    println(positiveNumber)

    //Unboxing
    val refinedType: Refined[Int, Positive] = refineMV[Positive](1981)
    println(refinedType)

    //Either to box the number in refined
    val rightNumber: Either[String, Refined[Int, Positive]] = refineV[Positive](1981)
    val leftNumber: Either[String, Refined[Int, Positive]] = refineV[Positive](-1981)
    println(rightNumber)
    println(leftNumber)

    val negativeNumber: Int Refined Negative = -1981
    //val negativeNumber : Int Refined Negative = 1981 --> it wont compile
    println(negativeNumber)
  }

  /**
   * Examples creating greater and lower numbers checking the rules to break in compilation time otherwise.
   */
  @Test
  def greaterLowerEqualThanNumbers(): Unit = {
    val greaterThan1000Number: Int Refined Greater[W.`1000`.T] = 1981
    //val lowerThan1000Number: Int Refined Greater[W.`1000`.T] = 900 --> It wont compile.
    println(greaterThan1000Number)

    val greaterOrEqual: Int Refined GreaterEqual[W.`1000`.T] = 1000
    println(greaterOrEqual)

    val lessThan2000Number: Int Refined Less[W.`2000`.T] = 1981
    //val lessThan2000Number: Int Refined Less[W.`2000`.T] = 2900 //--> It wont compile.
    println(lessThan2000Number)

    val lessOrEqual: Int Refined LessEqual[W.`1000`.T] = 1000
    println(lessOrEqual)
  }

  /**
   * We can compose rules just using [And] operator which it will create the Compose type.
   * Once we have that validation type, we can bound to the creation of a number just using as we saw
   * before [refineMV] passing as rule, the new one that we create.
   */
  @Test
  def composingRulesNumbers(): Unit = {
    type ComposeRule = Less[W.`2000`.T] And Greater[W.`1000`.T]
    val value = refineMV[ComposeRule](1981)
    println(value)
  }

  /**
   * Using String package we can valid string values again in compilation time. Or in runtime controlling effects with Either
   *
   * We use the next rules here:
   * * [Url] -> Check the format of the url, it can break in compilation or in runtime with an Either.
   * * [UUID] -> Check the format of the UUID, it can break in compilation or in runtime with an Either.
   */
  @Test
  def stringFeatures(): Unit = {
    val urlValue: String Refined Url = "http://localhost:8080"
    //val urlValueWrong : String Refined Url = "htp://localhost:8080" --> It wont compile
    println(urlValue)

    val eitherUrl = refineV[Url]("htp://localhost:8080")
    println(eitherUrl)

    val uuidValid: String Refined Uuid = "6d57b748-8236-11ea-bc55-0242ac130003"
    //val uuidInvalid : String Refined Uuid = "blabla" --> it wont compile
    println(uuidValid)

    val intStringValid: String Refined ValidInt = "1981"
    //val intStringInvalid : String Refined ValidInt = "1981.0" --> It wont compile
    println(intStringValid)
  }
}
