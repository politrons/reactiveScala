package app.impl.macros

import scala.language.experimental.macros
import scala.reflect.macros.blackbox

/**
  * Created by pabloperezgarcia on 09/07/2017.
  *
  * With Macros we can create functions that they will be invoke in compilation-time.
  * This is achieve thanks that we use the operator 'macro' before the invocation of the function.
  * Because of that the compiler know that he have to check that function in compilation time.
  *
  * Thanks to macros you have a new world of possibilities when you´e designing a DSL or any other code in your
  * project that require compilation and not runtime errors.
  *
  * First thing to be aware of is the Context, in this case blackbox, which is the responsible
  * to interact with the compiler, and do actions as abort, error or warning.
  *
  * context.Tree wraps an abstract syntax tree and tags it with its type in this case String.
  *
  * Then using this tree we can use pattern matching to get the invocation value, using Literal and
  * specifying the type through Constant.
  *
  * Once that we have the value it´s up to us to the business logic that we want, and then allow the continue
  * of the compilation returning the tree, or abort the compilation or throw an error, or just an echo or warning.
  *
  */
object :: {

  import scala.language.experimental.macros

  def ->(action: String): String = macro checkActionImpl

  def checkActionImpl(c: blackbox.Context)(action: c.Tree): c.Tree = {
    import c.universe._

    action match {
      case _tree@Literal(Constant(s: String)) if isValidAction(s) => _tree
      case _tree@Literal(Constant(s: String)) => c.abort(c.enclosingPosition, getErrorMessage(s))
    }
  }

  val PAYLOAD_STATUS = "(empty|non_empty)"
  val RESPONSE_CODE = "([2-5][0-10][0-10])"
  val VERSION = "([0-2].0)"
  val PARAMS = "(.*)=(.*)"
  val PARAM = "(.*)"
  val PAYLOAD_VALUE = s"^Payload".r
  val PAYLOAD_VALUE_REGEX = s"$PAYLOAD_VALUE $PARAMS".r
  val ADD = s"add '$PARAM'".r
  val MESSAGE = s"A message with version".r
  val MESSAGE_WRONG = s"$MESSAGE $PARAM".r
  val MESSAGE_GOOD = s"$MESSAGE $VERSION".r
  val MULTIPLY = s"multiply by '$PARAM'".r
  val HIGHER_THAN = s"The result should be higher than '$PARAM'".r

  def isValidAction(action: String): Boolean = {
    action match {
      case PAYLOAD_VALUE_REGEX(c, c1) => true
      case "Make a request to server" => true
      case "Giving a number" => true
      case MULTIPLY(value) => true
      case ADD(value) => true
      case HIGHER_THAN(value) => true
      case MESSAGE_GOOD(version) => true
      case _ => false
    }
  }

  def getErrorMessage(action: String): String = {
    action match {
      case PAYLOAD_VALUE(value) => s"The value of payload ($value)is wrong"
      case MESSAGE_WRONG(code) => s"The message has a wrong version ($code), only 1.0 and 2.0 allowed"
        case _ => "Invalid action for Test framework DSL. Check the allowed actions in RegexActions"
    }
  }

}
