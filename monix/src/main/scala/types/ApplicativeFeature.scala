package types

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.concurrent.{Await, Future}

object ApplicativeFeature extends App {


  trait ApplicativeType[F[_]] {

    def pure[A](a: A): F[A]

    def product[A, B](a: F[A], b: F[B]): F[(A, B)]

    def map[A, B](a: F[A])(f: A => B): F[B]

  }

  //  Option
  //__________
  private implicit val applicativeOption = new ApplicativeType[Option] {

    override def pure[A](a: A): Option[A] = Some(a)

    override def product[A, B](a: Option[A], b: Option[B]): Option[(A, B)] = {
      a.flatMap(value => b.map(value1 => {
        val tuple = (value, value1)
        tuple
      }))
    }

    override def map[A, B](input: Option[A])(f: A => B): Option[B] = {
      input match {
        case Some(a) => Some(f(a))
        case None => None
      }
    }
  }

  val optionIntValue = applicativeOption.pure(10)
  private val maybeInt: Option[Int] = applicativeOption.map(optionIntValue)(a => a * 100)
  println(maybeInt)

  val optionStringValue = applicativeOption.pure("hello")
  private val maybeString: Option[String] = applicativeOption.map(optionStringValue)(a => s"$a applicative world")
  println(maybeString)

  val optionStringValue1 = applicativeOption.pure("hello")
  val optionStringValue2 = applicativeOption.pure(" world")
  private val maybeTuple: Option[(String, String)] = applicativeOption.product(optionStringValue1, optionStringValue2)
  println(maybeTuple)

  //  Future
  //__________
  private implicit val applicativeFuture = new ApplicativeType[Future] {

    override def pure[A](a: A): Future[A] = Future(a)

    override def product[A, B](a: Future[A], b: Future[B]): Future[(A, B)] = {
      a.flatMap(value => b.map(value1 => (value, value1)))
    }

    override def map[A, B](input: Future[A])(f: A => B): Future[B] = {
      input.map(value => f(value))
    }
  }

  private val stringValue: Future[String] = applicativeFuture.pure("Hello")
  val future = applicativeFuture.map(stringValue)(value => s"$value Applicative world in the future")
  println(Await.result(future, 10 seconds))


}
