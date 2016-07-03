package app.impl

import org.junit.Test
import rx.lang.scala.Observable



class Filtering extends Generic[String, Long] {

  /**
    * Filter operator just apply a Predicate function to determine when allow the item to go through the pipeline
    */
  @Test def filter(): Unit = {
    addHeader("Observable filter")
    Observable.from(List(1, 2, 3, 4, 5))
      .filter(isBiggerThan(2))
      .map(n => n * 10)
      .filter(isBiggerThan(20))
      .map(transformToString)
      .filter(isString)
      .subscribe(println(_))
  }

  /**
    * Distinct operator just filter all those items that already pass through the pipeline
    */
  @Test def distinct(): Unit = {
    addHeader("Observable distinct")
    Observable.from(List(1, 2, 2, 1, 3, 3, 4, 5))
      .distinct
      .subscribe(n => println(n))
  }

  /**
    * Drop operator will drop items to be emitted by the observable to the observer under some circumstances.
    * We can use just the number of items to drop or use a Predicate function to determine when skip it
    * Shall print 5
    */
  @Test def drop(): Unit = {
    addHeader("Observable drop")
    Observable.from(List(1, 2, 3, 4, 5))
      .drop(3)
      .dropWhile(n => n <= 4)
      .subscribe(n => println(n))
  }

  /**
    * Take operator will take items to be emitted by the observable to the observer under some circumstances.
    * We can use just the number of items to take, or use an predicate function to determine when take it.
    * Shall print 4, 5
    */
  @Test def take(): Unit = {
    addHeader("Observable take")
    Observable.from(List(1, 2, 3, 4, 5))
      .take(2)
      .takeWhile(n => n > 3)
      .subscribe(n => println(n))

  }

  private def isBiggerThan(number: Integer): (Int) => Boolean = {
    n => n > number
  }

  private def transformToString: (Int) => String = {
    n => String.valueOf(n)
  }

  private def isString: (String) => Boolean = {
    s => s.isInstanceOf[String]
  }
}
