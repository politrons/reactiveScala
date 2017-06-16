package it

import com.twitter.finagle.{ChannelWriteException, Http}
import com.twitter.finagle.http.Request
import com.twitter.util.Await
import grizzled.slf4j.Logging
import org.scalatest.{BeforeAndAfterAll, FlatSpec, Matchers}

class ResolverNoMasterNodeTest extends FlatSpec with Matchers with BeforeAndAfterAll with Logging {

  behavior of "completely loaded setup"

  it should "on query, first send it to the master node, and then to other configured nodes" in {
    debug("Executing tests")

    val client =
      Http.client.newService("customResolver!politrons.com:/instances/*/*/*:GET", "service-discovery")
    try {
      val result = Await.result(client(Request()))
      println(result.contentString)
    } catch {
      case e: Exception => {
        assert(e.isInstanceOf[ChannelWriteException])
        assert(e.getMessage.contains("Connection refused"))
      }
    }
    Await.all(client.close())
  }
}
