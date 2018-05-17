package types

object MonadFeature extends App {


  trait MonadType[F[_]] {

    def pure[A](a: A): F[A]

    def map[A, B](a: F[A])(f: A => B): F[B]

    def flatMap[A, B](a: F[A])(f: A => F[B]): F[B]

  }

  private implicit val monadOption = new MonadType[Option] {

    override def pure[A](a: A): Option[A] = Some(a)

    override def map[A, B](input: Option[A])(f: A => B): Option[B] = {
      input match {
        case Some(a) => Some(f(a))
        case None => None
      }
    }

    override def flatMap[A, B](input: Option[A])(f: A => Option[B]): Option[B] = {
      input.flatMap(value => f(value))
    }
  }

  val optionIntValue = monadOption.pure(10)
  private val maybeInt: Option[Int] = monadOption.map(optionIntValue)(a => a * 100)
  println(maybeInt)

  private val maybeFlatMapInt: Option[Int] = monadOption.flatMap(optionIntValue)(a => monadOption.pure(a * 2000))
  println(maybeFlatMapInt)

  val optionStringValue = monadOption.pure("hello")
  private val maybeString: Option[String] = monadOption.map(optionStringValue)(a => s"$a applicative world")
  println(maybeString)



}
