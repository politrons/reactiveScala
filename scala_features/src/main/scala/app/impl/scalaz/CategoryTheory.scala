package app.impl.scalaz

import org.junit.Test
import scalaz._
import Scalaz._

class CategoryTheory {

  case class Bla(value: Int)

  case class Foo(a: Int, b: Int)


  @Test
  def applicativeOptionSugar: Unit = {
    val o1: Option[Int] = Some(100)
    val o2: Option[Int] = Some(50)
    val o3: Option[Int] = None

    val total = (o1 |@| o2) (Foo)
    println(total)

    val totalLeft = (o1 |@| o3) (Foo)
    println(totalLeft)

    val totalSum = o1 |+| o2
    println(totalSum)

    val totalSumNone = o1 |+| o3
    println(totalSumNone)

    val totalSumNoneWrongOrder = o3.map(value => value + 100)
    println(totalSumNoneWrongOrder)
  }

  @Test
  def applicativeEitherSugar: Unit = {
    val o1: \/[String, Int] = \/-(100)
    val o2: \/[String, Int] = \/-(50)
    val o3: \/[String, Int] = -\/("ups")

    val total = (o1 |@| o2) (Foo)
    println(total)

    val totalLeft = (o1 |@| o3) (Foo)
    println(totalLeft)

    //transform right/left
    val rightValue = o1.map(value => value + 100)
    println(rightValue)

    val leftValue = o3.map(value => value + 100)
    println(leftValue)

  }

  @Test
  def applicativeEitherNoSugar: Unit = {
    val o1: Either[String, Int] = Right(100)
    val o2: Either[String, Int] = Right(50)
    val o3: Either[String, Int] = Left("ups")

    val total = for {
      v1 <- o1
      v2 <- o2
    } yield Foo(v1, v2)
    println(total)

    val totalLeft = for {
      v1 <- o1
      v2 <- o3
    } yield Foo(v1, v2)
    println(totalLeft)

  }

}
