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

  implicit class StringImprovements(o: Observable[String]) {

    def defer(s: String): Observable[String] = o.map(x => s)

    def async: Observable[Any] = o.subscribeOn(scheduler)

    def customUpperCase: Observable[String] = o.map(x=> x.toUpperCase)

  }


}
