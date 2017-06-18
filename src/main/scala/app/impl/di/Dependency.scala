package app.impl.di
/**
  * Created by pabloperezgarcia on 16/06/2017.
  */
class Dependency()(implicit serviceCalculator: Calculator) {

  println(calcSum(10, 20))

  def calcSum(a: Int, b: Int): Int = {
    serviceCalculator.sum(a, b)
  }

}
