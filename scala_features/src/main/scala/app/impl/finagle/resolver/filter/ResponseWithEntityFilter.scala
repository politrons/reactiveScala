package app.impl.finagle.resolver.filter

import app.impl.finagle.resolver.entity.ResponseEntity
import com.fasterxml.jackson.databind.ObjectMapper
import com.twitter.finagle.http.Response
import com.twitter.finagle.{Filter, Service}
import com.twitter.util.{Future, Promise, Try}

/**
 * Filter responsible for Wrapping {@link Response} to {@link ResponseWithEntity}
 * @param objectMapper instance of { @link ObjectMapper} for converting between Java/scala objects and matching JSON constructs
 * @tparam Req the input request type
 * @tparam Res the output response type
 */
class ResponseWithEntityFilter[Req, Res](implicit val objectMapper: ObjectMapper)
  extends Filter[Req, ResponseEntity, Req, Response] {

  /**
   * @inheritdoc
   */
  override def apply(request: Req, service: Service[Req, Response]): Future[ResponseEntity] = {
    val promise = Promise[ResponseEntity]()
    val res = service(request)
    res.onFailure(x => promise.setException(x))
    res.onSuccess(response => {
      val responseWithEntity = Try(new ResponseEntity(response, objectMapper))
      promise.update(responseWithEntity)
    })
    promise
  }
}


