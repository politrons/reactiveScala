package app.impl.finagle.resolver.filter

import com.twitter.finagle.http.Response
import com.twitter.finagle.{Service, SimpleFilter}
import com.twitter.util.Future

/**
 * Strips the JSON Prefix when present
 */
object StripJsonPrefixFilter {

  /**
   * The prefix as used by RIAF filters
   */
  val Prefix = ")]}',\n"
}

/**
 * Filter to strip the JSON Prefix when present.
 *
 * @tparam Req the Req type of the filter. The implementation of the filter is agnostic to the specific type of Req.
 */
class StripJsonPrefixFilter[Req] extends SimpleFilter[Req, Response] {

  import StripJsonPrefixFilter._

  /**
   * @inheritdoc
   */
  override def apply(request: Req, service: Service[Req, Response]): Future[Response] =
    service(request) map { res =>
      if (Option(res.contentString).isDefined)
        res.contentString = res.contentString.stripPrefix(Prefix)
      res
    }
}
