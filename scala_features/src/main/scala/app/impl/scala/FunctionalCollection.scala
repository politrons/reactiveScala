package app.impl.scala

/**
  * I have seen far by standing on the shoulders of giants.
  * This whole exercise was based on the RockJVM youtube post https://www.youtube.com/watch?v=Y5rPHZaUakg&t=487s
  */
object FunctionalCollection extends App {


  /**
    * The whole idea behind this pattern is to use predicate functions recursively stored in each new immutable instance
    * of [Collection] then when we invoke [has] we recursively invoke all those functions.
    */
  trait Collection[T] {

    /**
      * Just apply the function passed into the constructor to check if the element is part of the instance or any previous
      * instance created.
      */
    def has(value: T): Boolean

    /**
      * Each time we add a new element into the Collection, we create a new instance but, as part of the predicate function
      * we pass to the constructor, we also include the previous one we had in the current instance.
      * So then once someone will use this function in [has] function, it will apply a recursive invocation of the predicate function of all
      * previous instance created.
      */
    def add(value: T): Collection[T]

    /**
      * In this case what the implementation must do, is to add a predicate function that make the negation of a maybe add predicate function
      * in case was added before.
      */
    def remove(value: T): Collection[T]
  }

  /**
    * Factory companion object to create MyCollection
    */
  object Collection {

    /**
      * We use same [add] function in a static way to create the first instance of [GenericCollection] where
      * as the most important part, we have to specify the function, we will use to check if an element
      * in the collection is there or not.
      */
    def add[T](value: T): GenericCollection[T] = {
      GenericCollection(x => x == value)

    }

    /**
      * We use same [add] function in a static way to create the first instance of [StringCollection] where
      * as the most important part, we have to specify the function, we will use to check if an element
      * in the collection is there or not and also if is in upperCase, unless is a whitespace.
      */
    def add(value: String): StringCollection = {
      StringCollection(x => value == x && x.toCharArray.count(c => c.isUpper || c.isWhitespace) == value.length)
    }
  }

  /**
    * Class that contains a function in constructor to make recursion invocation of previous
    * instances of the previous class instance to check if the element exist.
    */
  case class GenericCollection[T](property: T => Boolean) extends Collection[T] {

    def has(value: T): Boolean = property(value)

    def add(value: T): GenericCollection[T] = GenericCollection[T](e => property(e) || e == value)

    def remove(value: T): GenericCollection[T] = GenericCollection[T](e => property(e) && e != value)
  }

  /**
    * Class with String implementation that contains a function in constructor to make recursion invocation of previous
    * instances of the previous class instance to check if the element exist and is in upperCase.
    */
  case class StringCollection(property: String => Boolean) extends Collection[String] {

    override def has(value: String): Boolean = property(value)

    override def add(value: String): Collection[String] =
      StringCollection(x => property(x) ||
        (value == x && x.toCharArray.count(c => c.isUpper || c.isWhitespace) == value.length))

    override def remove(value: String): Collection[String] =
      StringCollection(x => property(x) &&
        (value != x && x.toCharArray.count(c => c.isUpper || c.isWhitespace) != value.length))
  }

  /**
    * We create a collection with some elements (which are actually predicate functions in memory)
    */
  val collection: Collection[Int] = Collection add 1981 add 100 add 12 add 1 add 5

  println(s"Exist:${collection.has(1981)}")
  println(s"Exist:${collection.has(100)}")
  println(s"Exist:${collection.has(2000)}")
  println(s"Exist:${collection.has(5)}")
  println(s"Exist:${collection.has(1000)}")

  /**
    * We remove an element from the current collection
    */
  val newCollection: Collection[Int] = collection remove 1981

  println(s"Exist after being removed:${newCollection.has(1981)}")

  /**
    * Example for [StringCollection]
    */
  val stringCollections: Collection[String] = Collection add "HELLO WORLD" add "A" add "foo"

  println(s"Exist and is upper case: ${stringCollections.has("HELLO WORLD")}")
  println(s"Exist and is upper case: ${stringCollections.has("foo")}")
  println(s"Exist and is upper case: ${stringCollections.has("bla")}")
  println(s"Exist and is upper case: ${stringCollections.has("A")}")

  val newStringCollection = stringCollections remove "HELLO WORLD"

  println(s"Exist after being removed:${newStringCollection.has("HELLO WORLD")}")

}
