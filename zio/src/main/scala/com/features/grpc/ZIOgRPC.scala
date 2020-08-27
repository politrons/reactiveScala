package com.features.grpc

import com.features.zio.connectorManager.{ConnectorInfoDTO, ConnectorManagerGrpc, ResponseInfo}
import io.grpc.{ManagedChannel, ManagedChannelBuilder, ServerBuilder}

import scala.concurrent.{Await, ExecutionContext, Future}
import scala.concurrent.duration._

object ZIOgRPC extends App {

  gRPCServer
  makeRequest()

  private def makeRequest() = {
    val connectorManagerStub = createConnectorManager()
    val request = ConnectorInfoDTO(connectorName = "Rest", requestInfo = "READ")
    val reply: Future[ResponseInfo] = connectorManagerStub.connectorRequest(request)
    println(Await.result(reply, 10 seconds).message)
  }

  private def createConnectorManager():ConnectorManagerGrpc.ConnectorManagerStub = {
    val channel: ManagedChannel = ManagedChannelBuilder.forAddress("localhost", 9999)
      .usePlaintext()
      .asInstanceOf[ManagedChannelBuilder[_]]
      .build()
   ConnectorManagerGrpc.stub(channel)
  }

  private def gRPCServer = {
    val service = ConnectorManagerGrpc.bindService(new ConnectorManagerImpl, ExecutionContext.global)
    val server = ServerBuilder.forPort(9999)
      .addService(service)
      .asInstanceOf[ServerBuilder[_]]
      .build
    server.start
  }

  private class ConnectorManagerImpl extends ConnectorManagerGrpc.ConnectorManager {
    override def connectorRequest(connector: ConnectorInfoDTO): Future[ResponseInfo] = {
      val reply = ResponseInfo(message = s"Some logic ${connector.requestInfo} of ${connector.connectorName}")
      Future.successful(reply)
    }

  }

}


