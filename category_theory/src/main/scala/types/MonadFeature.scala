package types

object MonadFeature extends App {


  trait MonadType[F[_]] {

    /**
      * [Pure] operator it's receive a simple Type A and we wrap into a F[A] defined by the monad.
      */
    def pure[A](input: A): F[A]

    /**
      * [Map] operator is what we normally have in functor types and is what apply a function to the data
      * to transform the data form type A => B
      *
      * @param input source to be transformed
      * @param  f    function to use to apply the input
      * @tparam A Type from the input
      * @tparam B Type for the output
      * @return
      */
    def map[A, B](input: F[A])(f: A => B): F[B]

    /**
      * [FlatMap] operator it's meant to be used for composition of functions, it's what make the difference from a Functor
      * to a Monad. The composition of functions. here we compose the output value from input source applying the function
      * which return another Monad Type F[B]
      *
      * @param input F[A]
      * @param f     function to apply the value from A
      * @tparam A input raw type
      * @tparam B output raw type
      */
    def flatMap[A, B](input: F[A])(f: A => F[B]): F[B]

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

  val initValue = monadOption.pure("Hello")
  private val optionString: Option[String] =
    monadOption.flatMap(initValue)(a => monadOption.pure(s"$a composition world".toUpperCase))
      .flatMap(value => monadOption.pure(value.concat("!!!!!")))
  println(optionString)

  private val maybeInt: Option[Int] =
    monadOption.flatMap(monadOption.pure(1))(a => monadOption.pure(a * 1000))
  println(maybeInt)


}
