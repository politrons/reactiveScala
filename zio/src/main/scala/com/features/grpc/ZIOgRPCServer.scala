package com.features.grpc

import com.features.zio.connectorManager.{ConnectorInfoDTO, ConnectorManagerGrpc, ResponseInfo}
import io.grpc.{Server, ServerBuilder, ServerServiceDefinition}
import zio.Runtime.{default => Main}
import zio.{Has, Task, ULayer, ZIO, ZLayer}

import scala.concurrent.{ExecutionContext, ExecutionContextExecutor, Future}

object ZIOgRPCServer extends App {

  private val port = 9999

  /**
   * Description of ConnectorManager dependency
   */
  trait ConnectorManager {
    def getConnectorManager: Task[ConnectorManagerGrpc.ConnectorManager]
  }

  /**
   * Description of grpc Server dependency
   */
  trait GRPCServer {
    def getServer(service: ServerServiceDefinition): Task[Server]
  }

  /**
   * Implementation/Behavior of ZLayer as dependency to be passed to the program to obtain ConnectorManager.
   */
  val connectorManagerDependency: ULayer[Has[ConnectorManager]] = ZLayer.succeed(new ConnectorManager {
    override def getConnectorManager: Task[ConnectorManagerGrpc.ConnectorManager] = ZIO.succeed {
      (connector: ConnectorInfoDTO) => {
        val reply = ResponseInfo(message = s"Some logic ${connector.requestInfo} of ${connector.connectorName}")
        Future.successful(reply)
      }
    }
  })

  /**
   * Implementation/Behavior of ZLayer as dependency of GRPCServer to be passed to the program to obtain grpc Server.
   */
  val serverDependency: ULayer[Has[GRPCServer]] = ZLayer.succeed((service: ServerServiceDefinition) => {
    ZIO.effect(ServerBuilder.forPort(port)
      .addService(service)
      .asInstanceOf[ServerBuilder[_]]
      .build)
  })

  val ecDependency: ULayer[Has[ExecutionContextExecutor]] = ZLayer.succeed(ExecutionContext.global)

  /**
   * DSL/Structure of how to obtain the Connector manager dependency inside the program
   */
  def getConnectorManager: ZIO[Has[ConnectorManager], Throwable, ConnectorManagerGrpc.ConnectorManager] =
    ZIO.accessM(_.get.getConnectorManager)

  /**
   * DSL/Structure of how to obtain the grpc Server dependency inside the program
   */
  def getGRPCServer(service: ServerServiceDefinition): ZIO[Has[GRPCServer], Throwable, Server] =
    ZIO.accessM(_.get.getServer(service))

  def getExecutorContext: ZIO[Has[ExecutionContextExecutor], Nothing, ExecutionContextExecutor] =
    ZIO.access(_.get)

  /**
   * Server program that receive the ConnectorManager, GRPCServer as dependency.
   * We extract those dependencies using the behavior functions, and we use it to bind it
   * together and run the server
   */
  private val serverProgram: ZIO[Has[ConnectorManager] with Has[GRPCServer] with Has[ExecutionContextExecutor], Throwable, Unit] =
    (for {
      connectorManager <- getConnectorManager
      executorContext <- getExecutorContext
      service <- ZIO.effect(ConnectorManagerGrpc.bindService(connectorManager, executorContext))
      server <- getGRPCServer(service)
      _ <- ZIO.effect(server.start())
      _ <- ZIO.succeed(println(s"ZIO gRPC server running on port $port"))
      _ <- ZIO.effect(server.awaitTermination())
    } yield ()).catchAll(t => {
      println(s"Error running ZIO gRPC server. Caused by $t")
      ZIO.fail(t)
    })

  val multiDependency: ZLayer[Any, Any, Has[ConnectorManager] with Has[GRPCServer] with Has[ExecutionContextExecutor]] =
    connectorManagerDependency ++ serverDependency ++ ecDependency

  Main.unsafeRun(serverProgram.provideCustomLayer(multiDependency))

}


