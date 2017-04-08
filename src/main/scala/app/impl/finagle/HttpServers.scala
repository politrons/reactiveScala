package app.impl.finagle

import com.twitter.finagle._
import com.twitter.finagle.http.service.HttpResponseClassifier
import com.twitter.util.{Await, Future}
import com.twitter.conversions.time._

/**
  * Created by pabloperezgarcia on 08/04/2017.
  *
  * Finagle provide multiple features on server side that could be handy
  */
object HttpServers extends App {

  private var sleepTime = 0
  private val port = "8888"
  private var responseType = "ok"

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
  Await.ready(mainServer().serve(s"localhost:$port", service))

  /**
    * This is a regular finagle server
    */
  def mainServer(): Http.Server  = {
    Http.server
      .withResponseClassifier(HttpResponseClassifier.ServerErrorsAsFailures)
  }

  /**
    * This server since only accept one concurrent request will throw a Concurrency limit exception
    */
  def limitedConnectionsServer(): Http.Server = {
    sleepTime = 5000
    Http.server
      .withResponseClassifier(HttpResponseClassifier.ServerErrorsAsFailures)
      .withAdmissionControl.concurrencyLimit(maxConcurrentRequests = 1, maxWaiters = 0)
  }

  /**
    * adding the error_retry response we will throw the first time an Future with Exception to the client
    * Then the retry policy of the client will try again, and then the second time since we change
    * the variable it will succeed.
    *
    * @return
    */
  def errorRetryServer(): Http.Server = {
    responseType = "error_retry"
    Http.server
      .withResponseClassifier(HttpResponseClassifier.ServerErrorsAsFailures)
  }

  /**
    * adding the error_retry response we will throw the first time an Future with Exception to the client
    * Then the retry policy of the client will try again, and then since we dont change the variable
    * the client will consume all retries and it will finally fail.
    */
  def errorNonServer(): Http.Server = {
    responseType = "error_non_retry"
    Http.server
      .withResponseClassifier(HttpResponseClassifier.ServerErrorsAsFailures)
  }

  /**
    * You can also provide the max TTL of your connection, and how much time the connection
    * can be idle without any traffic.
    * @return
    */
  def errorMaxLifeTime(): Http.Server = {
    sleepTime = 10000
    responseType = "error_retry"
    Http.server
      .withSession.maxLifeTime(5.seconds)
      .withSession.maxIdleTime(5.seconds)  }

}
