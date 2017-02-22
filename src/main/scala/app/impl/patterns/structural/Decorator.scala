package app.impl.patterns.structural

/**
  * Decorator pattern give us the flexibility to have a class that accept a Type class which will use
  * to run the implementation once this decorator class is consumed.
  * Giving us the chance to have different behaviours depending the class that it´s decorating
  */
object Decorator extends App{

  trait Calc {
    def sum(x: Int, y: Int): Int

    def div(x: Int, y: Int): Int

    def multiply(x: Int, y: Int): Int
  }

  class CalcDecorator(calc: Calc) {

    def sum(x: Int, y: Int): Int = calc.sum(x, y)

    def div(x: Int, y: Int): Int = calc.div(x, y)

    def multiply(x: Int, y: Int): Int = calc.multiply(x, y)

  }

  class CalcInMillis extends Calc {
    override def sum(x: Int, y: Int): Int = x + y + 1000
    override def div(x: Int, y: Int): Int = x / y + 1000
    override def multiply(x: Int, y: Int): Int = x * y + 1000
  }

  class NormalCalc extends Calc {
    override def sum(x: Int, y: Int): Int = x + y
    override def div(x: Int, y: Int): Int = x / y
    override def multiply(x: Int, y: Int): Int = x * y
  }

  println("========Calc in millis================")
  printActions(new CalcInMillis)
  println("=========Normal calc===============")
  printActions(new NormalCalc)


  private def printActions(calc:Calc) = {
    def decorator = new CalcDecorator(calc)
    println(decorator.sum(1, 2))
    println(decorator.div(10, 2))
    println(decorator.multiply(5, 2))
  }


}
