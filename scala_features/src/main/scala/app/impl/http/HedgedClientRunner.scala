package app.impl.http

import org.junit.Test
import zio.{Has, ZIO}

import scala.concurrent.{Await, Future}
import scala.concurrent.duration._

/**
 * Here we create our programs using the DSL of HttpHedgedClient, and using the different provide
 * like finagle or Akka http
 */
class HedgedClientRunner {

  /**
   * DSL program to be used by different engines using sugar for comprehension.
   */
  val programWithSugar: ZIO[Has[HttpHedgedClient.Service], Nothing, Future[Any]] =
    for {
      _ <- HttpHedgedClient.Get()
      _ <- HttpHedgedClient.Host("www.google.com:80")
      _ <- HttpHedgedClient.Timeout(1000)
      _ <- HttpHedgedClient.Hedged(4)
      future <- HttpHedgedClient.Run()
    } yield future

  /**
   * DSL program to be used by different engines.
   */
  val programWithoutSugar: ZIO[Has[HttpHedgedClient.Service], Nothing, Future[Any]] =
    HttpHedgedClient.Get()
      .flatMap(_ => HttpHedgedClient.Uri("/"))
      .flatMap(_ => HttpHedgedClient.Host("www.google.com:80"))
      .flatMap(_ => HttpHedgedClient.Timeout(1000))
      .flatMap(_ => HttpHedgedClient.Hedged(4))
      .flatMap(_ => HttpHedgedClient.Run())

  /**
   * In this example we use programWithSugar structure with finagleEngine as behavior
   */
  @Test
  def finagleEngineWithSugarSyntax(): Unit = {
    val programResponse: Future[Any] = HttpHedgedClient.runtime.unsafeRun {
      programWithSugar.provideCustomLayer(HttpHedgedClient.finagleEngine)
    }
    println(Await.ready(programResponse, 10 seconds))
  }

  @Test
  def finagleEngineWithoutSugarSyntax(): Unit = {
    val programResponse: Future[Any] = HttpHedgedClient.runtime.unsafeRun {
      programWithoutSugar.provideCustomLayer(HttpHedgedClient.finagleEngine)
    }
    println(Await.ready(programResponse, 10 seconds))
  }

  /**
   * In this example we use programWithSugar structure with akkaEngine as behavior
   */
  @Test
  def akkaEngine(): Unit = {
    val programResponse: Future[Any] = HttpHedgedClient.runtime.unsafeRun {
      programWithSugar.provideCustomLayer(HttpHedgedClient.akkaEngine)
    }
    println(Await.ready(programResponse, 10 seconds))
  }

}