package app.impl.patterns.behavioral

/**
  * Strategy pattern it´s similar to decorator patter with the different that the Strategy class
  * it´s not an extension of the wrapper class but a isolate behave.
  *
  * Here Strategy it´s not an instance but a function that receive two values and return 1
  * So the strategy class basically trigger the function
  */
object Strategy extends App{

  type Strategy = (Int, Int) => Int

  case class Context(computer: Strategy) {
    def use(a: Int, b: Int): Int = {
      computer.apply(a, b)
    }
  }

  val add: Strategy = _ + _
  val multiply: Strategy = _ * _


  println(Context(add).use(2, 3))
  println(Context(multiply).use(2, 3))


}
