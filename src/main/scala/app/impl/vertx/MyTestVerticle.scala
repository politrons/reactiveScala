import io.vertx.core._
import io.vertx.core.http.HttpServerRequest


/**
  * Created by pabloperezgarcia on 22/02/2017.
  */
class MyTestVerticle extends AbstractVerticle {

  override def start(startFuture: Future[Void]): Unit = {
    val handle = new Handler[HttpServerRequest] {
      override def handle(event: HttpServerRequest): Unit = {
        println(event)
        event.response().end("This is working!")
      }
    }
    this.getVertx.createHttpServer()
      .requestHandler(handle)
      .listen(8888)
  }
}

object run extends App {
  Vertx.vertx.deployVerticle(classOf[MyTestVerticle].getName)
}