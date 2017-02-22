package app.impl

import org.junit.Test

/**
  * Created by pabloperezgarcia on 01/12/2016.
  */
abstract class Utils {


  abstract class Element {
    def contents: Array[String]
  }
  def elem(s: String): Element

  @Test
  def combination(): Unit ={

  }

  @Test
  def testBoom(): Unit ={
    boom(3)
  }

  def  boom(x: Int): Int =
    if (x == 0) throw new Exception("boom!")
    else boom(x - 1) + 1
}
