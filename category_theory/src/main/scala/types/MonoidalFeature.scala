package types

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.concurrent.{Await, Future}

object MonoidalFeature extends App {


  /**
    * MonoidalType type class has a constructor of Type F[_] and is used when we have to compose two elements at same time
    * as it would happen in haskell since curried by default, where a function with two arguments like
    * a => b => c actually func(a) => func(a)( b) => c so here then we need to apply the very same function for both
    * elements and then return a tuple with both values.
    */
  trait MonoidalType[F[_]] {

    def pure[A](a: A): F[A]

    def product[A, B](a: F[A], b: F[A])(f: A => B): F[(B, B)]

  }

  //  Option
  //__________
  private implicit val monoidalOption = new MonoidalType[Option] {

    override def pure[A](a: A): Option[A] = Some(a)

    override def product[A, B](a: Option[A], b: Option[A])(f: A => B): Option[(B, B)] = {
      a.flatMap(value => b.map(value1 => (f(value), f(value1))))
    }

  }

  val oVal1 = monoidalOption.pure("hello")
  val oVal2 = monoidalOption.pure(" world")
  private val maybeTuple: Option[(String, String)] = monoidalOption.product(oVal1, oVal2)(x => x.toUpperCase)
  println(maybeTuple)

  //  Future
  //__________
  private implicit val monoidalFuture = new MonoidalType[Future] {

    override def pure[A](a: A): Future[A] = Future(a)

    override def product[A, B](a: Future[A], b: Future[A])(f: A => B): Future[(B, B)] = {
      a.flatMap(value => b.map(value1 => (f(value), f(value1))))
    }

  }

  private val fVaL1: Future[String] = monoidalFuture.pure("Hello")
  private val fVaL2: Future[String] = monoidalFuture.pure(" World")
  val future = monoidalFuture.product(fVaL1, fVaL2)(value => value.toUpperCase)
  println(Await.result(future, 10 seconds))

  //    Type Class
  //  _____________
  private val optionString = runType[String, String, Option](oVal1,oVal2, a => s"$a applicative Option type class")
  println(optionString)

  private val futureString = runType[String, String, Future](fVaL1,fVaL2, a => s"$a applicative future type class")
  println(Await.result(futureString, 10 seconds))

  def runType[A, B, F[_]](a: F[A], b: F[A], f: A => B)(implicit applicativeType: MonoidalType[F]): F[(B,B)] = {
    applicativeType.product(a, b)(f)
  }


}
