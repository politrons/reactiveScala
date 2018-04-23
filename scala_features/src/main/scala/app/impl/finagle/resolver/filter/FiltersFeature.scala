package app.impl.finagle.resolver.filter

import app.impl.finagle.HttpServers
import app.impl.finagle.workshop.FinagleHttpClient.response
import com.twitter.finagle._
import com.twitter.finagle.http.{Request, Response}
import com.twitter.util.{Await, Future}
import org.jboss.netty.handler.codec.http.HttpResponse
import org.junit.Test

class FiltersFeature {

  HttpServers.start()

  println("Running client")
  var service = Http.client
    .newService("localhost:1982")

  val request = http.Request("/")
  request.method(http.Method.Get)
  request.contentString = ""

  case class ResponseInfo(request: Request, response: Response) extends Response {
    override protected def httpResponse: HttpResponse = null

    override def toString =
      "Response(response)"
  }

  val filter1 = new Filter[Request, ResponseInfo, Request, ResponseInfo] {
    override def apply(request: Request, service: Service[Request, ResponseInfo]): Future[ResponseInfo] = {
      service.apply(request)
        .map(responseInfo => {
          responseInfo.copy(response = responseInfo.response)
        })
    }
  }

  val filter2 = new Filter[Request, ResponseInfo, Request, Response] {
    override def apply(request: Request, service: Service[Request, Response]): Future[ResponseInfo] = {
      service.apply(request)
        .map(response => ResponseInfo(request, response))
    }
  }

  @Test
  def main(): Unit = {

    val serviceRequest = filter1
      .andThen(filter2)
      .andThen(service)

    val responseFuture = serviceRequest(request)
    val response = Await.result(responseFuture)
    println(response.asInstanceOf[ResponseInfo].request)
    println(response.asInstanceOf[ResponseInfo].response.statusCode)

  }

}
