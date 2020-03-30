package app.impl.http

import com.twitter.util.{Await, Future}
import org.junit.Test
import zio.{Has, ZIO}

/**
 * Here we create our programs using the DSL of HttpHedgedClient, and using the different provide
 * like finagle
 */
class HedgedClientRunner {

  @Test
  def withSugarSyntax(): Unit = {
    val httpGetProgram: ZIO[Has[HttpHedgedClient.Service], Nothing, Future[Any]] =
      for {
        _ <- HttpHedgedClient.Get()
        _ <- HttpHedgedClient.Uri("/")
        _ <- HttpHedgedClient.Host("www.google.com:80")
        _ <- HttpHedgedClient.Timeout(5000)
        _ <- HttpHedgedClient.Hedged(10)
        future <- HttpHedgedClient.Run()
      } yield future

    val programResponse: Future[Any] = HttpHedgedClient.runtime.unsafeRun {
      httpGetProgram.provideCustomLayer(HttpHedgedClient.finagleEngine)
    }
    println(Await.ready(programResponse))
  }

  @Test
  def noSugarSyntax(): Unit = {
    val httpGetProgram: ZIO[Has[HttpHedgedClient.Service], Nothing, Future[Any]] =
      HttpHedgedClient.Get()
        .flatMap(_ => HttpHedgedClient.Uri("/"))
        .flatMap(_ => HttpHedgedClient.Host("www.google.com:80"))
        .flatMap(_ => HttpHedgedClient.Timeout(5000))
        .flatMap(_ => HttpHedgedClient.Hedged(10))
        .flatMap(_ => HttpHedgedClient.Run())

    val programResponse: Future[Any] = HttpHedgedClient.runtime.unsafeRun {
      httpGetProgram.provideCustomLayer(HttpHedgedClient.finagleEngine)
    }
    println(Await.ready(programResponse))
  }

}