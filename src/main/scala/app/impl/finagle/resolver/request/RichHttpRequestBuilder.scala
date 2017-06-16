package app.impl.finagle.resolver.request

import java.util
import java.util.Optional

import com.twitter.finagle.http._
import com.twitter.io.{Buf, Bufs}

import scala.collection.JavaConverters._


/**
  * Builder for creating a (Finagle) Request for use with the Resilient HTTP Client.
  */
object RichHttpRequestBuilder {
  private val Utf8 = "utf-8"
}

//todo: determine whether we want this hard Jackson wiring, we should probably make this a function Any => String
case class RichHttpRequestBuilder(requestBuilder: RequestBuilder[_, _] = RequestBuilder(),
                                  method: Option[Method] = None,
                                  content: Option[Buf] = None,
                                  url: Option[String] = None,
                                  templateParams: Map[String, String] = Map.empty,
                                  queryParams: Map[String, String] = Map.empty,
                                  private val containsAnyElement: Boolean = false,
                                  private val containsFileElement: Boolean = false) {

  def this() = this(RequestBuilder())

  /**
    * Set the HTTP method of to be used for the Request.
    *
    * @param method The HTTP method as Finagle's HttpMethod
    * @return a new RichHttpRequestBuilder based on the current builder plus the changes made in this method.
    */
  def withMethod(method: Method): RichHttpRequestBuilder =
    copy(method = Some(method))

  /**
    * Set the HTTP method of to be used for the Request.
    *
    * @param method The HTTP method as String ("GET", "POST", etc.)
    * @return a new RichHttpRequestBuilder based on the current builder plus the changes made in this method.
    */
  def withMethod(method: String): RichHttpRequestBuilder =
    withMethod(Method(method))

  /**
    * Set the content (body) of the Request.
    *
    * @param content   The content of the Request as Buf
    * @param mediaType The content's media type. May be <code>null</code>
    * @param charset   The character set used for the content, such as utf-8.
    * @return a new RichHttpRequestBuilder based on the current builder plus the changes made in this method.
    */
  def withContent(content: Buf, mediaType: Option[String], charset: Option[String]): RichHttpRequestBuilder = {
    val res = copy(
      content = Some(content),
      containsAnyElement = false,
      containsFileElement = false
    )

    //todo: Check why only the media type is checked for null and the charset is passed in regardless
    mediaType.fold(res) { mT =>
      res.withHeader("Content-Type", contentType(mT, charset))
    }
  }

  /**
    * Set the content (body) of the Request. UTF-8 charset will be used.
    *
    * @param content   The content of the Request as String
    * @param mediaType The content's media type. May be <code>null</code>
    * @return a new RichHttpRequestBuilder based on the current builder plus the changes made in this method.
    */
  def withContent(content: Buf, mediaType: String, charset: String): RichHttpRequestBuilder =
    withContent(content, Option(mediaType), Option(charset))

  def withContent(content: String, mediaType: String): RichHttpRequestBuilder =
    withContent(Bufs.utf8Buf(content), Option(mediaType), Some(RichHttpRequestBuilder.Utf8))

  /**
    * Set the content (body) of the Request.
    *
    * @param content The content of the Request as String. No media type will be set (can be set separately using
    *                withHeader(...)
    * @return a new RichHttpRequestBuilder based on the current builder plus the changes made in this method.
    */
  def withContent(content: String): RichHttpRequestBuilder =
    withContent(Bufs.utf8Buf(content), None, None)

  /**
    * Set the content (body) of the Request.
    *
    * @param content The content of the Request as Buf. No media type will be set (can be set separately using
    *                withHeader(...)
    * @return a new RichHttpRequestBuilder based on the current builder plus the changes made in this method.
    */
  def withContent(content: Buf): RichHttpRequestBuilder =
    withContent(content, None, None)

  /**
    * Set the URL of the Request. It is obligatory to provide either URL or Path.
    *
    * @param url The URL as String.
    * @return a new RichHttpRequestBuilder based on the current builder plus the changes made in this method.
    */
  def withUrl(url: String): RichHttpRequestBuilder = {
    copy(url = Some(url))
  }

  /**
    * Set the path of the Request. It is obligatory to provide either URL or Path. A path is basically the part of the
    * URL after the hostname (and port). You may start the path with a forward slash.
    *
    * @param path The Path.
    * @return a new RichHttpRequestBuilder based on the current builder plus the changes made in this method.
    */
  def withPath(path: String): RichHttpRequestBuilder = {
    // 'service' is just a dummy value.
    // todo: find util library for this, perhaps just use the apache commons one
    val prefix = if (Option(path).isDefined && path.nonEmpty && path.head != '/') "/" else ""
    withUrl(s"http://service$prefix$path")
  }

  /**
    * Set the parameters of the Request. Represented by key-value pairs. URLs containing placeholders denoted by
    * curly brackets will use these parameters. For example http//www.host.com/resource/{accountNr} will be translated
    * to http//www.host.com/resource/123 if the params map contains the entry ("accountNr, "123")
    *
    * @param params A map of key-value pairs. A mapping of parameter name to parameter value.
    * @return a new RichHttpRequestBuilder based on the current builder plus the changes made in this method.
    */
  def withParams(params: util.Map[String, String]): RichHttpRequestBuilder = {
    copy(templateParams = params.asScala.toMap)
  }

  /**
    * Add a parameter to the parameter map. See withParams(final Map<String, String> params).
    *
    * @param paramName  The name of the parameter to add
    * @param paramValue The value of the parameter to add
    * @return a new RichHttpRequestBuilder based on the current builder plus the changes made in this method.
    */
  def withParam(paramName: String, paramValue: String): RichHttpRequestBuilder = {
    withParams((templateParams + (paramName -> paramValue)).asJava)
  }

  /**
    * *Append* the query parameters of the Request. Represented by key-value pairs.
    *
    * @param queryParams A map of key-value pairs. A mapping of parameter name to parameter value.
    * @return a new RichHttpRequestBuilder based on the current builder plus the changes made in this method.
    */
  def withQueryParams(queryParams: util.Map[String, String]): RichHttpRequestBuilder = {
    withQueryParams(queryParams.asScala.toMap)
  }

  /**
    * *Append* the query parameters of the Request. Represented by key-value pairs.
    *
    * @param queryParams A scala map of key-value pairs. A mapping of parameter name to parameter value.
    * @return a new RichHttpRequestBuilder based on the current builder plus the changes made in this method.
    */
  def withQueryParams(queryParams: Map[String, String]): RichHttpRequestBuilder = {
    val mutatedQueryParams: Map[String, String] =
      queryParams.map { case (key, value) =>
        (key, s"$key=$value")
      }
    copy(queryParams = queryParams ++ mutatedQueryParams)
  }


  /**
    * Add a query parameter of the Request. Represented by key-value pairs.
    *
    * @param paramName  The name of the parameter to add
    * @param paramValue The value of the parameter to add
    * @return a new RichHttpRequestBuilder based on the current builder plus the changes made in this method.
    */
  def withQueryParam(paramName: String, paramValue: String): RichHttpRequestBuilder = {
    //withQueryParams(queryParams + (paramName -> s"$paramName=$paramValue"))
    withQueryParams(queryParams + (paramName -> paramValue))
  }

  /**
    * Set a header of the Request.
    *
    * @param headerName  The name of the header to set
    * @param headerValue The value of the header
    * @return a new RichHttpRequestBuilder based on the current builder plus the changes made in this method.
    */
  def withHeader(headerName: String, headerValue: String): RichHttpRequestBuilder =
    copy(requestBuilder = requestBuilder.setHeader(headerName, headerValue))

  /**
    * Add a FormElement to the Request. This also sets the HTTP method to POST.
    *
    * @param formElement The FormElement
    * @return a new RichHttpRequestBuilder based on the current builder plus the changes made in this method.
    */
  def withFormElement(formElement: FormElement): RichHttpRequestBuilder = {
    copy(
    containsAnyElement = true,
    containsFileElement = containsFileElement || formElement.isInstanceOf[FileElement]
    ).withMethod(Method.Post)
    .copy(requestBuilder = requestBuilder.add(formElement))
  }



  // Construct a UTF-8 content type given a media type
  private def contentType(mediaType: String, charset: Option[String]) =
    if (charset.isDefined)
      s"$mediaType;charset=${charset.get}"
    else
      mediaType

  // Add query parameters to the url
  private def instantiateQueryParameters(): RichHttpRequestBuilder = {
    if (queryParams.nonEmpty) {
      val separator: String =
        if (!url.get.contains("?"))
          "?"
        else
          "&"

      withUrl(s"${url.get}$separator${queryParams.values.mkString("&")}").copy(queryParams = Map.empty)
    } else this
  }

  // Use the params to remove all placeholders in the URL. Return false if not all placeholders where removed
  private def handlePathParameters: RichHttpRequestBuilder = {
    val urlWithParamsInstantiated =
      url.map { startUrl =>
        var workUrl = startUrl // todo: get?
        if (templateParams.nonEmpty) {
          for (keyValue <- templateParams.asJava.entrySet.asScala) {
            workUrl = workUrl.replaceAll("\\{" + keyValue.getKey + "\\}", keyValue.getValue)
          }
        }
        workUrl
      }

    urlWithParamsInstantiated.fold(this)(withUrl)
  }

  private def isValidUrl =
    url.get.indexOf("{") != 0 || url.get.indexOf("}") != 0

  /**
    * Build a Request based on the information given to the builder. This method does some checks to make sure all
    * information necessary to construct the request was given.
    *
    * @return A fully constructed Request
    */
  def build: Request = {
    require(method.isDefined, "HTTP Method is required for building a request.")
    require(url.isDefined, "URL is required for building a request.")

    // extend the url with query parameters if applicable and
    // replace placeholders of path parameters if applicable
    val parameterizedBuilder = this.instantiateQueryParameters().handlePathParameters

    // validate it
    require(parameterizedBuilder.isValidUrl, "URL is invalid after parameter placeholders were replaced.")

    // update the URL of the underlying Finagle RequestBuilder. It needs a valid URL.
    val completeBuilder = requestBuilder.url(parameterizedBuilder.url.get)

    if (containsAnyElement) {
      val rb = completeBuilder.asInstanceOf[RequestBuilder.CompleteForm]
      RequestBuilder.safeBuildFormPost(rb, containsFileElement)
    } else {
      val rb = completeBuilder.asInstanceOf[RequestBuilder.Complete]
      RequestBuilder.safeBuild(rb, method.get, content)
    }
  }

  // Java friendly getters
  def getMethod: Optional[Method] = toJavaOptional(method)

  def getContent: Optional[Buf] = toJavaOptional(content)

  def getUrl: Optional[String] = toJavaOptional(url)

  def getTemplateParams: util.Map[String, String] = templateParams.asJava

  def getQueryParams: util.Map[String, String] = queryParams.asJava

  private def toJavaOptional[T >: Null](option: Option[T]): Optional[T] = Optional.ofNullable(option.orNull)
}
