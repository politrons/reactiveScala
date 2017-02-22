package app.impl.rx

import java.util.concurrent.TimeUnit

import app.impl.Generic
import org.junit.Test
import rx.Subscription
import rx.lang.scala.Observable
import rx.observers.TestSubscriber

/**
  * Creating are the operator that create the observables from scratch with 0-N item to be emitted.
  */
class Creating extends Generic[String, Long] {

  /**
    * empty operator create an observable with Nothing class
    */
  @Test def empty(): Unit = {
    addHeader("empty observable")
    Observable.empty
      .subscribe(n => println(n))
  }

  /**
    * Just create an observable with just 1 item to emit
    */
  @Test def just(): Unit = {
    addHeader("Just observable")
    Observable.just("Hello scala world")
      .subscribe(n => println(n))
  }

  /**
    * From create an observable with a collection of N items to emit
    */
  @Test def from(): Unit = {
    addHeader("From observable")
    Observable.from(List[Int](1, 2, 3, 4, 5))
      .subscribe(n => println(n))
  }

  /**
    * Interval operator just emmit an item through the pipeline every Time specify
    */
  @Test def interval(): Unit = {
    addHeader("Interval observable")
    Observable.interval(createDuration(100))
      .map(n => "New item emitted:" + n)
      .doOnNext(s => print("\n" + s))
      .subscribe();
    new TestSubscriber[Subscription].awaitTerminalEvent(1000, TimeUnit.MILLISECONDS);
  }

  /**
    * Normally when you create an observable with just or create, The observable is created with the value that passed at that point,
    * and then once that a observer subscribe, the value it´s just passed through the pipeline.
    * Sometimes that´s not the desirable, since maybe we dont want to  create the observable at that point, only when an observer subscribe to it.
    * Defer it will wait to create the observable with the value when we subscribe our observer.
    * Basically create this Observable that wrap the observable that we want to create only when we subscribe to the observable.
    * Shall print
    *
    * Hello scala world
    *
    */
  @Test def defer(): Unit = {
    addHeader("Defer Observable")
    var text = "Foo"
    val deferObservable = Observable.defer(Observable.just(text))
    text = "Hello scala world"
    deferObservable.subscribe(s => println(s))
  }

}
