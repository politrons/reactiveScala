package features.zio

import zio.ZIO

object ZIOFlip extends App {

  val main: zio.Runtime[zio.ZEnv] = zio.Runtime.default

  case class MyError(t: Throwable)

  private val program: ZIO[Boolean, MyError, Unit] = for {
    mustFail <- ZIO.environment[Boolean]
    value <- ZIO.effect(if (!mustFail) "Hello flip world!" else throw new IllegalAccessException())
      .flip.map(t => MyError(t))
      .flip
    upperValue <- ZIO.succeed(value.toUpperCase())
    _ <- ZIO.succeed(println(upperValue))
  } yield ()

  main.unsafeRun(program.provide(false))
  main.unsafeRun(program.provide(true))

}
