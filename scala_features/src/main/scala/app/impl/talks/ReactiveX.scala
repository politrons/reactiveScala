package app.impl.talks

import app.impl.Generic
import org.junit.Test
import rx.lang.scala.Observable
import scala.concurrent.duration._

/**
  * Creating are the operator that create the observables from scratch with 0-N item to be emitted.
  */
class ReactiveX extends Generic[String, Long] {

  @Test def pipeline(): Unit = {

    Observable.from(List("Hello", "Reactive", "Stream", "foo", "world"))
              .filter(word => word != "foo") // Filters
              .delay(100 millis) // Give me some rest
              .map(word => word.toUpperCase) // Transformation
              .onErrorResumeNext(throwable => Observable.just(s"Error:$throwable")) //Error Handling
              .flatMap(word => Observable.just("-") // Composition
                                          .map(item => word.concat(item)))
              .subscribe(word => println(s"item emitted:$word"), // onNext function
                throwable => println(s"An error happens:$throwable", //onError function
                  () => println(s"All emissions finish"))) // onCompleted function


  }


}
