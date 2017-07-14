package app.impl.finagle.resolver

import com.twitter.util.Future

/**
  * The information sent by the ServiceDiscovery
  *
  * @param host       hostname or ip-address
  * @param port       port
  * @param datacenter the name of the datacenter
  * @param confidence the confidence that SD has that this instance still is operational
  */
case class InstanceResponse(host: String, port: Int, datacenter: String, confidence: Int)


case class LookupQuery(host: String, pathTemplate: String, method: String) {
  override def toString: String = s"$host:$pathTemplate:$method"
}

trait CustomClient {
  /**
    *
    * @param lookupQuery the query to execute
    * @return a future value of set of instances
    */
  def lookup(lookupQuery: LookupQuery): Future[Option[Set[InstanceResponse]]]

}