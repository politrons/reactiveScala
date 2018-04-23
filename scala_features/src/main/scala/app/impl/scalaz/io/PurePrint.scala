package app.impl.scalaz.io

import scalaz.ioeffect.{IO, RTS, SafeApp}
import scalaz.ioeffect.console._
import java.io.IOException

import org.junit.Test

object PurePrint extends SafeApp {

  type Error = IOException

  def run(args: List[String]): IO[Error, Unit] =
    putStrLn("Hello! What is your name?")
      .flatMap(_ => getStrLn
        .flatMap(response => putStrLn("Hello, " + response + ", good to meet you!")))

}


