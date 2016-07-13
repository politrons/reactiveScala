package app.impl.scala

import org.junit.Test

/**
  * Matches patter allow you to avoid all the verbose if/else structure, using an elegant switch style.
  */
class MatchesPattern {


  @Test def matches(): Unit = {
    println(matchTest(1))
    println(matchTest("test"))
    println(matchTest(2))
  }

  @Test def matchesOnPractice(): Unit = {
    val list = List[Any](1, 2, "test", 5)
    val newList = list.toStream
      .map(i => matchTest(i))
      .toList
    println(newList)
  }

  @Test def matchesOnList(): Unit = {
    val list = List[Any](1, 2, "test", 5)
    list.foreach {
      case 1 => println("one")
      case 2 => println("two")
      case _ => println("?")
    }
  }

  @Test def matchesOnMap(): Unit = {
    Map(1->1, 2->"2", 3->3).foreach{ entry =>
      entry._1 match {
        case 1 => println(s"Map value:${entry._2.asInstanceOf[Integer]*100}")
        case 2 => println(s"Map value:${entry._2.asInstanceOf[String].toUpperCase()}")
        case 3 => println(s"Map value:${entry._2.asInstanceOf[Integer] * 100}")
        case _ => println("???")
      }
    }
  }

  def matchTest(x: Any): Any = x match {
    case 1 => {
      "one".toUpperCase
    }
    case 2 => 2
    case "test" => {
      "TEST".toLowerCase
    }
    case _ => "many"
  }

}



