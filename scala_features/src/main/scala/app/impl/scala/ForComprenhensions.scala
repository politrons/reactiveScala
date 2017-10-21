package app.impl.scala

import java.lang.Thread._

import org.junit.Test

import scala.concurrent.Future
import scala.util.{Failure, Success}

/**
  * Created by pabloperezgarcia on 17/8/16.
  */
class ForComprenhensions {


  @Test def comprenhension(): Unit = {
    println(even(0, 10))
    println(toList(0, 10))
  }

  /**
    * using yield we can accumulate the items from the for iterated,
    * and return a list with those items that pass the filter
    */
  def even(from: Int, to: Int): List[Int] = for (i <- List.range(from, to) if i % 2 == 0) yield i

  /**
    * Every item emitted in the for is added into a List using yield
    *
    * @param from
    * @param to
    * @return
    */
  def toList(from: Int, to: Int): List[Int] = for (i <- List.range(from, to)) yield i


  @Test
  def filters = {
    val pNames = for (
      name <- getNames
      if name.contains("P")
    ) yield name

    println(pNames)
  }

  private def getNames = {
    List("Pablo", "Luis", "Javier", "Paul", "Esther")
  }

  import scala.concurrent.ExecutionContext.Implicits.global

  @Test
  def parallel = {
    val f1 = Future { sleep(800); 1 }
    val f2 = Future { sleep(200); 2 }
    val f3 = Future { sleep(400); "1" }

    // (b) run them simultaneously in a for-comprehension
    val result = for {
      r1 <- f1
      r2 <- f2
      r3 <- f3
    } yield (r1 + r2 + r3.toInt)

    // (c) do whatever you need to do with the result
    result.onComplete {
      case Success(x) => println(s"\nresult = $x")
      case Failure(e) => e.printStackTrace
    }

    Thread.sleep(1000)
  }

}