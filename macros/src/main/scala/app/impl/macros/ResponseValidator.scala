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
object ResponseValidator {

  import scala.language.experimental.macros

  def check(any: Int): Int = macro checkTypeImpl

  def checkIntType(any: Any): Any = macro checkIntTypeImpl


  def checkIntTypeImpl(c: blackbox.Context)(any: c.Tree): c.Tree = {
    if (any.tpe.toString == "Int") {
      any
    } else {
      c.abort(c.enclosingPosition, s"Error wrong type:${any.tpe.toString}")
    }
  }

  def checkTypeImpl(c: blackbox.Context)(any: c.Tree): c.Tree = {
    import c.universe._
    any match {
      //      case _tree@Literal(Constant(s: Any)) if isValidAction(s) => _tree
      case _tree@Literal(Constant(s: Any)) if isValidAction(s) => c.abort(c.enclosingPosition, s"Type value $s")
//      case _ => c.abort(c.enclosingPosition, s"Error wrong type:${any.tpe.toString} 2:${any.tpe.toString == "String"}")
      case _ => c.abort(c.enclosingPosition, s"2:${any.tpe} ")

    }
  }

  def isValidAction(action: Any): Boolean = {
    action match {
      case c if c.isInstanceOf[Int] => true
      case _ => false
    }
  }


}
