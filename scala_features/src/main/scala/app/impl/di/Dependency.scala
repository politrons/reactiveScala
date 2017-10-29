package app.impl.di

/**
  * Created by pabloperezgarcia on 16/06/2017.
  */
class  Dependency()(implicit val sumCalculator: SumCalculator,
                   implicit val divCalculator: DivCalculator) {

  def calcDiv(a: Int, b: Int) = {
    new SecondDependency().calcDiv(a, b)
  }

  def calcSum(a: Int, b: Int): Int = {
    sumCalculator.sum(a, b)
  }

}
