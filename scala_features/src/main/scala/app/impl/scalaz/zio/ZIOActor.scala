package app.impl.scalaz.zio

import app.impl.scalaz.zio.ZIOActor.{Capacity, Permit}
import org.junit.Test
import scalaz.zio.{DefaultRuntime, Queue, Semaphore, ZIO}
import ZIOActor.ActorQueue

/**
  * ZIOActor to provide async back-pressure, bulkhead pattern.
  *
  * This implementation is pretty much the same than Akka Actor with Strong type system.
  * All request will be executed in a fiber(green-thread) from a Executor with a limit of [Capacity] configured by client.
  *
  * We apply [boundedQueue] strategy. Once we reach the maximum executions, we will block the creation of fibers
  * until more job can be processed. Applying this patter we'll have [Back-pressure]
  *
  * We use [Semaphore] to allow a maximum number of threads in this Stream, applying [Bulkhead] pattern.
  * In this way we can only have a specific number of  threads running in this Stream, avoiding Thread starvation.
  * The rest of request it will be set in another Fiber Queue waiting until it can work.
  *
  * To ensure we release the semaphore, even in error cases we use [bracket] which provide a
  * before-after-run task where we acquire the semaphore in the before, and we release in the after
  */
object ZIOActor extends DefaultRuntime {

  trait Strategy

  case class Capacity(value: Int) extends AnyVal

  case class Permit(value: Long) extends AnyVal

  var inboxStrategy = Queue[ZIO[Any, Nothing, Any]]

  def createActor(capacity: Capacity = Capacity(100),
                  permit: Permit = Permit(10)): Queue[ZIO[Any, Nothing, Any]] = unsafeRun {
    for {
      semaphore <- Semaphore.make(permits = permit.value)
      query <- Queue.bounded[ZIO[Any, Nothing, Any]](capacity.value)
      _ <- query.take.flatMap(program => {
        semaphore.acquire.bracket(_ => semaphore.release) { _ =>
          program
        }
      }).forever.fork
    } yield query

  }

  /**
    * Extension method class to provide a DSL to interact once the actor is created by [createActor]
    */
  implicit class ActorQueue(queue: Queue[ZIO[Any, Nothing, Any]]) {

    def tell(program: ZIO[Any, Nothing, Any]): Unit = {
      unsafeRun(queue.offer(program))
    }

    def !(program: ZIO[Any, Nothing, Any]): Unit = {
      unsafeRun(queue.offer(program))
    }
  }

}

class ZIOActor extends DefaultRuntime {

  val myZioActor: Queue[ZIO[Any, Nothing, Any]] = ZIOActor.createActor(Capacity(1000), Permit(15))

  @Test
  def actorTell(): Unit = {
    runActorTellProgram("Hello Actor in ZIO World")
    runActorTellProgram("Reactive and Pure functional programing")
    Thread.sleep(1000)
  }

  private def runActorTellProgram(message: _root_.java.lang.String) = {
    val helloWorldProgram: ZIO[Any, Nothing, Unit] =
      (for {
        message <- ZIO.effect(message + " !!!")
        upper <- ZIO.effect(message.toUpperCase)
        _ <- ZIO.succeed(println(upper))
      } yield ()).catchAll(t => {
        println("Unhandled Error")
        ZIO.succeed(())
      })

    //Normal
    myZioActor.tell(helloWorldProgram)

    //Sugar style
    myZioActor ! helloWorldProgram

  }
}
