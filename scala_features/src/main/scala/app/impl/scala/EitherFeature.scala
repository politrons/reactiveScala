package app.impl.scala

import org.junit.Test

/**
  * Either is a pair value wrap that Scala introduce as pattern of get value or get compensation.
  * The normal use is, that you allocate a left or right object as an either which is an abstraction.
  * is similar as optional has two states, defined not_defined, here it would be left or right value.
  *
  * Also Either is more, he is a monad, which means that not only we can allocate the value in there, but also
  * transform that value using functions.
  */
class EitherFeature {

  /**
    * Normally in the standard way of use Either left is used for the bad part or the part of a result that you dont exepct
    * as compensation for something that was not as you expected.
    *
    */
  @Test def eitherLeft(): Unit = {
    val either = getEitherValue(false)
    assert(either.isLeft)
    println(either.left.get)
  }

  /**
    * Using either we can expect that the right side of the either is the "right" value.
    */
  @Test def eitherRight(): Unit = {
    val either = getEitherValue(true)
    assert(either.isRight)
    println(either.right.get)
  }

  /**
    * Merge operator will get the value from the either from left or right, just in case you dont care which side is
    */
  @Test def merge(): Unit = {
    val either = getEitherValue(true)
    println(either.merge)
  }


  private def getEitherValue(effect: Boolean): Either[String, String] = {
    if (effect) new Right("Right always good") else new Left("Left never its ok!!!")
  }
}
