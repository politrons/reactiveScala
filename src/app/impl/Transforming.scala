package app.impl

import java.util.concurrent.Executors

import org.junit.Test
import rx.lang.scala.Observable
import rx.lang.scala.schedulers.ExecutionContextScheduler

import scala.concurrent.ExecutionContext


/**
  * Transforming operators allow us to transform our pipeline and evolve the values passed through the pipeline.
  * Remember that everything that happen inside a pipeline is immutable, so every time that you "modify" an item
  * inside a function of Map or flatMap, actually you´re creating an emitting a new item.
  */
class Transforming extends Generic {

  /**
    * Map operator allow us to evolve the pipeline passing new items through the pipeline.
    * In order to do that use a Function which receive an item and return another item.
    * Remember that everything here is inmutable, so every new item emitted through the pipeline is ALWAYS new
    */
  @Test def map(): Unit = {
    addHeader("Map observable")
    val text = "number mutate to String:"
    val list = getList
    Observable.from(list)
      .map(n => text.concat(String.valueOf(n)))
      .map(s => s.toUpperCase())
      .subscribe(s => println(s))
  }


  /**
    * In case that you want a new pipeline in your current pipeline you can use flatMap,
    * where you can create another Observable which will emit their items, and once it finish,
    * the emitted items will be passed to the previous pipeline.
    *
    */
  @Test def flatMap(): Unit = {
    addHeader("Flat Map")
    val text = "number mutated in flatMap::"
    val list = getList
    Observable.from(list)
      .flatMap(n => Observable.just(n) //--> New pipeline
        .map(n => text.concat(String.valueOf(n))))
      .map(s => s.toUpperCase())
      .subscribe(s => println(s))
  }

  /**
    * This operator allow us to return a new item in case the one emitted was null/empty
    */
  @Test def orElse(): Unit = {
    addHeader("or Else")
    Observable.empty
      .orElse("hello scala world")
      .map(n => n.toUpperCase())
      .subscribe(n => println(n))

  }

  /**
    * Buffer operator create a ArrayBuffer which append all items emitted in the pipeline
    * Once that we have the arrayBuffer we can use the operators to remove some items inside it
    */
  @Test def buffer(): Unit = {
    addHeader("buffer operator")
    Observable.from(getList)
      .toBuffer
      .doOnNext(a => println(a.-(1)))
      .doOnNext(a => println(a.-(2)))
      .doOnNext(a => println(a.-(3)))
      .doOnNext(a => println(a.-(4)))
      .subscribe(n => println(n))
  }

  /**
    * Compose operator emit the observable to the Transformer function and return a new Observable.
    */
  @Test def composeToString(): Unit = {
    addHeader("compose operator")
    Observable.just(1)
      .compose(transformerToString)
      .subscribe(n => println(n))
  }

  /**
    * Compose operator emit the observable to the Transformer function and return a new Observable
    * that will run in another thread.
    */
  @Test def composeToAsync(): Unit = {
    addHeader("compose async operator")
    val executor = ExecutionContext.fromExecutor(Executors.newSingleThreadExecutor)
    val scheduler = ExecutionContextScheduler(executor)
    println("Thread before pipeline:"+Thread.currentThread().getName)
    Observable.just(1)
      .compose(transformerToAsync(scheduler))
        .doOnNext(i=> println("Thread inside pipeline:"+Thread.currentThread().getName))
      .subscribe(n => println(n))
  }

  /**
    * Using the scan operator we can interact with the items emitted in the pipeline.
    * We have to pass to the operator the default first value, and then as second argument we pass
    * the previous item emitted, and the new one.
    */
  @Test def scanInRevertMap():Unit = {
    val map = Map[String, Int]("1"->1, "1"-> 2, "3"-> 1)
    Observable.from(map.toIterable)
      .map(entry =>  Map[Int, String](entry._2 -> entry._1))
      .scan(Map[Int, String]())((m, m1) => m ++ m1)
      .last
      .subscribe(m => println(m))
  }

  def transformerToAsync(scheduler: ExecutionContextScheduler): (Observable[Int]) => Observable[Observable[Int]] = {
    i => Observable.just(i).observeOn(scheduler)
  }

  def transformerToString: (Observable[Int]) => Observable[String] = {
    o => Observable.just("Hello Scala world")
  }

  def getList: List[Int] = {
    List(1, 2, 3, 4, 5)
  }
}
