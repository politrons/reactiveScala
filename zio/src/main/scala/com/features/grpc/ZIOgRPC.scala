package com.features.grpc

import com.features.zio.helloworld.{GreeterGrpc, HelloReply, HelloRequest}
import io.grpc.{ManagedChannel, ManagedChannelBuilder, Server, ServerBuilder, ServerServiceDefinition}
import io.grpc.Server
import io.grpc.ServerBuilder

import scala.concurrent.{ExecutionContext, Future}

object ZIOgRPC extends App {

  val server = ServerBuilder.forPort(9999)
    .addService(GreeterGrpc.bindService(new GreeterImpl, ExecutionContext.global)).asInstanceOf[ServerBuilder[_]].build
  server.start

  val channel: ManagedChannel = ManagedChannelBuilder.forAddress("localhost", 9999)
    .usePlaintext()
    .asInstanceOf[ManagedChannelBuilder[_]]
    .build()

  val request = HelloRequest(name = "World")

  val blockingStub = GreeterGrpc.blockingStub(channel)
  val reply: HelloReply = blockingStub.sayHello(request)
  println(reply)


}


private class GreeterImpl extends GreeterGrpc.Greeter {
  override def sayHello(req: HelloRequest): Future[HelloReply] = {
    val reply = HelloReply(message = "Hello " + req.name)
    Future.successful(reply)
  }

}
