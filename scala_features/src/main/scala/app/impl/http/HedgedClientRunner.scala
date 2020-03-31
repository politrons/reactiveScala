package app.impl.http

import app.impl.http.HttpHedgedClient.{HttpClientInfo, Service}
import org.junit.Test
import zio.{Has, ZIO, ZLayer}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.concurrent.{Await, Future}

/**
 * Here we create our programs using the DSL of HttpHedgedClient, and using the different provide
 * like finagle, Akka http or even a Custom client engine.
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

  /**
   * One of the coolest feature of this connector it's to allow a custom engine implementation
   * by the consumer without have to change the DSL or Program already created.
   */
  @Test
  def customEngine(): Unit = {

    val customEngine = ZLayer.succeed(new Service {
      override def getHttpClient: HttpHedgedClient.HttpClientInfo = new HttpClientInfo {
        override def getHedged: Int = 1
      }

      override def withUri(uri: String): Unit = {}

      override def withHost(host: String): Unit = {}

      override def withGetMethod(): Unit = {}

      override def withPostMethod(): Unit = {}

      override def withBody(body: String): Unit = {}

      override def withTimeout(time: Long): Unit = {}

      override def withHedged(times: Int): Unit = {}

      override def run(): Future[Any] = {
        Future {
          "Hello from custom engine. Pretty cool for testing right?"
        }
      }
    })
    val programResponse: Future[Any] = HttpHedgedClient.runtime.unsafeRun {
      programWithSugar.provideCustomLayer(customEngine)
    }
    println(Await.ready(programResponse, 10 seconds))

  }

}