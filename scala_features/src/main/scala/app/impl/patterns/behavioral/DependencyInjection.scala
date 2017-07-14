package app.impl.patterns.behavioral

/**
  * Self Reference is a mechanism to create dependencies between class
  * using operator foo:Class => inside your class automatically you have access to all components of the other class
  * just like if you were extending that class. Then, if someone create an instance of that class,
  * must extends {A} and {B} otherwise the class wont compile
  */
object DependencyInjection extends App{

  class AB extends B with A{}//If you remove with A the code wont compile

  trait A {
    def name = "Paul"
  }

  trait B {
    a: A => //This line is our self reference to determine that if We have B we must have A as well
    def print() = println(s"Hello $name")
  }

  new AB().print()

}
