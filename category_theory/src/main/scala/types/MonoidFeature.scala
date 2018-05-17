package types

object MonoidFeature extends App {


  trait MonoidType[T] {
    def empty: T

    def compose(a: T, b: T): T
  }

  implicit val monoidString = new MonoidType[String] {

    override def empty: String = ""

    override def compose(a: String, b: String): String = a + b
  }

  implicit val monoidInt = new MonoidType[Int] {

    override def empty: Int = 0

    override def compose(a: Int, b: Int): Int = a + b
  }

  println(run(List(1, 2, 3, 4, 5)))
  println(run(List("1,2,3,4,5")))
  //  run(List(1L)) it will fail in runtime.

  def run[T](list: List[T])(implicit monoidType: MonoidType[T]): List[T] = {
    list.flatMap(value => list
      .map(value1 => monoidType.compose(value, value1)))
  }

}
