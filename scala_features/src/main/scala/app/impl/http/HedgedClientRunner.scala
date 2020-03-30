package app.impl.http

import com.twitter.util.{Await, Future}
import zio.{Has, ZIO}

object HedgedClientRunner extends App {

  private val httpGetProgram: ZIO[Has[HttpHedgedClient.Service], Nothing, Future[Any]] = for {
    _ <- HttpHedgedClient.Get()
    _ <- HttpHedgedClient.Uri("/")
    _ <- HttpHedgedClient.Host("www.google.com:80")
    _ <- HttpHedgedClient.Timeout(5000)
    _ <- HttpHedgedClient.Hedged(10)
    future <- HttpHedgedClient.Run()
  } yield future

  private val programResponse: Future[Any] = HttpHedgedClient.runtime.unsafeRun {
    httpGetProgram.provideCustomLayer(HttpHedgedClient.finagleEngine)
  }

  println(Await.ready(programResponse))

}