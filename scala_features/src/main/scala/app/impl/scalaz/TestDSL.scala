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

  case class _Action(action: String, any: Any) extends Action[Any]

  val PARAM = "(.*)"
  val ADD = s"add '$PARAM'".r
  val MULTIPLY = s"multiply by '$PARAM'".r
  val HIGHER_THAN = s"The result should be higher than '$PARAM'".r


  /**
    * A Free monad it´s kind like an Observable,
    * where we specify the Entry type Orders, and output type A
    */
  type ActionMonad[A] = Free[Action, A]

  def Given(action: String, any: Any): ActionMonad[Any] = liftF[Action, Any](_Action(action, any))

  implicit class customFree(free: Free[Action, Any]) {

    def When(action: String): ActionMonad[Any] = {
      free.flatMap(any => liftF[Action, Any](_Action(action, any)))
    }

    def Then(action: String): ActionMonad[Any] = {
      free.flatMap(any => liftF[Action, Any](_Action(action, any)))
    }

    def And(action: String): ActionMonad[Any] = {
      free.flatMap(any => liftF[Action, Any](_Action(action, any)))
    }

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
      case _Action(action, any) => processAction(action, any)
    }
  }

  private def processAction(action: String, any: Any): Any = {
    action match {
      case "Giving a number" => any
      case MULTIPLY(value) => any.asInstanceOf[Int] * value.asInstanceOf[String].toInt
      case ADD(value) => any.asInstanceOf[Int] + value.asInstanceOf[String].toInt
      case HIGHER_THAN(value) => assert(any.asInstanceOf[Int] > value.asInstanceOf[String].toInt); any
    }
  }

}
