package com.features.grpc

import com.features.zio.connectorManager.{ConnectorInfoDTO, ConnectorManagerGrpc, ResponseInfo}
import io.grpc.ServerBuilder
import zio.ZIO
import scala.concurrent.{ExecutionContext, Future}
import zio.Runtime.{default => Main}

object ZIOgRPCServer extends App {

  private val port = 9999
  private val serverProgram: ZIO[Any, Throwable, Unit] = (for {
    connectorManager <- ZIO.effect(new ConnectorManagerImpl)
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

  Main.unsafeRun(serverProgram)

  private class ConnectorManagerImpl extends ConnectorManagerGrpc.ConnectorManager {
    override def connectorRequest(connector: ConnectorInfoDTO): Future[ResponseInfo] = {
      val reply = ResponseInfo(message = s"Some logic ${connector.requestInfo} of ${connector.connectorName}")
      Future.successful(reply)
    }

  }

}


