package com.features.grpc

import com.features.zio.connectorManager.{ConnectorInfoDTO, ConnectorManagerGrpc}
import io.grpc.{ManagedChannel, ManagedChannelBuilder}
import zio.Runtime.{default => Main}
import zio.{Has, Task, UIO, ULayer, ZIO, ZLayer}

object ZIOgRPCClient extends App {

  val process = runServerFromAnotherJVM()

  /**
   * Experiment of how we can run gRPC between client and server from different JVM and still obtain a very
   * good performance, just like if both were running under the same JVM.
   */
  private def runServerFromAnotherJVM(): Process = {
    val path = "/Users/nb38tv/Developer/projects/reactiveScala/zio/target"
    val JavaCommand = s"java -cp $path/zio-1.0-SNAPSHOT-jar-with-dependencies.jar com.features.grpc.ZIOgRPCServer"
    val process: Process = Runtime.getRuntime.exec(JavaCommand)
    println(s"Service process alive:${process.isAlive}")
    process
  }

  Thread.sleep(2000)

  /**
   * Implementation of ZLayer as dependency of Channel to be injected in the program
   */
  val channel: ULayer[Has[ManagedChannel]] = ZLayer.succeed {
    ManagedChannelBuilder.forAddress("localhost", 9999)
      .usePlaintext()
      .asInstanceOf[ManagedChannelBuilder[_]]
      .build()
  }

  /**
   * DSL of how to obtain channel from the dependency
   *
   * @return ManagedChannel
   */
  def getChannel: ZIO[Has[ManagedChannel], Nothing, ManagedChannel] = ZIO.access(has => has.get)

  /**
   * Client gRPC program that receive as dependency the channel where it must connected.
   */
  private val clientProgram: ZIO[Has[ManagedChannel], Throwable, Unit] = (for {
    channel <- getChannel
    connectorManagerStub <- ZIO.effect(ConnectorManagerGrpc.stub(channel))
    request <- ZIO.effect(ConnectorInfoDTO(connectorName = "Rest", requestInfo = "READ"))
    response <- ZIO.fromFuture(_ => connectorManagerStub.connectorRequest(request))
    _ <- ZIO.succeed(println(s"Response: ${response.message}"))
  } yield ()).catchAll(t => {
    println(s"Error running ZIO gRPC client. Caused by $t")
    ZIO.fail(t)
  })
  Main.unsafeRun(clientProgram.provideCustomLayer(channel))
  //Kill the process, and the server running with it.
  process.destroy()


}


