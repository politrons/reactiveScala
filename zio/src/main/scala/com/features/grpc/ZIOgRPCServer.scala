package com.features.grpc

import com.features.zio.connectorManager.{ConnectorInfoDTO, ConnectorManagerGrpc, ResponseInfo}
import io.grpc.{ManagedChannel, ManagedChannelBuilder, ServerBuilder}

import scala.concurrent.{Await, ExecutionContext, Future}
import scala.concurrent.duration._

object ZIOgRPCServer extends App {

  val service = ConnectorManagerGrpc.bindService(new ConnectorManagerImpl, ExecutionContext.global)
  val server = ServerBuilder.forPort(9999)
    .addService(service)
    .asInstanceOf[ServerBuilder[_]]
    .build
  server.start
  server.awaitTermination()

  private class ConnectorManagerImpl extends ConnectorManagerGrpc.ConnectorManager {
    override def connectorRequest(connector: ConnectorInfoDTO): Future[ResponseInfo] = {
      val reply = ResponseInfo(message = s"Some logic ${connector.requestInfo} of ${connector.connectorName}")
      Future.successful(reply)
    }

  }

}


