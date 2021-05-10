
import zhttp.http._
import zhttp.service.Server

object ZIOHttp extends App{

  val runtime: zio.Runtime[zio.ZEnv] = zio.Runtime.default


  val app = Http.collect[Request, Throwable] {
    case Method.GET -> Root / "text" => Response.text("Hello World!")
  }

  runtime.unsafeRun(Server.start(8090, app))

}