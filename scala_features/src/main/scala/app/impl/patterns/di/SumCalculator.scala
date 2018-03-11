package app.impl.patterns.di

/**
  * Created by pabloperezgarcia on 16/06/2017.
  */
trait SumCalculator {

  def sum(a: Int, b: Int): Int

}

class SumCalculatorImpl extends SumCalculator {

  override def sum(a: Int, b: Int): Int = a + b

}
