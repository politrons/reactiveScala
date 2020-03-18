package app.impl.scalaz.zio

import org.junit.Test
import scalaz.zio._


object ZIOStreamFeature extends DefaultRuntime {

  case class ZIOStream[T, R](queueProgram: UIO[Queue[T]], program: T => R)

  def source[T, R](queueStream: UIO[Queue[T]]): ZIOStream[T, R] = {
    ZIOStream(queueStream, null)
  }

  implicit class ZIOStreamAPI[T, R](stream: ZIOStream[T, R]) {

    def map[R](f: T => R): ZIOStream[T, R] = {
      stream.copy(program = f)
    }

    def subscribe(): Unit = {
      val unit = for {
        semaphore <- Semaphore.make(permits = 10)
        zioQueue <- ZIO.environment[UIO[Queue[T]]]
        queue <- zioQueue
        _ <- queue.take.flatMap(event => {
          semaphore.acquire.bracket(_ => semaphore.release) { _ =>
            for {
              _ <- ZIO.effect(stream.program(event))
            } yield ()
          }
        }).catchAll(_ => ZIO.succeed())
          .forever.fork
      } yield ()
      unit.provide(stream.queueProgram)
    }

    //
    //    def flatMap[R](f: T => ZIOStream[R]): ZIOStream[R] = {
    //      ZIOStream {
    //        for {
    //          value <- stream.program
    //          program <- f(value).program
    //        } yield program
    //      }
    //    }
  }

}

class ZIOStreamFeature extends DefaultRuntime {


//  def createActor[T](capacity: Capacity = Capacity(100),
//                     permit: Permit = Permit(10),
//                     strategy: InboxStrategy = Bounded()): ZIOActor[T] = unsafeRun {
//    for {
//      semaphore <- Semaphore.make(permits = permit.value)
//      queue <- Queue.bounded[ZIO[Any, Nothing, T]](capacity.value)
//      zioActor <- ZIO.succeed(ZIOActor[T](queue))
//      _ <- queue.take.flatMap(program => {
//        semaphore.acquire.bracket(_ => semaphore.release) { _ =>
//          for {
//            response <- program._2
//            _ <- ZIO.effect(program._1.success(response))
//          } yield ()
//        }
//      }).forever.fork
//    } yield zioActor

//  }

  @Test
  def runStream(): Unit = {

    val queue = Queue.bounded[String](10000)


    ZIOStreamFeature.source(queue)
      .map(value => println(value.toUpperCase))
      .subscribe()

    unsafeRun(queue).offer("Hello stream world")
    unsafeRun(queue).offer("Hello again")


    Thread.sleep(2000)

  }

}
