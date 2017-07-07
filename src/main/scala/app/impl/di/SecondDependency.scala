package app.impl.di

/**
  * Created by pabloperezgarcia on 16/06/2017.
  */
class SecondDependency()(implicit divCalculator: DivCalculator) {

  def calcDiv(a: Int, b: Int): Int = {
    divCalculator.div(a, b)
  }

}
