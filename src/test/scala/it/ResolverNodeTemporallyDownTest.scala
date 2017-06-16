package it

import com.twitter.finagle
import com.twitter.finagle.http.{Request, Response}
import com.twitter.finagle.{Http, ListeningServer, Service}
import com.twitter.util.{Await, Future}
import grizzled.slf4j.Logging
import org.scalatest.concurrent.Eventually._
import org.scalatest.time.{Seconds, Span}
import org.scalatest.{BeforeAndAfterAll, FlatSpec, Matchers}

class ResolverNodeTemporallyDownTest extends FlatSpec with Matchers with BeforeAndAfterAll with Logging {

  behavior of "completely loaded setup"

  it should "on query, first send it to the master node, and then to other configured nodes" in {
    debug("Executing tests")

    val masterPort = 8443
    var normalNodeServer: ListeningServer = null

    val masterNodeSvc = Service.mk { _: Request =>
      val rep = Response()
      rep.setContentString(
        s"""
           |{"instances":[
           |  {"host":"127.0.0.1","port":"1981","datacenter":"master","confidence":100}
           |]}
        """.stripMargin
      )
      Future.value(rep)
    }
    lazy val normalNodeSvc = Service.mk[Request, Response] { _: Request => {
      val rep = Response()
      rep.setContentString(
        s"""
            {"instances":[
              {"host":"127.0.0.1","port":"1981","datacenter":"normal","confidence":100}
            ]}
        """.stripMargin
      )
      Future.value(rep)
    }
    }

    val masterServer: ListeningServer = Http.server
      .configuredParams(finagle.Http.Http2)
      .configured(finagle.Http.Netty4Impl)
      .serve(s"127.0.0.1:$masterPort", masterNodeSvc)


    val client =
      Http.client.newService("customResolver!politrons.com:/instances/*/*/*:GET", "service-discovery")

    var index = 0
    eventually(timeout(Span(60, Seconds)), interval(Span(1, Seconds))) {
      index += 1
      if (index == 5) {
        normalNodeServer = Http.server.serve(s"127.0.0.1:1981", normalNodeSvc)
      }
      Await.result(client(Request())).contentString should include("normal")
    }
    Await.all(masterServer.close(), normalNodeServer.close(), client.close())
  }
}
