package app.impl.finagle.resolver

import java.io.InputStream
import java.net.URLEncoder

import app.impl.finagle.resolver.entity.ResponseEntity
import app.impl.finagle.resolver.filter.{ResponseWithEntityFilter, StripJsonPrefixFilter}
import app.impl.finagle.resolver.mapper.ObjectMapperPatcher
import app.impl.finagle.resolver.request.RichHttpRequestBuilder
import com.twitter.finagle.http.{Method, Request, Status}
import com.twitter.finagle.{Filter, Http, Service}
import com.twitter.util._
import grizzled.slf4j.Logging


object DefaultCustomClient extends CustomClient {

  case class LookupResponse(instances: Array[InstanceResponse])

  val lookupHttpClient = Http.client.newService(s"customResolver!${CustomResolver.lookupEndpoint}", "resolver-lookup")

  implicit val mapper = ObjectMapperPatcher.patchedMapper

  private val lookupService: Service[LookupQuery, Option[Set[InstanceResponse]]] =
    CustomFilters.lookup
      .andThen(new ResponseWithEntityFilter()
        .andThen(new StripJsonPrefixFilter[Request])
        .andThen(lookupHttpClient))

  /** @inheritdoc */
  def lookup(lookupQuery: LookupQuery): Future[Option[Set[InstanceResponse]]] = lookupService(lookupQuery)

  object CustomFilters extends Logging {
    type Manifest = InputStream

    val lookup: Filter[LookupQuery, Option[Set[InstanceResponse]], Request, ResponseEntity] =
      Filter.mk[LookupQuery, Option[Set[InstanceResponse]], Request, ResponseEntity] { case (lookupQuery, service) =>
        service(createHttpRequest(lookupQuery))
          .map { response => processResponse(response) }
          .lowerFromTry
      }

    def processResponse(response: ResponseEntity): Try[Option[Set[InstanceResponse]]] = {
      if (response.getStatusCode == Status.NotFound.code) {
        Return(None)
      } else {
        val parsed: Try[LookupResponse] = response.readEntity[LookupResponse]
        parsed.map(x => Some(x.instances.toSet))
      }
    }

    def createHttpRequest(lookupQuery: LookupQuery): Request = {
      RichHttpRequestBuilder()
        .withPath(s"/instances/${lookupQuery.host}/${URLEncoder.encode(lookupQuery.pathTemplate, "UTF-8")}/${lookupQuery.method}")
        .withMethod(Method.Get)
        .build
    }

  }

}