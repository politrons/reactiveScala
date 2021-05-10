package app.impl.scala

object FunctionalCollection extends App {


  /**
    * The whole idea behind this pattern is to use predicate functions recursively stored in each new immutable instance
    * of [GenericCollection] then when we invoke [has] we recursively invoke all those functions.
    */
  trait GenericCollection[T] {

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
    def add(value: T): GenericCollection[T]

    /**
      *
      */
    def remove(value: T): GenericCollection[T]
  }

  /**
    * Factory companion class to create MyCollection
    */
  object GenericCollection {

    /**
      * We use same [add] function in a static way to create the first instance of [MyCollection] where
      * as the most important part, we have to specify the function, we will use to check if an element
      * in the collection is there or not.
      */
    def add[T](value: T): MyCollection[T] = {
      MyCollection(x => x == value)

    }

    /**
      * We use same [add] function in a static way to create the first instance of [StringCollection] where
      * as the most important part, we have to specify the function, we will use to check if an element
      * in the collection is there or not and also if is in upperCase.
      */
    def add(value: String): StringCollection = {
      StringCollection(x => value == x && x.toCharArray.count(c => c.isUpper) == value.length)
    }
  }

  /**
    * Class that contains a function in constructor to make recursion invocation of previous
    * instances of the previous class instance to check if the element exist.
    */
  case class MyCollection[T](property: T => Boolean) extends GenericCollection[T] {

    def has(value: T): Boolean = property(value)

    def add(value: T): MyCollection[T] = MyCollection[T](e => property(e) || e == value)

    def remove(value: T): MyCollection[T] = MyCollection[T](e => property(e) && e != value)
  }

  /**
    * Class with String implementation that contains a function in constructor to make recursion invocation of previous
    * instances of the previous class instance to check if the element exist and is in upperCase.
    */
  case class StringCollection(property: String => Boolean) extends GenericCollection[String] {

    override def has(value: String): Boolean = property(value)

    override def add(value: String): GenericCollection[String] =
      StringCollection(x => property(x) ||
        (value == x && x.toCharArray.count(c => c.isUpper) == value.length))

    override def remove(value: String): GenericCollection[String] =
      StringCollection(x => property(x) &&
        (value != x && x.toCharArray.count(c => c.isUpper) != value.length))
  }

  override def main(args: Array[String]): Unit = {
    /**
      * We create a collection with some elements (which are actually predicate functions in memory)
      */
    val collection: MyCollection[Int] = GenericCollection add 1981 add 100 add 12 add 1 add 5

    println(s"Exist:${collection.has(1981)}")
    println(s"Exist:${collection.has(100)}")
    println(s"Exist:${collection.has(2000)}")
    println(s"Exist:${collection.has(5)}")
    println(s"Exist:${collection.has(1000)}")

    /**
      * We remove an element from the current collection
      */
    val newCollection: MyCollection[Int] = collection remove 1981

    println(s"Exist after being removed:${newCollection.has(1981)}")

    /**
      * Example for [StringCollection]
      */
    val stringCollections: GenericCollection[String] = GenericCollection add "HELLO" add "A" add "foo"

    println(s"Exist and is upper case: ${stringCollections.has("HELLO")}")
    println(s"Exist and is upper case: ${stringCollections.has("foo")}")
    println(s"Exist and is upper case: ${stringCollections.has("bla")}")
    println(s"Exist and is upper case: ${stringCollections.has("A")}")

    val newStringCollection = stringCollections remove "HELLO"

    println(s"Exist after being removed:${newStringCollection.has("HELLO")}")

  }


}
