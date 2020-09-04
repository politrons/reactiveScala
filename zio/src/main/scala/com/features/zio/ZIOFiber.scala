package features.zio

import zio.{Queue, Semaphore, Task, ZIO}

object ZIOFiber extends App {

  val main: zio.Runtime[zio.ZEnv] = zio.Runtime.default

  val queue: Queue[Task[Unit]] = main.unsafeRun {
    for {
      semaphore <- Semaphore.make(permits = 2)
      queue <- Queue.bounded[Task[Unit]](1)
      _ <- queue.take.flatMap(program => {
        (for {
          spots <-semaphore.available
          _ <- ZIO.effect(println(s"let's run this program! we have $spots spots"))
          _ <- semaphore.withPermit(program)
        } yield ()).catchAll(t =>{
          println(s"Error in program in Thread ${Thread.currentThread().getName}. Caused by $t")
          ZIO.succeed(())
        }).fork
      }).forever.forkDaemon
    } yield queue
  }

  val program = Task.effect{
    Thread.sleep(1000)
    throw new NullPointerException()
  }

  main.unsafeRun(queue.offer(program))
  Thread.sleep(100)
  main.unsafeRun(queue.offer(program))
  Thread.sleep(100)
  main.unsafeRun(queue.offer(program))
  main.unsafeRun(queue.offer(program))
  main.unsafeRun(queue.offer(program))
  main.unsafeRun(queue.offer(program))
  main.unsafeRun(queue.offer(program))
  main.unsafeRun(queue.offer(program))

  Thread.sleep(2000)

}

