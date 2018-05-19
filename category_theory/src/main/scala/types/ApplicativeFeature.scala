package types

object ApplicativeFeature extends App {

  /**
    * Applicative type class has a constructor of Type T and is used when we have a function that receive two argumemnts
    * as the function just process the first element it apply curried so it return a function so here what we have is that
    * situation when I have the second argument F[A] to process and they pass me a function inside the constructor F[A => B]
    *
    * In haskell since we have curried by default, a function with two arguments like
    * a => b => c if we apply func(a) it will return another function waiting for b func(b) in order to return [c]
    */
  trait ApplicativeType[F[_]] {

    def pure[A](a: A): F[A]

    /**
      * Curried operator receive a function with more arguments that you actually receive as input, so instead of
      * perform the execution of the function return another function expecting one argument less than initially.
      */
    def curried[A, B](a: F[A])(func: A => A => B): F[A => B]

    /**
      * Apply operator it receive an input and a function wrapped in a constructor F[A=>B] since it was an output
      * of the curried operator. Here we unwrap the input value and also the function and apply.
      */
    def apply[A, B](fa: F[A])(f: F[A => B]): F[B]

  }

  //  Option
  //__________
  private implicit val applicativeOption = new ApplicativeType[Option] {

    override def pure[A](a: A): Option[A] = Some(a)

    override def curried[A, B](a: Option[A])(func: A => A => B): Option[A => B] = {
      a.map(value => func(value))
    }

    override def apply[A, B](input: Option[A])(f: Option[A => B]): Option[B] = {
      input.flatMap(value => f.map(func => func(value)))
    }

  }

  //  apply
  val oVal1 = applicativeOption.pure("hello applicative world")
  private val maybeTuple: Option[String] = applicativeOption.apply(oVal1)(applicativeOption.pure(x => x.toUpperCase))
  println(maybeTuple)

  //  curried
  val oval2 = applicativeOption.pure(" again!!!")
  private val curriedFunc: Option[String => String] = applicativeOption.curried(oVal1)(a => b => a + b)
  private val maybeString: Option[String] = applicativeOption.apply(oval2)(curriedFunc)
  println(maybeString)


  //    Type Class
  //  _____________
  private val optionString = runType[String, String, Option](oVal1, Option(a => s"$a applicative Option type class"))
  println(optionString)

  def runType[A, B, F[_]](a: F[A], f: F[A => B])(implicit applicativeType: ApplicativeType[F]): F[B] = {
    applicativeType.apply(a)(f)
  }


}
