package types

object MonadFeature extends App {


  trait MonadType[F[_]] {

    def pure[A](a: A): F[A]

    def map[A, B](a: F[A])(f: A => B): F[B]

    def flatMap[A, B](a: F[A])(f: A => F[B]): F[B]

  }

  private implicit val monadOption = new MonadType[Option] {

    /**
      * [Pure] operator it's receive a simple Type A and we wrap into a F[A] defined by the monad.
      */
    override def pure[A](a: A): Option[A] = Some(a)

    /**
      * [Map] operator is what we normally have in functor types and is what transform the data form type A => B
      * @param input source to be transformed
      * @param  f function to use to apply the input
      * @tparam A Type from the input
      * @tparam B Type for the output
      * @return
      */
    override def map[A, B](input: Option[A])(f: A => B): Option[B] = {
      input match {
        case Some(a) => Some(f(a))
        case None => None
      }
    }

    /**
      * [FlatMap] operator it's meant to be used for composition of functions, it's what make the difference from a Functor
      *  to a monad. The composition of functions. here we compose the output value from input source applying the function
      *  which return another Type F[_]
      *
      * @param input F[A]
      * @param f function to apply the value from A
      * @tparam A input raw type
      * @tparam B output raw type
      */
    override def flatMap[A, B](input: Option[A])(f: A => Option[B]): Option[B] = {
      input.flatMap(value => f(value))
    }
  }

  val initValue = monadOption.pure("Hello")
  private val optionString: Option[String] =
    monadOption.map(initValue)(a => a.toUpperCase)
  println(optionString)

  private val maybeFlatMapInt: Option[String] =
    monadOption
      .flatMap(monadOption.pure(" composition world"))(a => optionString.map(value => value.concat(a.toUpperCase)))
  println(maybeFlatMapInt)

  private val maybeInt: Option[Int] =
    monadOption.map(monadOption.pure(1))(a => a * 100)
  println(maybeInt)


}
