package app.impl.scalaz

import org.junit.Test

class MonadIO {

  object Pure {
    sealed trait IO[A] {

      def flatMap[B](f: A => IO[B]): IO[B] =
        Suspend(() => f(this.run))

      def map[B](f: A => B): IO[B] =
        Return(() => f(this.run))

      def run: A =
        this match {
          case Return(a) => a()
          case Suspend(s) => s().run
        }
    }
    final case class Return[A](a: () => A) extends IO[A]
    final case class Suspend[A](s: () => IO[A]) extends IO[A]

    object IO {
      def point[A](a: => A): IO[A] =
        Return(() => a)
    }

    def println(msg: String): IO[Unit] =
      IO.point(Predef.println(msg))
  }

  val io =
    for {
      _ <- Pure.println("Starting work now.")
      // Do some pure work
      x = 1 + 2 + 3
      _ <- Pure.println("All done. Home time.")
    } yield x

  def run = io.run


  @Test
  def main(): Unit = {
    run
  }

}
