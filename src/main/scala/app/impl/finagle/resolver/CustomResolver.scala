package app.impl.finagle.resolver

import java.net.InetSocketAddress

import com.twitter.finagle._
import com.twitter.finagle.stats.{DefaultStatsReceiver, StatsReceiver}
import com.twitter.finagle.util.DefaultTimer
import com.twitter.util.{Closable, Var, _}
import grizzled.slf4j.Logging

/**
  * @todo make the settings changeable so it can be perhaps changed after loading... Not sure whether that is
  *       the best way...
  * @param statsReceiver
  * @param resolvePool
  * @param phoenixClient
  */
class CustomResolver(statsReceiver: StatsReceiver,
                     resolvePool: FuturePool,
                     phoenixClient: CustomClient) extends Resolver with Logging with Closable {

  var retryNumber = 0

  var maxRetryNumber = 10

  val pollIntervalOpt: Option[Duration] = Some(Duration.fromSeconds(5))

  lazy val masterNode = Address.Inet(new InetSocketAddress("localhost", 8443), Addr.Metadata.empty)

  import CustomResolver._

  val scheme: String = "customResolver"
  private[this] val timer = DefaultTimer.twitter

  def this() =
    this(DefaultStatsReceiver,
      FuturePool.immediatePool,
      DefaultCustomClient)


  /**
    * We expect something of the format:
    *
    *
    * For now it is with stars because SD registered themselves with *.
    * {{{
    *   customResolver!domain:method:pathparam
    *   customResolver!politrons.com:/instances/*/*/*:GET   -- to fix highlighting: */*/*/
    * }}}
    *
    * @param endpoint
    * @return
    */
  override def bind(endpoint: String): Var[Addr] = {
    endpoint.split(":") match {
      case Array(host, pathTemplate, method) =>
        bindHosts(LookupQuery(host, pathTemplate, method))
      case _ =>
        throw new IllegalArgumentException(s"Invalid endpoint definition [$endpoint]")
    }
  }

  private def bindHosts(query: LookupQuery): Var[Addr] = {
    lazy val res: Var[Addr] = Var.async(initializeAddr()) { updatable =>
      def lookupAndUpdate(): Future[Addr] =
        resolvePool {
          lookup(query)
            .onSuccess { addr =>
              info(s"First time Updating instances for query [$query] to [$addr]")
              updatable.update(merge(res.sample(), addr))
            }
            .onFailure { ex =>
              error(s"First time Error updating instances for query [$query]", ex)
            }
        }.flatten

      lookupAndUpdate()
      pollIntervalOpt match {
        case Some(pollInterval) =>
          timer.schedule(pollInterval.fromNow, pollInterval) {
            lookupAndUpdate()
          }
        case None =>
          Closable.nop
      }
    }
    res
  }

  private def lookup(query: LookupQuery): Future[Addr] = {
    info(s"Finding instances for query: [$query]")
    toAddr(query, phoenixClient.lookup(query))
      .within(Duration.fromSeconds(5))(DefaultTimer.twitter)
      .onSuccess { x =>
        info(s"x [$query] to [$x]")
      }
      .onFailure { ex =>
        error(s"Firy [$query]", ex)
      }
  }

  private def toAddr(query: LookupQuery, future: Future[Option[Set[InstanceResponse]]]): Future[Addr] = {
    future.liftToTry.map {
      case Return(Some(instances)) =>
        val addresses: Set[Address] =
          instances.map {
            instance => Address.Inet(new InetSocketAddress(instance.host, instance.port), Addr.Metadata.empty)
          }
        Addr.Bound(addresses)
      case Throw(_: NoBrokersAvailableException | _: FailedFastException) =>
        warn(s"No brokers available for Phoenix using phoenix itself, falling back to seed nodes")
        //TODO:Where is the retry policy!
        //TODO:Poor implementation of retry strategy, implement it properly!
        retryNumber += 1
        if (retryNumber == maxRetryNumber) {
          Addr.Failed(new Exception() {
            override def fillInStackTrace(): Throwable = this
          })
        } else {
          initializeAddr()
        }
      case Throw(throwable) =>
        error(s"Looking up X failed with $throwable")
        Addr.Failed(new Exception(throwable.toString) {
          override def fillInStackTrace(): Throwable = this
        })
    }
  }

  def initializeAddr(): Addr = Addr.Bound(masterNode)


  override def close(deadline: Time): Future[Unit] = {
    resolvePool {
      timer.stop()
    }
  }

}

object CustomResolver extends Logging {

  val lookupEndpoint = LookupQuery("politrons.com", "/instances/*/*/*", "GET")

  /**
    * Determine the resulting [[Addr]] based on the current known [[Addr]] and a newly tried lookup.
    *
    * @todo merge some cases
    * @param current the [[Addr]] that has been already determined
    * @param next    the [[Addr]] result from a new lookup
    * @return An optimistic [[Addr]] that keeps track of bound addresses in case of failure
    */
  def merge(current: Addr, next: Addr): Addr = (current, next) match {
    // Happy case where we found a fresh set of instances
    case (_, Addr.Bound(_, _)) => next
    // When we come from a Bound state and encounter anything other than Bound in the next,
    // return the current
    case (Addr.Bound(_, _), _) => current

    // Remember previous state in case of a lookup error
    case (Addr.Pending | Addr.Bound(_, _) | Addr.Neg, Addr.Failed(_)) => current

    // When next state is Pending, keep the current state.
    case (_, Addr.Pending) => current

    // When the next state is Neg, keep the next state.
    case (_, Addr.Neg) => next

    // When coming from a failed Addr and keeping a Failed Addr, roll forward.
    case (Addr.Failed(_), Addr.Failed(_)) => next
  }

}

