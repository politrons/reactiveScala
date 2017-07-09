package app.impl.macros

import scala.language.experimental.macros
import scala.reflect.macros.blackbox

/**
  * Created by pabloperezgarcia on 09/07/2017.
  */
object DSLValidator {

  import scala.language.experimental.macros

  def Given(message: String): Any = macro checkActionImpl
  def When(message: String): Any = macro checkActionImpl
  def Then(message: String): Any = macro checkActionImpl
  def And(message: String): Any = macro checkActionImpl


  def checkActionImpl(c: blackbox.Context)(message: c.Expr[String]): c.Tree = {
    import c.universe._
    def isValidAction(s: String): Boolean = checkMessage(s)

    message.tree match {
      case t@Literal(Constant(s: String)) if isValidAction(s) => t
      case _ => c.abort(c.enclosingPosition, "Invalid action for DSL. Check the allowed actions in RegexActions")
    }
  }

  val PARAMS = "(.*)=(.*)"
  val PAYLOAD_VALUE_REGEX = s"^Payload $PARAMS".r

  def checkMessage(action: String): Boolean = {
    action match {
      case PAYLOAD_VALUE_REGEX(c, c1) => true
      case "Make a request to F2E" => true
      case _ => false
    }
  }


}