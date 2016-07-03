package app.impl


import java.util.concurrent.Executors

import org.junit.Test
import rx.lang.scala.Observable
import rx.lang.scala.schedulers.ExecutionContextScheduler

import scala.concurrent.ExecutionContext;

/**
  * Scheduler is an object that schedules units of work, which means set in which thread the execution will happens.
  * We can specify if we want that the whole pipeline will be executed in a specific thread subscribeOn
  * Or if we want to just execute some steps in a specific thread observerOn
  */
class Scheduler extends Generic[String] {

  val executor = ExecutionContext.fromExecutor(Executors.newSingleThreadExecutor)
  val scheduler = ExecutionContextScheduler(executor)

  /**
    * subscribeOn specify in which thread the pipeline will executed once the observer subscribe it
    * Shall print
    *
    *   Thread out of the pipeline:main
    *   Thread in pipeline:pool-1-thread-1
    */
  @Test def subscribeOn(): Unit = {
    addHeader("subscribeOn observable")
    println("Thread out of the pipeline:" + Thread.currentThread().getName)
    Observable.just("Hello async scala world")
      .subscribeOn(scheduler)
      .doOnNext(s =>
        println("Thread in pipeline:" + Thread.currentThread().getName))
      .subscribe(n => println(n))
  }

  /**
    * observerOn operator establish in the pipeline once is set that the rest of steps with process the items in the thread specify
    * Here we specify that the execution of steps in this threads.
    *
    * 1 doOnNext --> Main
    * 2 doOnNext --> pool-2-thread-1
    * 3 doOnNext --> pool-3-thread-1
    */
  @Test def observerOn(): Unit = {

    val executor = ExecutionContext.fromExecutor(Executors.newSingleThreadExecutor)
    val executor1 = ExecutionContext.fromExecutor(Executors.newSingleThreadExecutor)
    val scheduler = ExecutionContextScheduler(executor)
    val scheduler1 = ExecutionContextScheduler(executor1)

    addHeader("observerOn observable")
    Observable.just("hello async scala world")
      .doOnNext(s =>
        println("1 Step-Thread in pipeline:" + Thread.currentThread().getName))
      .observeOn(scheduler)
      .doOnNext(s =>
        println("2 Step-Thread in pipeline:" + Thread.currentThread().getName))
      .observeOn(scheduler1)
      .doOnNext(s =>
        println("2 Step-Thread in pipeline:" + Thread.currentThread().getName))
      .subscribe(n => println(n))

  }

}
