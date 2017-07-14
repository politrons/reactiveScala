package app.impl.scalaz

import scalaz.Free._
import scalaz.{Free, ~>}


/**
  * Created by pabloperezgarcia on 12/03/2017.
  *
  *
  */
class TestDSL {

  /**
    * With type we define a new class type instead have to create an object
    * class as we have to do in Java
    */
  type Id[+X] = X

  /**
    * The next classes are our ADT algebraic data types
    */

  sealed trait Action[A]

  case class _Given(action: String, any: Any) extends Action[Any]

  case class _When(action: String, any: Any) extends Action[Any]

  case class _Then(action: String, any: Any) extends Action[Any]

  /**
    * A Free monad it´s kind like an Observable,
    * where we specify the Entry type Orders, and output type A
    */
  type ActionMonad[A] = Free[Action, A]

  def Given(action: String, any: Any): ActionMonad[Any] = liftF[Action, Any](_Given(action, any))

  implicit class customFree(free: Free[Action, Any]) {

    def When(action: String): Free[Action, Any] = free.map(any => liftF[Action, Any](_When(action, any)))

    def Then(action: String): Free[Action, Any] = free.map(any => liftF[Action, Any](_Then(action, any)))

    def And(action: String): Free[Action, Any] = free.map(any => liftF[Action, Any](_Then(action, any)))

    def runScenario = free.foldMap(actionInterpreter)

  }

  /**
    * This function return a function which receive an Order type of A and return that type
    * That type it could be anything, so using the same FreeMonad DSL we can define multiple
    * implementations types.
    *
    * @return
    */
  def actionInterpreter: Action ~> Id = new (Action ~> Id) {
    def apply[A](order: Action[A]): Id[A] = order match {
      case _Given(action, any) =>
        any.asInstanceOf[String].toUpperCase()
      case _When(action, any) =>
        any.asInstanceOf[String].toUpperCase() + "!"
      case _Then(action, any) =>
        println(s"Assert:${any.asInstanceOf[String]}")
        any
    }
  }


  Given("A message", "hello DSL world")
    .When("I put in upper case")
    .Then("The result should be showed")
    .runScenario


}
