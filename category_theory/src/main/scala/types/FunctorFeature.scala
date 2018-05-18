package types

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.concurrent.{Await, Future}

object FunctorFeature extends App {


  /**
    * functor type class is a Type with constructor F which unwrap your functor to apply the value over that
    * functor and then wrap again.
    * Is like when you forgot to add a wish note in your present but has been wrapped already. With Functor, we
    * can unwrap that present transform the input and then wrap again.
    */
  trait functorType[F[_]] {

    /**
      * Pure just receive an element A and wrap in a constructor F[A]
      */
    def pure[A](a: A): F[A]

    /**
      * Here we receive the functor option and we want to unwrap, apply the function and wrap again.
      *
      * @param input the functor to unwrap
      * @param f     the function to apply over the unwrapped value.
      * @tparam A type from the input
      * @tparam B type in the output
      */
    def map[A, B](input: F[A])(f: A => B): F[B]

  }

  //  Option
  //__________
  private implicit val functorOption = new functorType[Option] {

    override def pure[A](a: A): Option[A] = Some(a)

    override def map[A, B](input: Option[A])(f: A => B): Option[B] = {
      input match {
        case Some(a) => Some(f(a))
        case None => None
      }
    }
  }

  /**
    * Here we have our present, but then we want to transform and add a lovely message ;)
    */
  val optionStringValue:Option[String] = functorOption.pure("Present")
  private val maybeString: Option[String] =
    functorOption.map(optionStringValue)(a => s"$a I love you!")
  println(maybeString)

  val optionIntValue = functorOption.pure(10)
  private val maybeInt: Option[Int] = functorOption.map(optionIntValue)(a => a * 100)
  println(maybeInt)


  //  Future
  //__________
  private implicit val functorFuture = new functorType[Future] {

    override def pure[A](a: A): Future[A] = Future(a)

    override def map[A, B](input: Future[A])(f: A => B): Future[B] = {
      input.map(value => f(value))
    }
  }

  private val stringValue: Future[String] = functorFuture.pure("Hello")
  val future = functorFuture.map(stringValue)(value => s"$value functor world in the future")
  println(Await.result(future, 10 seconds))

  //  Type Class
  //_____________
  private val optionString = runType[String, String, Option](functorOption.pure("Hello"), a => s"$a functor Option type class")
    .map(value => value.concat("!!!!"))
  println(optionString)

  private val futureString = runType[String, String, Future](stringValue, a => s"$a functor future type class")
    .map(value => value.toUpperCase)
  println(Await.result(futureString, 10 seconds))

  def runType[A, B, F[_]](a: F[A], f: A => B)(implicit functorType: functorType[F]): F[B] = {
    functorType.map(a)(f)
  }


}
