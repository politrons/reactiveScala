package app.impl.haskell

import java.util.Calendar._

import app.impl.haskell.ScalaHaskell.{Name, Time, Word}
import org.junit.Test
import scalaz.ioeffect.{IO, RTS}


/**
  * Just like using the Monad IO of Haskell, here we use the IO Monad for ScalaZ.
  * What really make this code pure like in Haskell is, that is not eager evaluation but lazy,
  * so just like in Haskell, if we use composition, those functions in the IO wont
  * be executed until we finish our program and we use [unsafePerformIO] which means
  * we can compose those pure functions in our program knowing that those will provide
  * same result once are executed.
  */
class ScalaHaskell extends RTS {

  /**
    * One of the best ways to emulate Haskell from Scala is using for comprehension which is
    * just like [do block] a sugar syntax to make sequential composition of functions.
    */
  @Test
  def doBlockWithIO(): Unit = {
    val io = for {
      result <- concatTwoWords(Word("hello"))(Word("world"))
      result <- appendValue(result)
      result <- transformToUpperCase(result)
    } yield result

    println(unsafePerformIO(io))

  }

  /**
    * Here we can see an example of pure referential transparency where our
    * function receive Word => Word and we always return IO[Throwable, Word]
    * of that value once is evaluated.
    */
  def concatTwoWords: Word => Word => IO[Throwable, Word] =
    word => word1 => IO.point(Word(s"${word.value} - ${word1.value}"))

  def appendValue: Word => IO[Throwable, Word] =
    word => IO.point(Word(word.value.concat("!!!!")))

  def transformToUpperCase: Word => IO[Throwable, Word] =
    word => IO.point(Word(word.value.toUpperCase))


  /**
    * In this example we show how using IO monad like in Haskell we can have pure Functional programing
    * using Referential transparency, we are doing composition of this three functions, and it does not
    * matter how many times we compose those functions, since are lazy evaluated we know that function
    * will give same result in time for example in here.
    * We create the IO and even waiting 2 seconds before evaluate the io and go through pure to impure
    * we see the time it never was set in place when we were composing the functions.
    */
  @Test
  def lazyEvaluation(): Unit = {
    val io = for {
      time <- pureTimeFunction(Time(""))
      time <- pureTimeFunction(time)
      time <- pureTimeFunction(time)
    } yield time
    val now = getInstance()
    println(s"Time before we start the evaluation: ${now.get(MINUTE)} - ${now.get(SECOND)}")
    Thread.sleep(2000)
    println(unsafePerformIO(io))
  }

  def pureTimeFunction: Time => IO[Throwable, Time] =
    time => IO.point {
      val now = getInstance()
      Time(s"${time.value} : ${now.get(MINUTE)} - ${now.get(SECOND)}")
    }

  /**
    * Scala provide in all their Monads very good operators to transform(map), compose(flatMap, zip) and filter
    * So for this particular example we could use [filter] of List to find the name in the list, but what
    * we want to show here is how just like Haskell Scala use [tail recursion] a very efficient way
    * to do recursion without create a stack per recursion which it can end up in a StackOverFlow
    * The patter matching of Scala is an implementation of how Haskell function can define internally this
    * match so in here we can reduce the list giving us one value and the rest of the list like in Haskell we do with
    *
    * sumAllPrices :: Double -> [Item] -> Double
    * sumAllPrices totalPrice (item:items) = sumAllPrices (totalPrice + (price item)) items
    * sumAllPrices totalPrice [] = totalPrice -- Last condition. When the list is empty we break the recursion
    */
  @Test
  def recursion(): Unit = {
    val io = for {
      name <- searchName(Name("Politrons"))(List(Name("Paul"), Name("Lee"), Name("Politrons"), Name("Esther")))
      name <- nameToUpperCase(name)
    } yield name
    println(unsafePerformIO(io))
  }

  def searchName: Name => List[Name] => IO[Throwable, Name] =
    name => {
      case listName :: otherNames => name.value == listName.value match {
        case true => IO.point(listName)
        case false => searchName(name)(otherNames)
      }
      case List() => IO.point(Name("Name not found"))
    }

  def nameToUpperCase: Name => IO[Throwable, Name] =
    name => IO.point(name.copy(value = name.value.toUpperCase))

  /**
    * Thanks to [implicit] we can make the compiler once he infer a type go to
    * one implicit implementation for that class that implement a trait or another,
    * just like Haskell does. Here unfortunately it's quite more verbose than in
    * Haskell but still is doable use this amazing feature.
    */
  @Test
  def typeClasses(): Unit = {
    println(processValue("hello world"))
    println(processValue(1981))

  }

  def processValue[T](value: T)(implicit myClass: MyClass[T]): T = {
    myClass.process(value)
  }

  /**
    * Like in Haskell we define the class, here [trait] with generic type and the
    * function process which receive a T and return a T
    * Then we define the instances, here implicit anonymous implementations of MyClass[T]
    * and in that implementation just like in Haskell we specify the type for that class.
    *
    * Haskell type class example:
    * -- | Here we just define the class with the structure as a contract.
    * class ArithmeticTypeClass _type where
    * customSum :: _type -> _type -> _type
    *
    * -- |Here we define the implementation for type Integer.
    * instance ArithmeticTypeClass Integer where
    * customSum i1 i2 = i1  + i2
    */
  trait MyClass[T] {
    def process: T => T
  }

  /**
    * Implementation for String type of type class [MyClass]
    */
  implicit val stringValue: MyClass[String] = new MyClass[String] {
    override def process: String => String = value => value.toUpperCase + "!!!"
  }

  /**
    * Implementation for Int type of type class [MyClass]
    */
  implicit val intValue: MyClass[Int] = new MyClass[Int] {
    override def process: Int => Int = value => value * 1000
  }

}

object ScalaHaskell {

  case class Word(value: String) extends AnyVal

  case class Time(value: String) extends AnyVal

  case class Name(value: String) extends AnyVal


}
