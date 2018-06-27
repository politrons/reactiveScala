package app.impl.scala

import org.junit.Test

import scala.reflect.runtime.universe._

class TreeMacro {

  @Test
  def main(): Unit = {
    val tree = Apply(Select(Ident(TermName("x")), TermName("$plus")), List(Literal(Constant(2))))
    val str: String = show(tree)
    println(str)
  }

  /**
    * Create new elements in compilation time.
    */
  @Test
  def treeWithClass(): Unit = {
    val expr = reify { class Flower { def name = "Rose" } }
    println(showRaw(expr))
  }




}
