import rx.lang.scala.Observable

/**
  * Created by pabloperezgarcia on 26/6/16.
  */
class Transforming extends Generic {

  def map(): Unit = {
    addHeader("Map observable")
    val text = "number mutate to String:"
    val list = List(1, 2, 3, 4, 5)
    Observable.from(list)
      .map(n => text.concat(String.valueOf(n)))
      .map(s => s.toUpperCase())
      .subscribe(s => println(s))
  }


  def flatMap(): Unit = {
    addHeader("Flat Map")
    val text = "number mutated in flatMap::"
    val list = List(1, 2, 3, 4, 5)
    Observable.from(list)
      .flatMap(n => Observable.just(n)
        .map(n => text.concat(String.valueOf(n))))
      .map(s => s.toUpperCase())
      .subscribe(s => println(s))
  }


}
