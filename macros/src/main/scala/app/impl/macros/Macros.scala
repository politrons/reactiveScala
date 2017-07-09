package app.impl.macros

import scala.reflect.macros.blackbox
import scala.language.experimental.macros

/**
  * Created by pabloperezgarcia on 09/07/2017.
  */

object Macros {

  def hello(message:String): Unit = macro helloImpl

  def helloImpl(c: blackbox.Context)(message: c.Expr[String]): c.Expr[Unit] = {
    import c.universe._
//    println(showRaw(q"""println("hello " + ${message.tree} + "!")"""))
    c.Expr(q"""println("hello " + ${message.tree} + "!")""")
  }
}

//Error:(12, 7) macro definition needs to be enabled
//by making the implicit value scala.language.experimental.macros visible.
//This can be achieved by adding the import clause 'import scala.language.experimental.macros'
//or by setting the compiler option -language:experimental.macros.
//See the Scaladoc for value scala.language.experimental.macros for a discussion
//why the feature needs to be explicitly enabled.
//def hello: Unit = macro helloImpl