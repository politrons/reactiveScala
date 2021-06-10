package com.features.zio

import zio.IO
import zio.stm.{STM, TRef}

/**
  * Using [STM] we can guarantee transactional effect in a concurrent access in pure functional world.
  * Everything inside a STM program only can deal with Transactional types like
  * TRef[T] which all actions against him are happening locking the element and propagate
  * the change in the element for the concurrent access with the new value.
  *
  * In this example I update sentence in parallel and we can see how one of the STM using the same instance
  * TRef[String] obtain the change of the other process that was perform before.
  *
  * Having this we can guarantee access without concurrent access issues.
  */
object ZIOStm extends App {

  val main: zio.Runtime[zio.ZEnv] = zio.Runtime.default

  val toUpperCaseZ: TRef[String] => STM[String, String] = sentence =>
    for {
      output <- sentence.updateAndGet(s => s.toUpperCase)
    } yield output

  val concatWord: (TRef[String], String) => STM[String, String] = (sentence, word) =>
    for {
      _ <- sentence.update(existing => existing + word)
      output <- sentence.get
    } yield output

  val program: IO[String, String] = for {
    sentence <- STM.atomically(TRef.make("hello transactional"))
    fiber1 <- STM.atomically(concatWord(sentence, " world")).fork
    fiber2 <- STM.atomically(concatWord(sentence, " again")).fork
    fiber3 <- STM.atomically(toUpperCaseZ(sentence)).fork
    res <- fiber1.join
    res1 <- fiber2.join
    res2 <- fiber3.join
  } yield s"$res1 -> $res -> $res2"

  println(main.unsafeRun(program))

}
