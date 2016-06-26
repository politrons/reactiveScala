import java.util.concurrent.TimeUnit

import rx.Subscription
import rx.lang.scala.Observable
import rx.observers.TestSubscriber

/**
  * Created by pabloperezgarcia on 26/6/16.
  */
class Creating extends Generic{

  def interval(): Unit = {
    addHeader("Interval observable")
    Observable.interval(createDuration(100))
      .map(n => "New item emitted:" + n)
      .doOnNext(s => print("\n" + s))
      .subscribe();
    new TestSubscriber[Subscription].awaitTerminalEvent(1000, TimeUnit.MILLISECONDS);
  }

  def deferObservable(): Unit = {
    addHeader("Defer Observable")
    var text = "Foo"
    val deferObservable = Observable.defer(Observable.just(text))
    text = "Hello scala world"
    deferObservable.subscribe(s => println(s))
  }

}
