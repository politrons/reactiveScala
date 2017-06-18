package app.impl.di

import org.junit.Test

/**
  * Created by pabloperezgarcia on 16/06/2017.
  */
class Main() {

  implicit val sumCalculator = new SumCalculatorImpl


  @Test def firstSum(): Unit = {
    println(new Dependency().calcSum(10,20))
  }

  @Test def secondSum(): Unit = {
    println(new Dependency().calcDiv(50, 10))
  }

}
