package app.impl.patterns.behavioral

/**
  * In command patter we pass a Command which can be just a "parameter of type" which in
  * Scala it´s used by arg: => Type specifying that you´e expecting that param type.
  */
object Command extends App {

  //======== First pattern ========\\
  object Invoker {

    def invoke(command: => Unit) {
      // by-name parameter
      command
    }
  }

  Invoker.invoke(println("foo"))
  Invoker.invoke {
    println("bar 1")
    println("bar 2")
  }

  //======== Second pattern ========\\

  type Command = (String) => Unit

  object Runner {

    def run(command: Command, value:String) {
      // by-name parameter
      command.apply(value)
    }
  }

  def upperCase: Command = s => println(s.toUpperCase)

  def lowerCase: Command = s => println(s.toLowerCase)


  Runner.run(upperCase, "hello world")
  Runner.run(lowerCase, "HELLO WORLD")


}
