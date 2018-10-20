package app.impl.scala

import java.util.PriorityQueue

import app.impl.scala.CaseClassAnyVal.{A, B}
import org.junit.Test


object CaseClassAnyVal{

  case class A(value:String) extends AnyVal

  case class B(value:String) extends AnyVal


}

class CaseClassAnyVal {

  var queue = new PriorityQueue[Long]()

  @Test def keyInMap() {
    val map:Map[Any,String] = Map(A("1") -> "works",A("2") -> "works_again" )
    println(map.get(A("1")))
    println(map.get(A("2")))
    println(map.get(B("2")))

//    val queueValue = queue.poll()
//    println(queueValue)
//    val value = A(queueValue).value
//    println(value)
//    println(value == null)

  }

}



