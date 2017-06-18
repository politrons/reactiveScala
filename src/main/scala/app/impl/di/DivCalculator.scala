package app.impl.di

/**
  * Created by pabloperezgarcia on 16/06/2017.
  */
trait DivCalculator {

  def div(a: Int, b: Int): Int

}

class DivCalculatorImpl extends DivCalculator {

  override def div(a: Int, b: Int): Int = a / b

}