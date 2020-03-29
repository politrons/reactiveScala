package app.impl.http

import app.impl.http.HttpHedgedClient.HttpClient
import zio.{Has, Layer, ZIO, ZLayer}

object HttpHedgedClient {

  case class HttpClient(url: String = "", hedged: Int = 0)

  val runtime: zio.Runtime[zio.ZEnv] = zio.Runtime.default

  trait Service {

    var httpClient:HttpClient = HttpClient()

    def getHttpClient(url: String): HttpClient

    def getHedged(times: Int): HttpClient

  }

  val hasHttpService: ZLayer[Any, Nothing, Has[Service]] = ZLayer.succeed(new Service {
    override def getHttpClient(url: String): HttpClient = {
      httpClient = httpClient.copy(url = url)
      httpClient
    }

    override def getHedged(times: Int): HttpClient = {
      httpClient = httpClient.copy(hedged = times)
      httpClient
    }
  })

  def Get(url: String): ZIO[Has[HttpHedgedClient.Service], Nothing, HttpClient] = {
    ZIO.access(hasService => hasService.get.getHttpClient(url))
  }

  def Hedged(times: Int): ZIO[Has[HttpHedgedClient.Service], Nothing, HttpClient] = {
    ZIO.access(hasService => hasService.get.getHedged(times))
  }
}


object MainTest extends App {

  private val httpProgram: ZIO[Has[HttpHedgedClient.Service], Nothing, HttpClient] = for {
    _ <- HttpHedgedClient.Get("http://google.com")
    httpClient <- HttpHedgedClient.Hedged(10)
  } yield httpClient

  private val programResponse: HttpClient = HttpHedgedClient.runtime.unsafeRun {
    httpProgram.provideCustomLayer(HttpHedgedClient.hasHttpService)
  }

  println(programResponse)

}