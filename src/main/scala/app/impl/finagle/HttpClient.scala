package app.impl.finagle

import com.twitter.finagle.http.{Request, Response}
import com.twitter.finagle.{Http, Service, http}
import com.twitter.util.{Await, Future}

/**
  * Created by pabloperezgarcia on 08/04/2017.
  *
  * A really easy way to implement a client without almost any code
  * The Service class will receive and response a Future[Response] the type that you specify
  * Service[Req,Rep]
  */
object HttpClient extends App {

  val client: Service[Request, Response] = Http.newService("localhost:8888")
  val request = http.Request(http.Method.Get, "/")
  val response = makeRequest
  defineOnFailure
  defineOnSuccess
  Await.result(response)

  private def makeRequest: Future[Response] = {
    client(request)
  }

  private def defineOnFailure = {
    response.onFailure { t =>
      println(s"Error:${t.getMessage}")
      makeRequest
    }
  }

  private def defineOnSuccess = {
    response.onSuccess { rep =>
      println("Response: " + rep)
    }
  }


}
