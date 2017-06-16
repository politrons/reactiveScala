package app.impl.di

/**
  * Created by pabloperezgarcia on 16/06/2017.
  */
trait Calculator {

  def sum(a: Int, b: Int): Int

}

class ServiceCalculator extends Calculator {

  override def sum(a: Int, b: Int): Int = a + b

}