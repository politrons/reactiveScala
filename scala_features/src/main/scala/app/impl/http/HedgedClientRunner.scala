package app.impl.http

import io.vertx.core.Vertx
import io.vertx.core.http.HttpClient
import org.junit.Test
import zio.{Has, ZIO}

import scala.concurrent.duration._
import scala.concurrent.{Await, Future}
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.stream.ActorMaterializer

import scala.concurrent.Future
import scala.util.{Failure, Success}

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
    println(Await.ready(programResponse, 10 seconds))
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
    println(Await.ready(programResponse, 10 seconds))
  }

  @Test
  def vertxEngine(): Unit = {

    val client: HttpClient = Vertx.vertx().createHttpClient()
    val request = client.get("www.google.com:80", "/")

  }

  @Test
  def akkaEngine(): Unit = {
    implicit val system = ActorSystem()
    implicit val materializer = ActorMaterializer()
    // needed for the future flatMap/onComplete in the end
    implicit val executionContext = system.dispatcher

    val request: HttpRequest = HttpRequest()
      .withMethod(HttpMethods.GET)
      .withUri("http://www.google.com")

    val responseFuture: Future[HttpResponse] = Http().singleRequest(request)

    responseFuture
      .onComplete {
        case Success(res) => println(res)
        case Failure(_) => println("something wrong")
      }

    Thread.sleep(10000)

  }

}