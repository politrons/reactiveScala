package types

object MonoidFeature extends App {


  /**
    * Monoid just define a constructor of type T which value can be initialized with empty operator,
    * and also compare, sum, div or whatever we want if the two elements are with the same type.
    * @tparam T of class
    */
  trait MonoidType[T] {

    def empty: T

    def compose(a: T, b: T): T

  }

  /**
    * Here using type classes we define an implementation of the Monoid.
    * Something similar as you would use with [Class] and [Instance] in Haskell.
    */
  implicit val monoidString = new MonoidType[String] {

    override def empty: String = ""

    override def compose(a: String, b: String): String = a + b
  }

  /**
    * As you can see here we define another implementation this time for Int type.
    */
  implicit val monoidInt = new MonoidType[Int] {

    override def empty: Int = 0

    override def compose(a: Int, b: Int): Int = a + b
  }

  /**
    * Now this is the time where all magic happens, and that is thanks to scala implicits. Something that in Haskell
    * is natural in the language.
    * Here since we have the implicit monoidType for type T it will inference the type passed in the input value,
    * and it will automatically apply the function compose for that particular type.
    * As you can see, that's an incredible way to reuse code in your program, and even more important
    * extend the contract defined in the trait without have to obey your consumers to implement another interfaces.
    */
  def run[T](list: List[T])(implicit monoidType: MonoidType[T]): List[T] = {
    list.flatMap(value => list.map(value1 => monoidType.compose(value, value1)))
  }

  println(run(List(1, 2, 3, 4, 5)))
  println(run(List("1,2,3,4,5")))
  //  run(List(1L)) it will fail in runtime since it cannot find an implicit for that particular type.

}
