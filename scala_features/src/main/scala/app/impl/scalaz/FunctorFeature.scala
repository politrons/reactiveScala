package app.impl.scalaz

import java.util.concurrent.TimeUnit._

import org.junit.Test

import scala.concurrent.Await._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration.Duration._

/**
  * Created by pabloperezgarcia on 04/11/2017.
  *
  * With Scalaz we can create functors, a type class that allow us unwrap a F[A] treat the value/transform A to B and wrap again in F[B]
  * This is really handy when you´e using wrappers as Option, Either, Future or whatever Type class that contains values, and you
  * want to work with the values in your functions, transform those values and then wrap it again in their F
  */
class FunctorFeature {

  import scalaz.Functor

  case class Container[A](first: A, second: A)

  case class CustomMonad[A](value: A) {
    def map[B](f: A => B): CustomMonad[B] = CustomMonad(f(value))
  }

  /**
    * Functor for Container type implement map to apply the function over the values of container and wrap it up again
    * in a Container
    *
    */
  implicit val containerFunctor = new Functor[Container] {
    def map[A, B](input: Container[A])(f: A => B): Container[B] = Container(f(input.first), f(input.second))
  }

  /**
    * Functor for option type implement map to unwrap the option and get the value apply the function and wrap it up again
    * in an Option. As side effect if the option does not contain value return None and not apply the function
    */
  implicit val optionFunctor = new Functor[Option] {
    def map[A, B](input: Option[A])(f: A => B): Option[B] = {
      input match {
        case Some(a) => Option(f(a))
        case None => None
      }
    }
  }

  /**
    * Functor for Future type implement map to unwrap the future and get the value apply the function and wrap it up again
    * in an Future.
    */
  implicit val futureFunctor = new Functor[Future] {
    def map[A, B](input: Future[A])(f: A => B): Future[B] = input.map(value => f(value))
  }

  /**
    * Functor that use the CustomMonad type created. Implement map to unwrap the CustomMonad and get the value apply the function and wrap it up again
    * in an CustomMonad.
    */
  implicit val customFunctor = new Functor[CustomMonad] {
    def map[A, B](input: CustomMonad[A])(f: A => B): CustomMonad[B] = input.map(value => f(value))
  }

  /**
    * Using the functor we pass the fa Value and function f(A=>B)
    */
  @Test
  def main(): Unit = {
    println(containerFunctor(Container(1, 2))(value => value * 100))
    println(containerFunctor(Container("Hello", "world"))(value => value.toUpperCase))
    println(optionFunctor(Option("Hello world??"))(value => value.toUpperCase))
    println(optionFunctor(Option.empty[String])(value => value.toUpperCase))
    println(result(futureFunctor(eventualString)(value => value.toUpperCase), create(10, SECONDS)))
    println(result(futureFunctor(eventualInt)(value => value * 200), create(10, SECONDS)))
    println(customFunctor(CustomMonad("Hello custom monad"))(value => value.toUpperCase))
    println(customFunctor(CustomMonad(1000))(value => value * 9))
    //    println(customFunctor(Bla("Hello bla monad"))(value => value.asInstanceOf[String].toUpperCase))
  }


  //  case class Bla[A <: String](value: String) extends CustomMonad {
  //    override def map[B <: String](f: A => B): Bla[B] = Bla(f(value))
  //  }

  val eventualString = Future {
    "Will be Hello world"
  }

  val eventualInt = Future {
    10
  }
}
