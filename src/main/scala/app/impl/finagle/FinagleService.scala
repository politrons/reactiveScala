package app.impl.finagle

import com.twitter.finagle.{Failure, http, _}
import com.twitter.util.Future

/**
  * Created by pabloperezgarcia on 08/04/2017.
  */
object FinagleService {

  var sleepTime = 0
  var responseType = "ok"

  val service = new Service[http.Request, http.Response] {
    def apply(req: http.Request): Future[http.Response] = {
      Thread.sleep(sleepTime)
      responseType match {
        case "ok" => Future.value(http.Response(req.version, http.Status.Ok))
        case "error_retry" =>
          responseType = "ok"
          Future.exception(Failure.rejected("busy"))
        case "error_non_retry" => Future.exception(Failure("Don't try again",
          Failure.Rejected | Failure.NonRetryable))
      }
    }

  }
