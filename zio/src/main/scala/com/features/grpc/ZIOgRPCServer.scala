package com.features.grpc

import com.features.zio.connectorManager.{ConnectorInfoDTO, ConnectorManagerGrpc, ResponseInfo}
import io.grpc.ServerBuilder
import zio.{Has, UIO, ULayer, ZIO, ZLayer}

import scala.concurrent.{ExecutionContext, Future}
import zio.Runtime.{default => Main}

object ZIOgRPCServer extends App {

  private val port = 9999

  trait ConnectorManager {
    def getConnectorManager: UIO[ConnectorManagerGrpc.ConnectorManager]
  }

  val connectorManagerDep: ULayer[Has[ConnectorManager]] = ZLayer.succeed(new ConnectorManager {
    override def getConnectorManager: UIO[ConnectorManagerGrpc.ConnectorManager] = ZIO.succeed {
      (connector: ConnectorInfoDTO) => {
        val reply = ResponseInfo(message = s"Some logic ${connector.requestInfo} of ${connector.connectorName}")
        Future.successful(reply)
      }
    }
  })

  def getConnectorManager: ZIO[Has[ConnectorManager], Nothing, ConnectorManagerGrpc.ConnectorManager] =
    ZIO.accessM(_.get.getConnectorManager)

  private val serverProgram: ZIO[Has[ConnectorManager], Throwable, Unit] = (for {
    connectorManager <- getConnectorManager
    service <- ZIO.effect(ConnectorManagerGrpc.bindService(connectorManager, ExecutionContext.global))
    server <- ZIO.effect(ServerBuilder.forPort(port)
      .addService(service)
      .asInstanceOf[ServerBuilder[_]]
      .build)
    _ <- ZIO.effect(server.start())
    _ <- ZIO.succeed(println(s"ZIO gRPC server running on port $port"))
    _ <- ZIO.effect(server.awaitTermination())
  } yield ()).catchAll(t => {
    println(s"Error running ZIO gRPC server. Caused by $t")
    ZIO.fail(t)
  })

  Main.unsafeRun(serverProgram.provideCustomLayer(connectorManagerDep))

}


