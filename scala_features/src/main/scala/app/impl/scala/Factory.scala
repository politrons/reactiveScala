package app.impl.scala

import org.junit.Test


/**
  * In Scala does not exist static type, instead Scala use the object type, which by design it´s static
  * The normal pattern to use Factory class in Scala is use object combine with class
  * And use this object as Factories to return new instances.
  */
class Factory {

  @Test def createHelloClass() {
    Hello.create().print()
  }

  object Hello {
    def create(): Hello = {
      new Hello("Paul")
    }
  }

  class Hello(name: String) {
    def print() = println("Hello " + name)
  }
}



