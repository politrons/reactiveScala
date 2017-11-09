package app.impl.scalaz

import org.junit.Test

/**
  * Created by pabloperezgarcia on 09/11/2017.
  */
class TraverseFeature {

  val pf: Int => Option[Int] = PartialFunction.condOpt(_) { case 3 => 30; case 4 => 40 }


  @Test
  def main(): Unit = {
//    List(1, 2, 3, 4).traverse(pf)
//    List(3, 4).traverse(pf)
  }


}
