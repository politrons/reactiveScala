import java.util.concurrent.TimeUnit

import rx.Subscription
import rx.lang.scala.Observable
import rx.lang.scala.schedulers.NewThreadScheduler
import rx.observers.TestSubscriber

import scala.concurrent.duration.{Duration, FiniteDuration}

/**
  * Created by pabloperezgarcia on 26/6/16.
  */
class Creating {

  def interval(): Unit = {
    println("\nCreating observable")
    val subscription = Observable.interval(createDuration(100))
      .map(n => "New item emitted:" + n)
      .doOnNext(s => print("\n" + s))
      .subscribe();
    new TestSubscriber[Subscription].awaitTerminalEvent(1000, TimeUnit.MILLISECONDS);
  }

  def subscribe(): Unit = {
    Observable.from(List(1, 2, 3))
      .subscribeOn(NewThreadScheduler())
      .subscribe(println(_))
  }

  def createDuration(time: Long): FiniteDuration = {
    Duration.create(time, TimeUnit.MILLISECONDS)
  }

}
