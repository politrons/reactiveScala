package app.impl

import rx.lang.scala.Observable
import implicitObservablesUtils._
import org.junit.Test

class ImplicitObservables {

  @Test def implicitClass(): Unit = {
    Observable.just("Hello observable implicits").customUpperCase.subscribe(s=> println(s))

  }


}