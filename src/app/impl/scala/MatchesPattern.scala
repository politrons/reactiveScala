package app.impl.scala

import org.junit.Test

/**
  * Matches patter allow you to avoid all the verbose if/else structure, using an elegant switch style.
  */
class MatchesPattern  {


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



