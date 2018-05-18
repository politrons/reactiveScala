package types

object ApplicativeFeature extends App {

  /**
    * Applicative type class has a constructor of Type T and is used when we have a function that receive two argumemnts
    * as the function just process the first element it apply curried so it return a function so here what we have is that
    * situation when I have the second argument F[A] to process and they pass me a function inside the constructor F[A => B]
    */
  trait ApplicativeType[F[_]] {

    def pure[A](a: A): F[A]

    def ap[A, B](fa: F[A])(f: F[A => B]): F[B]

    def product[A, B, C](a: F[A], b: F[B])(func: A => B => C): F[C]

  }

  //  Option
  //__________
  private implicit val applicativeOption = new ApplicativeType[Option] {

    override def pure[A](a: A): Option[A] = Some(a)

    override def ap[A, B](input: Option[A])(f: Option[A => B]): Option[B] = {
      input.flatMap(value => f.map(func => func(value)))
    }

    override def product[A, B, C](a: Option[A], b: Option[B])(func: A => B => C): Option[C] = {
      a.flatMap(aVal => b.map(bVal => {
        val bToC = func(aVal)
        bToC(bVal)
      }))
    }
  }

  val oVal1 = applicativeOption.pure("hello applicative world")
  private val maybeTuple: Option[String] = applicativeOption.ap(oVal1)(applicativeOption.pure(x => x.toUpperCase))
  println(maybeTuple)


  //    Type Class
  //  _____________
  private val optionString = runType[String, String, Option](oVal1, Option(a => s"$a applicative Option type class"))
  println(optionString)

  def runType[A, B, F[_]](a: F[A], f: F[A => B])(implicit applicativeType: ApplicativeType[F]): F[B] = {
    applicativeType.ap(a)(f)
  }


}
