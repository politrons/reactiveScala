package app.impl.finagle

import com.twitter.finagle.{Service, SimpleFilter}
import com.twitter.util.{Duration, Future, Timer}

/**
  * Created by pabloperezgarcia on 08/04/2017.
  *
  * A filter can wrap a service execution and apply a transformation of the result of that service
  * Or as in this particular case throw an exception if the service does not response in less time
  * as we specify in the timeout.
  *
  */
class TimeoutFilter[Req, Rep](timeout: Duration) extends SimpleFilter[Req, Rep] {

  val timer = Timer.Nil

  def apply(request: Req, service: Service[Req, Rep]): Future[Rep] = {
    val res = service(request)
    res.within(timer, timeout)
  }
}