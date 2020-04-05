package app.impl.zio

import app.impl.zio.ZIOActorSystem.{Capacity, Permit, ZIOActor, createActor}
import org.junit.Test
import scalaz.zio.{DefaultRuntime, Queue, Semaphore, UIO, ZIO}

import scala.concurrent.{Await, Future, Promise}
import scala.concurrent.duration._

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
object ZIOActorSystem extends DefaultRuntime {

  trait InboxStrategy

  /**
    * ADT of the Actor
    */
  case class Bounded() extends InboxStrategy

  case class Sliding() extends InboxStrategy

  case class Dropping() extends InboxStrategy

  case class Capacity(value: Int) extends AnyVal

  case class Permit(value: Long) extends AnyVal

  case class ZIOActor[T](inbox: Queue[(Promise[T], ZIO[Any, Nothing, T])])

  /**
    * Function to configure which strategy for the inbox it will be configured
    */
  def inboxStrategy[T]: Capacity => InboxStrategy => UIO[Queue[(Promise[T], ZIO[Any, Nothing, T])]] = capacity => {
    case Bounded() => Queue.bounded[(Promise[T], ZIO[Any, Nothing, T])](capacity.value)
    case Sliding() => Queue.sliding[(Promise[T], ZIO[Any, Nothing, T])](capacity.value)
    case Dropping() => Queue.dropping[(Promise[T], ZIO[Any, Nothing, T])](capacity.value)
  }

  /**
    * Factory function to create the actor with the Possibility of some optional configuration
    *
    * @param capacity of the inbox. Once we reach we will Apply the [InboxStrategy]
    * @param permit   of max thread that can work async with this actor
    * @param strategy of what to do with the Queue once we reach the maximum
    * @return
    */
  def createActor[T](capacity: Capacity = Capacity(100),
                  permit: Permit = Permit(10),
                  strategy: InboxStrategy = Bounded()): ZIOActor[T] = unsafeRun {
    for {
      semaphore <- Semaphore.make(permits = permit.value)
      queue <- inboxStrategy[T](capacity)(strategy)
      zioActor <- ZIO.succeed(ZIOActor[T](queue))
      _ <- queue.take.flatMap(program => {
        semaphore.acquire.bracket(_ => semaphore.release) { _ =>
          for {
            response <- program._2
            _ <- ZIO.effect(program._1.success(response))
          } yield ()
        }
      }).forever.fork
    } yield zioActor

  }

  /**
    * Extension method class to provide a DSL to interact once the actor is created by [createActor]
    */
  implicit class ActorQueue[T](zioActor: ZIOActor[T]) {

    def tell(program: ZIO[Any, Nothing, T]): Unit = {
      unsafeRun(zioActor.inbox.offer((Promise[T], program)))
    }

    def !(program: ZIO[Any, Nothing, T]): Unit = {
      unsafeRun(zioActor.inbox.offer((Promise[T], program)))
    }

    def ask(program: ZIO[Any, Nothing, T]): Future[T] = {
      val promise = Promise[T]
      unsafeRun(zioActor.inbox.offer((promise, program)))
      promise.future
    }

    def ?(program: ZIO[Any, Nothing, T]): Future[T] = {
      val promise = Promise[T]
      unsafeRun(zioActor.inbox.offer((promise, program)))
      promise.future
    }
  }
}


class ZIOActor extends DefaultRuntime {

  val myZioActorUnit: ZIOActorSystem.ZIOActor[Unit] = createActor[Unit](Capacity(1000), Permit(15))

  @Test
  def actorTell(): Unit = {
    runActorTellProgram("Hello Actor in ZIO World")
    runActorTellProgram("Reactive and Pure functional programing")
    Thread.sleep(1000)
  }

  /**
    * A test program that it send to the actor using Fire & Forget patter with [tell] or [!] as it does in Akka
    */
  private def runActorTellProgram(message: String) = {
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
    myZioActorUnit.tell(helloWorldProgram)

    //Sugar style
    myZioActorUnit ! helloWorldProgram

  }

  val myZioActorString: ZIOActorSystem.ZIOActor[String] = createActor[String](Capacity(1000), Permit(15))

  @Test
  def actorAsk(): Unit = {
    runActorAskProgram("Hello Actor in Future ZIO World")
    runActorAskProgram("Reactive and Pure functional programing in Future")
    Thread.sleep(1000)
  }

  /**
    * A test program that it send to the actor using Fire & Forget patter with [tell] or [!] as it does in Akka
    */
  private def runActorAskProgram(message: String) = {
    val helloWorldProgram: ZIO[Any, Nothing, String] =
      (for {
        message <- ZIO.effect(message + " !!!")
        upper <- ZIO.effect(message.toUpperCase)
      } yield upper).catchAll(t => {
        ZIO.succeed("Unhandled Error")
      })

    //Normal
    val future = myZioActorString.ask(helloWorldProgram)
    val value = Await.result(future, 10 seconds)
    println(value)
    //Sugar style
    val future1 = myZioActorString ? helloWorldProgram
    val value1 = Await.result(future1, 10 seconds)
    println(value1)
  }
}
