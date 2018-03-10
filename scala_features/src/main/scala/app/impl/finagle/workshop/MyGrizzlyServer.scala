package app.impl.finagle.workshop

import org.glassfish.grizzly.http.server.{HttpHandler, HttpServer, Request, Response}


object MyGrizzlyServer extends App {

  val server = HttpServer.createSimpleServer("", "localhost", 8080)
  server.getServerConfiguration.addHttpHandler(new HttpHandler() {
    @throws[Exception]
    def service(request: Request, response: Response): Unit = {
      response.getWriter.write("hello world")
    }
  }, "/hello_server")
  try {
    server.start()
    System.out.println("Press any key to stop the server...")
    System.in.read
  } catch {
    case e: Exception =>
      System.err.println(e)
  }

}
