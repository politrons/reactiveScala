package it

import java.net.InetSocketAddress

import com.twitter.finagle
import com.twitter.finagle.http.{Request, Response}
import com.twitter.finagle.{Http, ListeningServer, Service}
import com.twitter.util.{Await, Future}
import grizzled.slf4j.Logging
import org.scalatest.concurrent.Eventually._
import org.scalatest.time.{Seconds, Span}
import org.scalatest.{BeforeAndAfterAll, FlatSpec, Matchers}

class ResolverTest extends FlatSpec with Matchers with BeforeAndAfterAll with Logging {

  behavior of "completely loaded setup"

  it should "on query, first send it to the master node, and then to other configured nodes" in {
    debug("Executing tests")

    val masterPort = 8443
    var normalPort: Int = 0
    var normalPort1: Int = 0


    def getPort(listeningServer: ListeningServer): Int =
      listeningServer.boundAddress.asInstanceOf[InetSocketAddress].getPort

    val masterNodeSvc = Service.mk { _: Request =>
      val rep = Response()
      rep.setContentString(
        s"""
           |{"instances":[
           |  {"host":"127.0.0.1","port":$normalPort,"datacenter":"master","confidence":100},
           |  {"host":"127.0.0.1","port":$normalPort1,"datacenter":"master","confidence":100}
           |
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
              {"host":"127.0.0.1","port":$normalPort,"datacenter":"normal","confidence":100}
            ]}
        """.stripMargin
      )
      Future.value(rep)
    }
    }
    lazy val normalNodeSvc1 = Service.mk[Request, Response] { _: Request => {
      val rep = Response()
      rep.setContentString(
        s"""
            {"instances":[
              {"host":"127.0.0.1","port":$normalPort1,"datacenter":"normal1","confidence":100}
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

    val normalNodeServer: ListeningServer = Http.server.serve(s"127.0.0.1:*", normalNodeSvc)
    val normalNodeServer1: ListeningServer = Http.server.serve(s"127.0.0.1:*", normalNodeSvc1)

    normalPort = getPort(normalNodeServer)
    normalPort1 = getPort(normalNodeServer1)

    val client =
      Http.client.newService("customResolver!politrons.com:/instances/*/*/*:GET", "service-discovery")

    eventually(timeout(Span(60, Seconds)), interval(Span(1, Seconds))) {
      Await.result(client(Request())).contentString should include("normal")
      Await.result(client(Request())).contentString should include("normal1")
    }
    Await.all(masterServer.close(), normalNodeServer.close(),normalNodeServer1.close(), client.close())
  }
}
