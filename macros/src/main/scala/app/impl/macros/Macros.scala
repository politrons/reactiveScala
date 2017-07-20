package app.impl.macros

import scala.language.experimental.macros
import scala.reflect.macros.blackbox

/**
  * Created by pabloperezgarcia on 09/07/2017.
  */

object Macros {

  def hello(message: String): Unit = macro helloImpl

  def helloImpl(c: blackbox.Context)(message: c.Expr[String]): c.Expr[Unit] = {
    import c.universe._
    c.Expr(q"""println("hello " + ${message.tree} + "!")""")
  }

  def printparam(param: Any): Unit = macro printparam_impl

  def printparam_impl(c: blackbox.Context)(param: c.Expr[Any]): c.Expr[Unit] = {
    import c.universe._
    reify { println(param.splice) }
  }

  def debug(param: Any): Unit = macro debug_impl

  def debug_impl(c: blackbox.Context)(param: c.Expr[Any]): c.Expr[Unit] = {
    import c.universe._
    val paramRep = show(param.tree)
    val paramRepTree = Literal(Constant(paramRep))
    val paramRepExpr = c.Expr[String](paramRepTree)
    reify { println(paramRepExpr.splice + " = " + param.splice) }
  }

}
