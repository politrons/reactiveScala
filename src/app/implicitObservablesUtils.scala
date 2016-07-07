import java.util.concurrent.Executors

import rx.lang.scala.Observable
import rx.lang.scala.schedulers.ExecutionContextScheduler

import scala.concurrent.ExecutionContext

/**
  * Created by pabloperezgarcia on 6/7/16.
  */
package object implicitObservablesUtils {

  val executor = ExecutionContext.fromExecutor(Executors.newSingleThreadExecutor)
  val scheduler = ExecutionContextScheduler(executor)

  implicit class StringUtils(o: Observable[String]) {

    def async: Observable[Any] = o.subscribeOn(scheduler)

    def customUpperCase: Observable[String] = o.map(x => x.toUpperCase)

  }

  implicit class MapUtils(o: Observable[Map[Any, Any]]) {

    def revertListValueAsKey(m: Map[Int, List[Int]]): Observable[Map[Int, Int]] = {
      Observable.from(m)
        .map(entry => Map[Int, Int](sumListValues(entry) -> entry._1))
        .scan(Map[Int, Int]())((m, m1) => m ++ m1)
    }

  }

  def sumListValues(entry: (Int, List[Int])): Int = {
    entry._2.sum
  }
}
