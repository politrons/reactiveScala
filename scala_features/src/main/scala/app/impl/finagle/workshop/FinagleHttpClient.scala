package app.impl.finagle.workshop

import java.net.URL

import com.twitter.finagle.http.{HeaderMap, Request, RequestBuilder, Response}
import com.twitter.finagle.{Http, Service, http}
import com.twitter.util.{Await, Future}

/**
  * Created by pabloperezgarcia on 08/04/2017.
  *
  * A really easy way to implement a client without almost any code
  * The Service class will receive and response a Future[Response] the type that you specify
  * Service[Req,Rep]
  */
object FinagleHttpClient extends App {

  println("Running client")
  var service = Http.client
    //      .withRetryBackoff()
    .newService("0.0.0.0:8080")
  //  val service: Service[Request, Response] = Http.newService("0.0.0.0:8080")

  val request = http.Request(http.Method.Get, "/hello_server")
  //  request.host = "http://0.0.0.0:8080/"
  request.headerMap.add("host", "http://0.0.0.0:8080/")

  //  val request = RequestBuilder()
  //    .url(new URL("http://0.0.0.0:8080/hello_server"))
  //    .buildGet()

  val serviceWithFilter =
    ReplaceFilter("WORLD", "PLANET")
      .andThen(UpperCaseFilter()
        .andThen(AppendFilter("!!!!")
          .andThen(service)))

  val response = serviceWithFilter(request)

  defineOnFailure
  defineOnSuccess
  println(Await.result(response))


  private def defineOnFailure = {
    response.onFailure { t =>
      println(s"Error:${t.getMessage}")
    }
  }

  private def defineOnSuccess = {
    response.onSuccess { rep =>
      println("Response: " + rep.contentString)
    }
  }


}
