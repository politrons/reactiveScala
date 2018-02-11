package app.impl.finagle.workshop

import com.twitter.finagle.http.Response
import com.twitter.finagle.{Service, SimpleFilter}
import com.twitter.util.Future


case class UpperCaseFilter[Req]() extends SimpleFilter[Req, Response] {

  override def apply(request: Req, service: Service[Req, Response]): Future[Response] =
    service(request) map { res =>
      res.setContentString(res.getContentString().toUpperCase)
      res
    }
}
