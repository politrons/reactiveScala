package app.impl.di

/**
  * Created by pabloperezgarcia on 16/06/2017.
  */
class Conf {

  def init(): Unit = {

    implicit val serviceCalculator: Calculator = new ServiceCalculator

  }

}
