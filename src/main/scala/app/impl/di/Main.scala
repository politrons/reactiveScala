package app.impl.di

import org.junit.Test

/**
  * Created by pabloperezgarcia on 16/06/2017.
  */
class Main() {

  implicit val serviceCalculator: Calculator = new ServiceCalculator

//  new Conf().init()

  @Test def sumTest(): Unit = {
    new Dependency()
  }

}
