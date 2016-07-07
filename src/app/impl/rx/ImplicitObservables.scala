package app.impl.rx

import implicitObservablesUtils._
import org.junit.Test
import rx.lang.scala.Observable

class ImplicitObservables {

  @Test def implicitStringClass(): Unit = {
    Observable.just("Hello observable implicits")
      .customUpperCase
      .subscribe(s => println(s))

  }

  @Test def implicitObservableClass(){
    val map = Map[Int, List[Int]](1 -> List[Int](1, 2), 2 -> List[Int](3, 4))
    Observable.empty.revertTotalValueAsKey(map).last
      .subscribe(s => println(s))
  }

  @Test def changeToOperator() {
    Observable.just(1).changeTo("Hello scala!")
      .subscribe(s => println(s))
  }


}