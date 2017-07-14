package app.impl.finagle.resolver.entity

import com.fasterxml.jackson.databind.ObjectMapper
import com.twitter.finagle.http.Response
import com.twitter.util.Try

import scala.reflect.ClassTag

/**
 * Wrapper class encapsulates the functionality class [[com.twitter.finagle.http.Response]]
 *
 * @param response     the original response
 * @param objectMapper instance of { @link com.fasterxml.jackson.databind.ObjectMapper} for converting between Java/scala objects and matching JSON constructs
 */
class ResponseEntity(val response: Response, implicit val objectMapper: ObjectMapper) {
  /**
   * Returns the http status code
   *
   * @return statusCode the http status code
   */
  def getStatusCode: Int = response.statusCode

  /**
   * Returns the content as string
   *
   * @return contentString the content as string
   */
  def getContentString: String = response.getContentString()


  /**
   * Deserializes JSON, given as content string into a Java/Scala type
   *
   * @tparam Res the output type
   * @return Try[Res] in case the deserialization succeeds Try contains the the Java/Scala type, otherwise it contains an exception
   */
  def readEntity[Res: ClassTag]: Try[Res] = {
    Try {
      unsafeReadEntity[Res]
    }
  }

  /**
   * Deserializes JSON, given as content string into a Java/Scala type
   *
   * @tparam Res the output type
   * @return Res the Java/scala type or throw an exception
   */
  private def unsafeReadEntity[Res: ClassTag]: Res = {
    val claszz = implicitly[ClassTag[Res]].runtimeClass.asInstanceOf[Class[Res]]
    objectMapper.readValue[Res](getContentString, claszz)
  }


}
