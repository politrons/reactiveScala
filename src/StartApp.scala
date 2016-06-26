import rx.lang.scala.Observable
import rx.lang.scala.schedulers.NewThreadScheduler

/**
  * Created by pabloperezgarcia on 26/6/16.
  */


object StartApp {
  def main(args: Array[String]) {
    subscribe();
    println("\n")
    filter();
    println("\n")
    map();
  }

  def map(): Unit = {
    Observable.from(List(1, 2, 3, 4, 5))
      .filter(_ >= 3)
      .map(_ = "number mutate to String: " + _)
      .foreach(println(_))
  }

  def filter(): Unit = {
    Observable.from(List(1, 2, 3))
      .filter(_ == 1)
      .map(_ * 10)
      .foreach(println(_))
  }

  def subscribe(): Unit = {
    Observable.from(List(1, 2, 3))
      .subscribeOn(NewThreadScheduler())
      .subscribe(println(_))
  }


}



