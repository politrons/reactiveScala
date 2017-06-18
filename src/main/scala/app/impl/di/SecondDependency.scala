package app.impl.di

/**
  * Created by pabloperezgarcia on 16/06/2017.
  */
class SecondDependency()(implicit calculator: DivCalculator) {

  def calcDiv(a: Int, b: Int): Int = {
    calculator.div(a, b)
  }

}
