package app.impl

import org.junit.Test
import rx.lang.scala.Observable

/**
  * Combining operators are used in order to combine items emitted by the pipeline.
  */
class Combining extends Generic {


  /**
    * Merge operator get two observables and combine together, returning both items emitted together
    */
  @Test def merge(): Unit = {
    addHeader("Observable merge")
    Observable.just("hello")
      .merge(Observable.just(" scala"))
      .merge(Observable.just(" world!"))
      .reduce((s, s1) => s.concat(s1))
      .map(s => s.toUpperCase).subscribe(s => println(s));
  }

  /**
    * Zip operator allow us to chain several observables and once that all of them has been emitted,
    * return all them at the same time.
    */
  @Test def zip(): Unit = {
    addHeader("Observable zip")
    Observable.just("hello")
      .zip(Observable.just(" scala"))
      .zip(Observable.just(" world!"))
      .map(s => s._1._1.concat(s._1._2).concat(s._2).toUpperCase)
      .subscribe(s => println(s));
  }

  /**
    * This creator operator allow us create the observable with three observables, if you want more than 3
    * you can always implement your own function.
    */
  @Test def zipWith3(): Unit = {
    addHeader("Observable zip with 3")
    Observable.zip(Observable.just("hello"), Observable.just(" scala"), Observable.just(" world"))
      .map(sentences => sentences._1.toUpperCase.concat(sentences._2.toUpperCase.concat(sentences._3.toUpperCase)))
      .subscribe(s => println(s))
  }

//  def concat(): Unit = {
//    addHeader("Observable concat")
//    Observable.just("hello").concatMap(Observable.just(""))
//      .subscribe(n => println(n))
//
//  }


}
