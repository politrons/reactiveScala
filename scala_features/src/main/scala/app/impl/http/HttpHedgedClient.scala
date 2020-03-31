package app.impl.http

import java.util.concurrent.TimeUnit._

import akka.http.scaladsl.model.{HttpMethods, HttpRequest}
import com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES
import com.fasterxml.jackson.databind.ObjectMapper
import com.twitter.finagle.http.Request
import com.twitter.finagle.{Http, http}
import com.twitter.util.{Duration, Future}
import zio._

import scala.concurrent.{Promise, Future => ScalaFuture}

/**
 * The whole idea behind this Http connector is about this paper of Google engineers [https://blog.acolyer.org/2015/01/15/the-tail-at-scale/]
 * in particular the Hedged request pattern to improve the performance in communications between peers.
 */
object HttpHedgedClient {

  val runtime: zio.Runtime[zio.ZEnv] = zio.Runtime.default

  private val objectMapper: ObjectMapper = new ObjectMapper().configure(FAIL_ON_UNKNOWN_PROPERTIES, false)

  var finagleClientInfo: FinagleClientInfo = FinagleClientInfo()

  trait HttpClientInfo {

    def getHedged: Int
  }

  /**
   * Internal ADT of the library to keep state of Finagle configuration. (Mmmmmmm maybe better a Monad state)
   */
  case class FinagleClientInfo(hedged: Int = 1,
                               host: String = "",
                               client: Http.Client = Http.client,
                               request: Request = http.Request(http.Method.Get, "/")) extends HttpClientInfo {

    override def getHedged: Int = hedged
  }

  case class AkkaHttpClientInfo(hedged: Int = 1,
                                request: HttpRequest = HttpRequest()) extends HttpClientInfo {
    override def getHedged: Int = hedged
  }

  /**
   * Definition of the library
   * --------------------------
   * Here we define the internal generic API of our program, which it will have so many implementations as
   * different behavior we want to provide to the DSL.
   * All the different behaviors/engines just need to implement this Service
   */
  trait Service {

    def getHttpClient: HttpClientInfo

    def withUri(uri: String): Unit

    def withHost(host: String): Unit

    def withGetMethod(): Unit

    def withPostMethod(): Unit

    def withBody(body: String): Unit

    def withTimeout(time: Long): Unit

    def withHedged(times: Int): Unit

    def run(): ScalaFuture[Any]

  }

  /**
   * Finagle behavior/engine
   * ------------------------
   * This is the behavior/engine of the program for Finagle http client.
   * We use just like type classes pattern the implementation of [Service] and is
   * wrapped into a [ZLayer] to be later inject in the program to then is able
   * the program to use the Has[Service] in the DSL that you can see bellow.
   */
  val finagleEngine: ZLayer[Any, Nothing, Has[Service]] = ZLayer.succeed(new Service {

    override def getHttpClient: HttpClientInfo = finagleClientInfo

    override def withUri(uri: String): Unit = {
      finagleClientInfo = finagleClientInfo.copy(request = finagleClientInfo.request.uri(uri))
    }

    override def withGetMethod(): Unit = {
      finagleClientInfo = finagleClientInfo.copy(request = finagleClientInfo.request.method(http.Method.Get))
    }

    override def withPostMethod(): Unit = {
      finagleClientInfo = finagleClientInfo.copy(request = finagleClientInfo.request.method(http.Method.Post))
    }

    override def withBody(body: String): Unit = {
      val request = finagleClientInfo.request
      request.contentString = serialize(body)
      finagleClientInfo = finagleClientInfo.copy(request = request)
    }

    override def withHedged(times: Int): Unit = {
      finagleClientInfo = finagleClientInfo.copy(hedged = times)
    }

    override def withTimeout(timeout: Long): Unit = {
      finagleClientInfo = finagleClientInfo.copy(client = finagleClientInfo.client.withRequestTimeout(Duration(timeout, MILLISECONDS)))
    }

    override def withHost(host: String): Unit = {
      finagleClientInfo = finagleClientInfo.copy(host = host)
    }

    override def run(): ScalaFuture[Any] = {
      val twitterFuture = finagleClientInfo.client.newService(finagleClientInfo.host)(finagleClientInfo.request)
      twitterFuture.toScalaFuture
    }
  })

  val akkaEngine: ZLayer[Any, Nothing, Has[Service]] = ZLayer.succeed(new Service {

    var httpClient: AkkaHttpClientInfo = AkkaHttpClientInfo()

    override def getHttpClient: HttpClientInfo = httpClient

    override def withUri(uri: String): Unit = {
      httpClient = httpClient.copy(request = httpClient.request.withUri(uri))
    }

    override def withHost(host: String): Unit = {
      httpClient = httpClient.copy(request = httpClient.request.withUri(host))
    }

    override def withGetMethod(): Unit = {
      httpClient = httpClient.copy(request = httpClient.request.withMethod(HttpMethods.GET))
    }

    override def withPostMethod(): Unit = {
      httpClient = httpClient.copy(request = httpClient.request.withMethod(HttpMethods.POST))
    }

    override def withBody(body: String): Unit = {
      httpClient = httpClient.copy(request = httpClient.request.withEntity(body))

    }

    override def withTimeout(time: Long): Unit = ???

    override def withHedged(times: Int): Unit = ???

    override def run(): ScalaFuture[Any] = ???
  })

  /**
   * DSL / Structure of Client
   * -------------------------
   * This is the DSL or Structure of our program.
   * Using the [ZIO.access] operator we're able to use one Has[Service] implementation depending
   * which one is provided by the consumer of the DSL once run the program. Pretty much like Type classes pattern.
   * This program provide so far a Finagle http client engine, for communication, but potentially we could use
   * this very same DSL with any other Service implementation that we want to introduce in the future, or even
   * allow the consumer to create his own implementation of Service.
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

  def Body(body: String): ZIO[Has[HttpHedgedClient.Service], Nothing, Unit] = {
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

  def Run(): ZIO[Has[HttpHedgedClient.Service], Nothing, ScalaFuture[Any]] = {
    for {
      hedgedProgram <- processHedgedProgram
      future <- hedgedProgram
    } yield future
  }

  /**
   * Function responsible to get the hedged value passed in the DSL and run in parallel using [race] function
   * of ZIO all of them, having this pattern [Hedged request] over an idempotent API allow us, to ensure that
   * always a request/response is done correctly without have to apply a retry strategy which improve the performance,
   * with the cost of generate more traffic and produce also more Throughput in the server.
   */
  private def processHedgedProgram: URIO[Has[Service], UIO[ScalaFuture[Any]]] = {
    ZIO.access[Has[Service]](hasService => {
      (1 to hasService.get.getHttpClient.getHedged).toList.foldRight(ZIO.succeed(hasService.get.run()))((_, zio) => {
        for {
          fiber1 <- zio.fork
          fiber2 <- ZIO.succeed(hasService.get.run()).fork
          future <- fiber1.join race fiber2.join
        } yield future
      })
    })
  }

  private def serialize(value: Any): String = {
    import java.io.StringWriter
    val writer = new StringWriter()
    objectMapper.writeValue(writer, value)
    writer.toString
  }

  /**
   * Implicit function to transform Twitter Future to Scala Future
   */
  implicit class TwitterFutureToScalaFuture[T](future: Future[T]) {

    def toScalaFuture: ScalaFuture[T] = {
      val promise = Promise[T]()
      future.onSuccess(value => promise.success(value))
      future.onFailure(t => promise.failure(t))
      promise.future
    }
  }

}

