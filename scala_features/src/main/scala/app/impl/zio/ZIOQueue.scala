package app.impl.zio

import zio.{Queue, Semaphore, Task, ZIO}

object ZIOQueue extends App {

  val main: zio.Runtime[zio.ZEnv] = zio.Runtime.default

  val queue: Queue[Task[Unit]] = main.unsafeRun {
    for {
      semaphore <- Semaphore.make(permits = 100)
      queue <- Queue.bounded[Task[Unit]](100)
      _ <- queue.take.flatMap(program => {
        for {
          _ <- semaphore.withPermit(program)
        } yield ()
      }).forever.forkDaemon
    } yield queue
  }

  val program = Task.effect(println("hello world"))

  main.unsafeRun(queue.offer(program))

  Thread.sleep(1000)

}
