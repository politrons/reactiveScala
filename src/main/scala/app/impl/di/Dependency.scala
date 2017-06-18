package app.impl.di
/**
  * Created by pabloperezgarcia on 16/06/2017.
  */
class Dependency()(implicit calculator: SumCalculator) {

  implicit val divCalculator = new DivCalculatorImpl

  def calcDiv(a: Int, b: Int) ={
    new SecondDependency().calcDiv(a,b)
  }

  def calcSum(a: Int, b: Int): Int = {
    calculator.sum(a, b)
  }

}
