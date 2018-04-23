package app.impl.finagle.resolver.filter

import app.impl.finagle.HttpServers
import app.impl.finagle.workshop.FinagleHttpClient.response
import com.twitter.finagle._
import com.twitter.finagle.http.{Request, Response}
import com.twitter.util.{Await, Future}
import org.junit.Test

class FiltersFeature {

  HttpServers.start()

  println("Running client")
  var service = Http.client
    .newService("localhost:1982")

  val request = http.Request("/")
  request.method(http.Method.Get)
  request.contentString = ""

  val filter1 = new Filter[Request, Response, Request, Response] {
    override def apply(request: Request, service: Service[Request, Response]): Future[Response] = {
      val value = service.apply(request)
      value
    }
  }

  val filter2 = new Filter[Request, Response, Request, Response] {
    override def apply(request: Request, service: Service[Request, Response]): Future[Response] = {
      val value = service.apply(request)
      value
    }
  }

  @Test
  def main(): Unit = {
    val serviceRequest = filter1.andThen(filter2).andThen(service)
    val response = serviceRequest(request)
    println(Await.result(response))


  }

}
