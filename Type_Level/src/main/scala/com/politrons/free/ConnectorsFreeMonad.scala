package com.politrons.free

import cats.free.Free
import cats.free.Free.liftF
import cats.{Id, ~>}
import com.twitter.finagle.{Http, Service, http}
import com.twitter.util.Await
import io.vertx.core.Future
import io.vertx.core.Vertx.vertx
import io.vertx.core.buffer.Buffer
import io.vertx.ext.web.client.{HttpRequest, HttpResponse, WebClient}

object ConnectorsFreeMonad extends App {

  /**
    * ADT (algebra data types)
    * ------------------------
    */
  sealed trait ActionA[A]

  case class Host(value: String) extends ActionA[Unit]

  case class Endpoint(value: String) extends ActionA[Unit]

  case class Method(value: String) extends ActionA[Unit]

  case class Request() extends ActionA[Response[String]]

  case class Response[T](value: T) extends ActionA[Response[T]]


  /**
    * DSL
    * ----
    */

  /**
    * Create a Free monad of Host returns nothing (i.e. Unit).
    */
  def host[T](value: String): Free[ActionA, Unit] =
    liftF[ActionA, Unit](Host(value))

  /**
    * Create a Free monad of Endpoint returns nothing (i.e. Unit).
    */
  def endpoint[T](value: String): Free[ActionA, Unit] =
    liftF[ActionA, Unit](Endpoint(value))

  /**
    * Create a Free monad of Method returns nothing (i.e. Unit).
    */
  def method[T](value: String): Free[ActionA, Unit] =
    liftF[ActionA, Unit](Method(value))

  /**
    * Create a Free monad of Response returns Response[String]
    */
  def request(): Free[ActionA, Response[String]] =
    liftF[ActionA, Response[String]](Request())

  /**
    * Structure Program
    * -----------------
    */

  /**
    * This is just an example of how a consumer can use our DSL to create a program
    */
  def program: Free[ActionA, Response[String]] =
    for {
      _ <- host("run.mocky.io:80")
      _ <- endpoint("/v3/808e7664-3079-4978-ae2d-a4f2ac4e669b")
      _ <- method("GET")
      response <- request()
    } yield response

  /**
    * Behavior Programs
    * ------------------
    */

  /**
    * Apache connector implementation.
    * We implement the behavior to be used by the program that use the DSL.
    * The program will become impure and it might have side-effects when:
    * * Receive the ADT
    * * We make the request
    * * We read the response.
    */
  def apacheConnector: ActionA ~> Id =
    new (ActionA ~> Id) {

      import org.apache.http.client.methods.{HttpGet, HttpRequestBase}
      import org.apache.http.impl.client.{BasicResponseHandler, CloseableHttpClient, HttpClients}

      private val client: CloseableHttpClient = HttpClients.createDefault()
      var host: String = _
      var endpoint: String = _
      var httpRequest: HttpRequestBase = _

      def apply[A](fa: ActionA[A]): Id[A] =
        fa match {
          case Host(value) =>
            host = value
            ().asInstanceOf[A]
          case Endpoint(value) =>
            println(s"endpoint:$value")
            endpoint = value
            ().asInstanceOf[A]
          case Method("GET") =>
            println(s"GET method")
            httpRequest = new HttpGet(s"http://$host$endpoint")
            ().asInstanceOf[A]
          case Request() =>
            println(s"Apache request executing......")
            val responseHandler = new BasicResponseHandler
            val response = client.execute(httpRequest, responseHandler)
            Response(response).asInstanceOf[A]
        }
    }

  /**
    * Finagle connector implementation.
    * We implement the behavior to be used by the program that use the DSL.
    * The program will become impure and it might have side-effects when:
    * * Receive the ADT
    * * We create the Service
    * + We make the request and we await for the resolution of the future.
    */
  def finagleConnector: ActionA ~> Id =
    new (ActionA ~> Id) {

      var host: String = _
      var endpoint: String = _
      var client: Service[http.Request, http.Response] = _
      var request: http.Request = _

      def apply[A](fa: ActionA[A]): Id[A] =
        fa match {
          case Host(value) =>
            host = value
            client = Http.newService(host)
            ().asInstanceOf[A]
          case Endpoint(value) =>
            println(s"endpoint:$value")
            endpoint = value
            ().asInstanceOf[A]
          case Method("GET") =>
            println(s"GET method")
            request = http.Request(http.Method.Get, endpoint)
            request.host = host
            ().asInstanceOf[A]
          case Request() =>
            println(s"Finagle request executing......")
            val response = Await.result(client(request))
            Response(response.contentString).asInstanceOf[A]
        }
    }

  /**
    * Apache connector
    * ----------------
    * We use in our program the apache connector interpreter
    */
  val apacheResponse: Response[String] = program.foldMap(apacheConnector)
  println(apacheResponse.value)

  /**
    * Finagle connector
    * -----------------
    * We use in our program the finagle connector interpreter
    */
  val vertxResponse: Response[String] = program.foldMap(finagleConnector)
  println(vertxResponse.value)

}
