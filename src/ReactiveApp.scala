import rx.lang.scala.Observable

/**
  * Created by pabloperezgarcia on 26/6/16.
  */


object ReactiveApp {

  def main(args: Array[String]) {

    val transforming = new Transforming();
    transforming.map();
    transforming.flatMap();

    val creating = new Creating();
    creating.interval();
    creating.deferObservable();
//    println("*****Subscribe******\n")
//    subscribe();
//    println("*****Filter******\n")
//    filter();
//
//    map();
//
//    flatMap()
  }


  def filter(): Unit = {
    Observable.from(List(1, 2, 3))
      .filter(n => n == 1)
      .map(n => n * 10)
      .foreach(println(_))
  }



}



