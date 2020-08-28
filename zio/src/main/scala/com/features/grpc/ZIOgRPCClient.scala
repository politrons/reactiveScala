package com.features.grpc

import com.features.zio.connectorManager.{ConnectorInfoDTO, ConnectorManagerGrpc}
import io.grpc.{ManagedChannel, ManagedChannelBuilder}
import zio.{Task, ZIO}
import zio.Runtime.{default => Main}

object ZIOgRPCClient extends App {

  private val clientProgram: ZIO[Any, Throwable, Unit] = (for {
    channel <- createChannel()
    connectorManagerStub <- ZIO.effect(ConnectorManagerGrpc.stub(channel))
    request <- ZIO.effect(ConnectorInfoDTO(connectorName = "Rest", requestInfo = "READ"))
    response <- ZIO.fromFuture(_ => connectorManagerStub.connectorRequest(request))
    _ <- ZIO.succeed(println(s"Response: ${response.message}"))
  } yield ()).catchAll(t => {
    println(s"Error running ZIO gRPC client. Caused by $t")
    ZIO.fail(t)
  })
  Main.unsafeRun(clientProgram)

  private def createChannel(): Task[ManagedChannel] = {
    ZIO.effect {
      ManagedChannelBuilder.forAddress("localhost", 9999)
        .usePlaintext()
        .asInstanceOf[ManagedChannelBuilder[_]]
        .build()
    }
  }

}

/*
java -cp zio-1.0-SNAPSHOT-jar-with-dependencies.jar com.features.grpc.ZIOgRPCServer
*/



