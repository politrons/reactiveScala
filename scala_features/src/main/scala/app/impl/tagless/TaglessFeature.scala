package app.impl.tagless

import org.junit.Test

class TaglessFeature {

  // ################
  // #   Algebras   #
  // ################
  /**
    * Here we define the Algebras or actions that our DSL will be able to use.
    * This Algebras are meant to be used only by the bridges which you can see in
    * the next section, which are the code that the consumer will use as DSL
    */
  trait MyDSL[Action[_]] {

    def number(v: Int): Action[Int]

    def increment(a: Action[Int]): Action[Int]

    def add(a: Action[Int], b: Action[Int]): Action[Int]

    def text(v: String): Action[String]

    def toUpper(a: Action[String]): Action[String]

    def concat(a: Action[String], b: Action[String]): Action[String]

    def toString(v: Action[Int]): Action[String]

  }

  // ################
  // #   Bridges    #
  // ################
  /**
    * Bridges aka DSL is the Domain specific Language that our consumers will use.
    * Bridges use Scala Types class pattern to be a bridge between the value passed and the implementation defined in the Algebra.
    * As Type class patter provide, depending the type passed as the implicit the implementation can be one or another.
    * The implementation it will ve provided by the interpreters in the code below.
    */
  trait MyBridge[ScalaValue] {
    def apply[Action[_]](implicit L: MyDSL[Action]): Action[ScalaValue]
  }

  def buildNumber(number: Int) = new MyBridge[Int] {
    override def apply[Action[_]](implicit interpreter: MyDSL[Action]): Action[Int] = interpreter.number(number)
  }

  def buildIncrementNumber(number: Int) = new MyBridge[Int] {
    override def apply[Action[_]](implicit interpreter: MyDSL[Action]): Action[Int] = interpreter.increment(interpreter.number(number))
  }

  def buildIncrementExpression(expression: MyBridge[Int]) = new MyBridge[Int] {
    override def apply[Action[_]](implicit interpreter: MyDSL[Action]): Action[Int] = interpreter.increment(expression.apply)
  }

  def buildComplexExpression(text: String, a: Int, b: Int) = new MyBridge[String] {
    override def apply[Action[_]](implicit interpreter: MyDSL[Action]): Action[String] = {
      val addition = interpreter.add(interpreter.number(a), interpreter.increment(interpreter.number(b)))
      interpreter.concat(interpreter.toUpper(interpreter.text(text)), interpreter.toString(addition))
    }
  }

  //
  // ####################
  // #   Interpreters   #
  // ####################
  /**
    * The interpreter implement the DSL/Algebras to give a implementation with the values that we receive.
    * As you can see here one of the biggest benefits of use Tagless or Free are that we can have multiples
    * implementations which are really easy to plug and play as you can see in the execution of the examples.
    */
  type Id[ScalaValue] = ScalaValue

  val interpret = new MyDSL[Id] {

    override def number(v: Int): Id[Int] = v

    override def increment(a: Id[Int]): Id[Int] = a + 1

    override def add(a: Id[Int], b: Id[Int]): Id[Int] = a + b

    override def text(v: String): Id[String] = v

    override def toUpper(a: Id[String]): Id[String] = a.toUpperCase

    override def concat(a: Id[String], b: Id[String]): Id[String] = a + " " + b

    override def toString(v: Id[Int]): Id[String] = v.toString
  }

  type PrettyPrint[ScalaValue] = String

  val interpretAsPrint = new MyDSL[PrettyPrint] {
    override def number(v: Int): PrettyPrint[Int] = s"($v)"

    override def increment(a: PrettyPrint[Int]): PrettyPrint[Int] = s"(inc $a)"

    override def add(a: PrettyPrint[Int], b: PrettyPrint[Int]): PrettyPrint[Int] = s"(+ $a $b)"

    override def text(v: String): PrettyPrint[String] = s"[$v]"

    override def toUpper(a: PrettyPrint[String]): PrettyPrint[String] = s"(toUpper $a)"

    override def concat(a: PrettyPrint[String], b: PrettyPrint[String]): PrettyPrint[String] = s"(concat $a $b)"

    override def toString(v: PrettyPrint[Int]): PrettyPrint[String] = s"(toString $v)"
  }

  type Nested[ScalaValue] = MyBridge[ScalaValue]

  val nestedInterpreter = new MyDSL[Nested] {
    var nesting = 0

    override def number(v: Int): Nested[Int] = new MyBridge[Int] {
      override def apply[Wrapper[_]](implicit L: MyDSL[Wrapper]): Wrapper[Int] = {
        if (nesting > 0) {
          val temp = nesting
          nesting = 0
          L.add(L.number(temp), L.number(v))
        } else {
          L.number(v)
        }
      }
    }

    override def increment(a: MyBridge[Int]): Nested[Int] = new MyBridge[Int] {
      override def apply[Wrapper[_]](implicit L: MyDSL[Wrapper]): Wrapper[Int] = {
        nesting = nesting + 1
        a.apply(L)
      }
    }

    override def add(a: MyBridge[Int], b: MyBridge[Int]): Nested[Int] = new MyBridge[Int] {
      override def apply[Wrapper[_]](implicit L: MyDSL[Wrapper]): Wrapper[Int] = {
        if (nesting > 0) {
          val temp = nesting
          nesting = 0
          L.add(L.number(temp), L.add(a.apply(L), b.apply(L)))
        } else {
          L.add(a.apply(L), b.apply(L))
        }
      }
    }

    override def text(v: String): Nested[String] = ???

    override def toUpper(a: Nested[String]): Nested[String] = ???

    override def concat(a: Nested[String], b: Nested[String]): Nested[String] = ???

    override def toString(v: Nested[Int]): Nested[String] = ???
  }


  @Test def mainInterpreter(): Unit = {
    println(buildNumber(1).apply(interpret))
    println(buildIncrementNumber(1).apply(interpret))
    println(buildIncrementExpression(buildNumber(1)).apply(interpret))
    println(buildComplexExpression("Hello", 10, 20).apply(interpret))
  }

  @Test def mainInterpreterAsPrint(): Unit = {
    println(buildNumber(1).apply(interpretAsPrint))
    println(buildIncrementNumber(1).apply(interpretAsPrint))
    println(buildIncrementExpression(buildNumber(1)).apply(interpretAsPrint))
    println(buildComplexExpression("Hello", 10, 20).apply(interpretAsPrint))
  }

  @Test def mainInterpreterNested(): Unit = {

    println(buildNumber(1).apply(nestedInterpreter).apply(interpretAsPrint))


  }

}
