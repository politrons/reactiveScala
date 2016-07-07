package app.impl

import implicitObservablesUtils._
import org.junit.Test
import rx.lang.scala.Observable

class ImplicitObservables {

  @Test def implicitStringClass(): Unit = {
    Observable.just("Hello observable implicits")
      .customUpperCase
      .subscribe(s => println(s))

  }

  @Test def implicitObservableClass(): Unit = {
    val map = Map[Int, List[Int]](1 -> List[Int](1, 2), 2 -> List[Int](3, 4))
    Observable.empty.revertListValueAsKey(map).last
      .subscribe(s => println(s))
  }


}