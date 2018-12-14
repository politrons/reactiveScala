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
    println(A(null).value)



    val map:Map[Any,String] = Map(A("1") -> "works",A("2") -> "works_again" )
    println(map.get(A("1")))
    println(map.get(A("2")))
    println(map.get(B("2")))


    val list:List[A] = List(A("hello"), A("world"))

    println(s"list:${list.contains(A("hello"))}")
    println(s"list:${list.contains(A("Hello"))}")
    println(s"list:${list.contains(A("foo"))}")



    //    val queueValue = queue.poll()
//    println(queueValue)
//    val value = A(queueValue).value
//    println(value)
//    println(value == null)


    var lifoList:List[String] = List()

    lifoList = lifoList :+ "1"
    lifoList = lifoList :+ "2"
    lifoList = lifoList :+ "3"
    lifoList = lifoList :+ "4"
    lifoList = lifoList :+ "5"
    lifoList = lifoList :+ "6"
    lifoList = lifoList :+ "7"
    lifoList = lifoList :+ "8"
    lifoList = lifoList :+ "9"

    println(lifoList.last)
    lifoList = lifoList.dropRight(1)

  }

}



