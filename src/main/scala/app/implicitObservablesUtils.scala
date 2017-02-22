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

    def mergeStrings(a: Observable[String], b: Observable[String]): Observable[String] = {
      o.merge(a).merge(b)
    }

  }

  implicit class MapUtils(o: Observable[Any]) {

    def revertTotalValueAsKey(m: Map[Int, List[Int]]): Observable[Map[Int, Int]] = {
      Observable.from(m)
        .map(entry => Map[Int, Int](entry._2.sum -> entry._1))
        .scan(Map[Int, Int]())((m, m1) => m ++ m1)
    }

    def changeTo(a: Any): Observable[Any] = {
      o.map(x => a)
    }

  }

}
