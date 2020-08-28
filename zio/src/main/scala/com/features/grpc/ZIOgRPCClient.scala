package com.features.grpc

import java.nio.charset.StandardCharsets

import com.features.zio.connectorManager.{ConnectorInfoDTO, ConnectorManagerGrpc}
import io.grpc.{ManagedChannel, ManagedChannelBuilder}
import org.apache.commons.io.IOUtils
import zio.{Task, ZIO}
import zio.Runtime.{default => Main}

object ZIOgRPCClient extends App {

  val process = runServerFromAnotherJVM()

  private def runServerFromAnotherJVM(): Process = {
    val path = "/Users/nb38tv/Developer/projects/reactiveScala/zio/target"
    val JavaCommand = s"java -cp $path/zio-1.0-SNAPSHOT-jar-with-dependencies.jar com.features.grpc.ZIOgRPCServer"
    val process: Process = Runtime.getRuntime.exec(JavaCommand)
    println(s"Service process alive:${process.isAlive}")
    process
  }

  Thread.sleep(2000)

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
  process.destroy()

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



