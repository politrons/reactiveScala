package app.impl.scalaz

import org.junit.Test

/**
  * Created by pabloperezgarcia on 21/10/2017.
  */
class MonadLaws {

  /**
    * The first monad law states that if we take a value number = 2, put it in a default context with return
    * and then feed it to a function by using List(number).flatMap(f), it’s the same as just taking
    * the value and applying the function to it f(number).
    *
    * If you have a box (monad) with a value in it and a function that takes the same type of value and returns the same
    * type of box, then flatMapping it on the box or just simply applying it to the value should yield the same result.
    */
  @Test
  def leftIdentity = {
    //Example1
    val hello = "hello folks"
    val function: Function[String, String] = a => a.toUpperCase()
    val maybeString = Some(hello).flatMap(value => Some(function(value)))
    val someString = Some(function(hello))
    println(maybeString)
    println(someString)
    println(maybeString == someString)

    //Example2
    val f: (Int => List[Int]) = x => List(x, -x)

    val number = 2
    val list2 = List(number).flatMap(f)

    assert(list2 == f(number))

    println(list2)
    println(f(number))

    val option1 = Option(30).flatMap(globalFunction1)
    println(option1)
    println(globalFunction1(30))

  }

  /**
    * The second law states that if we have a monadic value and we use flatMap to feed it to return,
    * the result is our original monadic value.
    *
    * If you have a box (monad) with a value in it and you have a function that takes the same type of
    * value and wraps it in the same kind of box untouched, then after flatMapping that function on your
    * box should not change it.
    */
  @Test
  def rightIdentity = {
    //Example1
    val hello = "hello"
    val someString = Some(hello)
    val maybeString = Some(hello).flatMap(Some(_))
    println(someString)
    println(maybeString)
    println(maybeString == someString)
    //Example2
    val list1 = List(2)
    val list2 = list1.flatMap(List(_))
    assert(list1 == list2)
    println(list1)
    println(list2)

  }

  /**
    * The final monad law says that when we have a chain of monadic function applications with flatMap,
    * it shouldn’t matter how they’re nested.
    *
    * If you have a box (monad) and a chain of functions that operates on it as the previous two did,
    * then it should not matter how you nest the flatMappings of those functions.
    */
  @Test
  def associativity = {
    //Example1
    val upperFunction: Function[String, Option[String]] = a => Some(a.toUpperCase)
    val concatFunction: Function[String, Option[String]] = a => Some(a.concat(" works!"))

    val maybeString = Some(upperFunction("This monad")).flatMap(value => concatFunction(value.get))
    val someMaybeString = Some(upperFunction("This monad").flatMap(value => concatFunction(value)))
    println(someMaybeString)
    println(maybeString)
    println(maybeString == someMaybeString)

    //Example2
    val f1: (Int => List[Int]) = x => List(x * 10)
    val f2: (Int => List[Int]) = x => List(x * -1)
    val m = List(1, 2, 3, 4, 5)
    val list1 = m.flatMap(f1).flatMap(f2)
    val list2 = m.flatMap(f1(_).flatMap(f2))

    assert(list1 == list2)

    println(list1)
    println(list2)

    val option1 = Option(30).flatMap(globalFunction1).flatMap(globalFunction2)
    val option2 = Option(30).flatMap(globalFunction1(_).flatMap(globalFunction2))

    println(option1)
    println(option2)

    assert(option1 == option2)
  }

  val globalFunction1: (Int => Option[Int]) = x => if (x < 10) None else Some(x * 2)
  val globalFunction2: (Int => Option[Int]) = x => if (x > 50) Some(x + 1) else None


}
