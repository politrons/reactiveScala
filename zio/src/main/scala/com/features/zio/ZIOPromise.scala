package com.features.zio

import zio.ZIO.sleep
import zio.console.putStrLn
import zio.duration.durationInt
import zio.{IO, Promise}


object ZIOPromise extends App {

  val main: zio.Runtime[zio.ZEnv] = zio.Runtime.default

  main.unsafeRun {
    for {
      promise <- Promise.make[Throwable, String]
      innerProgram = (IO.succeed(s"Logic running in thread ${Thread.currentThread().getName}") <* sleep(1.second)).flatMap(promise.succeed)
      getValueProgram = promise.await.flatMap(value => putStrLn(s"Printing promise output in Thread ${Thread.currentThread().getName}: $value"))
      fiber1 <- innerProgram.fork
      fiber2 <- getValueProgram.fork
    } yield (fiber1 zip fiber2) join
  }
}


