package app.impl.finagle

import io.vertx.core._
import io.vertx.core.http.HttpServerRequest


/**
  * Created by pabloperezgarcia on 22/02/2017.
  *
  */
class MyVerticle extends AbstractVerticle {

  override def start(): Unit = {
    val handle = new Handler[HttpServerRequest] {
      override def handle(event: HttpServerRequest): Unit = {
//        Http.server.serve(":*", FinagleService.service)
      }
    }
    this.getVertx.createHttpServer()
      .requestHandler(handle)
      .listen(8888)
  }
}
//
//object FinagleService {
//  val service = new Service[HttpServerRequest, HttpServerResponse] {
//    def apply(req: HttpServerRequest): Future[HttpServerResponse] = {
//      Future.value(req.response().end("This is working!"))
//    }
//  }
//}

object run extends App {
  val initHandler = new Handler[AsyncResult[String]]() {
    override def handle(event: AsyncResult[String]): Unit = {
      if (event.succeeded()) {
        println("server initialized")
      }
    }
  }
  Vertx.vertx.deployVerticle(classOf[Verticle].getName, initHandler)
}