package app.impl.patterns.creational

import org.junit.Test

/**
  * Created by pabloperezgarcia on 27/10/2017.
  */
class CakePattern {

  trait FooAbleComponent {
    val fooAble: FooAble

    class FooAble {
      def foo() = "here is your foo"
    }

  }

  trait BazAbleComponent {
    val bazAble: BazAble

    class BazAble {
      def baz() = "baz too"
    }

  }

  class BarUsingFooAble {
    this: FooAbleComponent with BazAbleComponent =>
    def bar() = s"bar calls foo: ${fooAble.foo()} and baz: ${bazAble.baz()}"
  }

  @Test
  def mainTest() {
    val barWithFoo = new BarUsingFooAble with FooAbleComponent with BazAbleComponent {
      val bazAble = new BazAble()
      //or any other implementation
      val fooAble = new FooAble() //or any other implementation
    }
    println(barWithFoo.bar())
  }

}
