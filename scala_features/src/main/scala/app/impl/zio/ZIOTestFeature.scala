package app.impl.zio

import zio._
import zio.console._
import zio.test._
import zio.test.Assertion._
import zio.test.environment._

import ZIOTestFeature._

object ZIOTestFeature {
  def sayHello: ZIO[Console, Nothing, Unit] = console.putStrLn("Hello, World!")

  def returnValueProgram: Task[String] = ZIO.effect("Hello world with effect!!")
}

object ZIOTestFeatureSpec extends DefaultRunnableSpec {
  def spec = suite("HelloWorldSpec")(
    testM("First test scenario") {
      for {
        _      <- sayHello
        output <- TestConsole.output
      } yield assert(output)(equalTo(Vector("Hello, World!\n")))
    },
    testM("Second test scenario") {
      for {
        output <- returnValueProgram
      } yield assert(output)(equalsIgnoreCase("Hello world with effect!!"))
    }
  )


}

