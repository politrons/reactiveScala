package step3

object FizzBuzzStep3 {

  /**
    * Function to update the map counter of types.
    */
  private lazy val increaseCounterFunc: (Map[String, Int], String) => Map[String, Int] =
    (elements, key) => elements.updated(key, elements.getOrElse(key, 0) + 1)

  def apply(_from: Int, _to: Int): Map[String, Int] = {
    (_from to _to)
      .foldLeft(Map[String, Int]())((elements, number) => {
        number match {
          case num if num.toString.toCharArray.contains(51) => increaseCounterFunc(elements, "lucky")
          case num if num % 15 == 0 => increaseCounterFunc(elements, "fizzbuzz")
          case num if num % 5 == 0 => increaseCounterFunc(elements, "buzz")
          case num if num % 3 == 0 => increaseCounterFunc(elements, "fizz")
          case _ => increaseCounterFunc(elements, "integer")
        }
      })
  }


}
