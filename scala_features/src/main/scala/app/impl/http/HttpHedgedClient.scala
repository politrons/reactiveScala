package app.impl.http

import java.util.concurrent.TimeUnit._

import app.impl.http.HttpHedgedClient.HttpClient
import com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES
import com.fasterxml.jackson.databind.ObjectMapper
import com.twitter.finagle
import com.twitter.finagle.http.{Request, Response}
import com.twitter.finagle.{Http, Service, http}
import com.twitter.util.{Await, Duration, Future}
import zio.{Has, ZIO, ZLayer}

object HttpHedgedClient {

  private val objectMapper: ObjectMapper = new ObjectMapper().configure(FAIL_ON_UNKNOWN_PROPERTIES, false)

  case class HttpClient(hedged: Int = 0,
                        host: String = "",
                        client: Http.Client = Http.client,
                        request: Request = http.Request(http.Method.Get, "/"))

  val runtime: zio.Runtime[zio.ZEnv] = zio.Runtime.default

  trait Service {

    var httpClient: HttpClient = HttpClient()

    def withUri(uri: String): Unit

    def withHost(host: String): Unit

    def withGetMethod(): Unit

    def withPostMethod(): Unit

    def withBody(body: Any): Unit

    def withTimeout(time: Long): Unit

    def withHedged(times: Int): Unit

    def run(): Future[Any]

  }

  /**
   * Behavior of Client
   * ---------------------
   */
  val finagleEngine: ZLayer[Any, Nothing, Has[Service]] = ZLayer.succeed(new Service {

    override def withUri(uri: String): Unit = {
      val request = httpClient.request
      httpClient = httpClient.copy(request = request.uri(uri))
    }

    override def withGetMethod(): Unit = {
      val request = httpClient.request
      httpClient = httpClient.copy(request = request.method(http.Method.Get))
    }

    override def withPostMethod(): Unit = {
      val request = httpClient.request
      httpClient = httpClient.copy(request = request.method(http.Method.Post))
    }

    override def withBody(body: Any): Unit = {
      val request = httpClient.request
      request.contentString = serialize(body)
      httpClient = httpClient.copy(request = request)
    }

    override def withHedged(times: Int): Unit = {
      httpClient = httpClient.copy(hedged = times)
    }

    override def withTimeout(timeout: Long): Unit = {
      httpClient = httpClient.copy(client = httpClient.client.withRequestTimeout(Duration(timeout, MILLISECONDS)))
    }

    override def withHost(host: String): Unit = {
      httpClient = httpClient.copy(host = host)
    }

    override def run(): Future[Any] = {
      val service = httpClient.client.newService(httpClient.host)
      service(httpClient.request)
    }

    private def serialize(value: Any): String = {
      import java.io.StringWriter
      val writer = new StringWriter()
      objectMapper.writeValue(writer, value)
      writer.toString
    }

  })

  /**
   * DSL / Structure of Client
   * -------------------------
   */
  def Uri(uri: String): ZIO[Has[HttpHedgedClient.Service], Nothing, Unit] = {
    ZIO.access(hasService => hasService.get.withUri(uri))
  }

  def Get(): ZIO[Has[HttpHedgedClient.Service], Nothing, Unit] = {
    ZIO.access(hasService => hasService.get.withGetMethod())
  }

  def Post(): ZIO[Has[HttpHedgedClient.Service], Nothing, Unit] = {
    ZIO.access(hasService => hasService.get.withPostMethod())
  }

  def Body(body: Any): ZIO[Has[HttpHedgedClient.Service], Nothing, Unit] = {
    ZIO.access(hasService => hasService.get.withBody(body))
  }

  def Timeout(timeout: Long): ZIO[Has[HttpHedgedClient.Service], Nothing, Unit] = {
    ZIO.access(hasService => hasService.get.withTimeout(timeout))
  }

  def Hedged(times: Int): ZIO[Has[HttpHedgedClient.Service], Nothing, Unit] = {
    ZIO.access(hasService => hasService.get.withHedged(times))
  }

  def Host(host: String): ZIO[Has[HttpHedgedClient.Service], Nothing, Unit] = {
    ZIO.access(_.get.withHost(host))
  }

  def Run(): ZIO[Has[HttpHedgedClient.Service], Nothing, Future[Any]] = {
    ZIO.access(hasService => hasService.get.run())
  }

}

object MainTest extends App {

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