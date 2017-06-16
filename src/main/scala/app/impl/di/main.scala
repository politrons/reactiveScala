package app.impl.di

import app.impl.di.Conf.serviceCalculator
import org.junit.Test

/**
  * Created by pabloperezgarcia on 16/06/2017.
  */
class main {

  @Test def sumTest(): Unit = {
    println(calcSum(10, 20))
  }

  def calcSum(a: Int, b: Int)(implicit serviceCalculator: Calculator): Int = {
    serviceCalculator.sum(a, b)
  }

}
