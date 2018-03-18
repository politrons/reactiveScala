package app.impl.scala

import org.junit

class InferTypes {


  case class Test(name: String)

  @junit.Test
  def main(): Unit = {
    inferTypes(1, 2, 3)
    inferTypes(1L, 2L, 3L)
    inferTypes[String, String, String]("bla", "foo", "boo") //The definition of types it´s optional
    inferTypes(Test("a"), Test("b"), Test("c"))
    inferTypes(1, "2", 3L)
  }

  def inferTypes[A, B, C](a: A, b: B, c: B): Unit = {
    println(a)
    println(b)
    println(c)
  }

}
