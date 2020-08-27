package com.features.grpc

import com.features.zio.helloworld.{ConnectorManagerGrpc, RequestInfo, ResponseInfo}
import io.grpc.{ManagedChannel, ManagedChannelBuilder, ServerBuilder}

import scala.concurrent.{ExecutionContext, Future}

object ZIOgRPC extends App {

  val server = ServerBuilder.forPort(9999)
    .addService(ConnectorManagerGrpc.bindService(new ConnectorManagerImpl, ExecutionContext.global)).asInstanceOf[ServerBuilder[_]].build
  server.start

  val channel: ManagedChannel = ManagedChannelBuilder.forAddress("localhost", 9999)
    .usePlaintext()
    .asInstanceOf[ManagedChannelBuilder[_]]
    .build()

  val request = RequestInfo(name = "World")

  val blockingStub = ConnectorManagerGrpc.blockingStub(channel)
  val reply: ResponseInfo = blockingStub.connectorRequest(request)
  println(reply.message)


}


private class ConnectorManagerImpl extends ConnectorManagerGrpc.ConnectorManager {
  override def connectorRequest(req: RequestInfo): Future[ResponseInfo] = {
    val reply = ResponseInfo(message = s"Hello ${req.name}")
    Future.successful(reply)
  }

}
