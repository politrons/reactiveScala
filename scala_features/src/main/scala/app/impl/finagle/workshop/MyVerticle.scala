package app.impl.finagle.workshop

import io.vertx.core._
import io.vertx.core.http.HttpServerRequest


/**
  * Created by pabloperezgarcia on 22/02/2017.
  */
class MyVerticle extends AbstractVerticle {

  override def start(startFuture: Future[Void]): Unit = {
    val handle = new Handler[HttpServerRequest] {
      override def handle(event: HttpServerRequest): Unit = {
        println(event)
        event.response().end("This is working!")
      }
    }
    this.getVertx.createHttpServer()
      .requestHandler(handle)
      .listen(8080)
    startFuture.complete()
  }
}

object MyVerticle extends App {
  val initHandler = new Handler[AsyncResult[String]]() {
    override def handle(event: AsyncResult[String]): Unit = {
      if (event.succeeded()) {
        println("server initialized")
      }
    }
  }
  Vertx.vertx.deployVerticle(classOf[MyVerticle].getName, initHandler)
}