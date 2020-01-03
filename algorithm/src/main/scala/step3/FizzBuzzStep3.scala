package step3

object FizzBuzzStep3 {

  def apply(_from: Int, _to: Int): Map[String, Int] = {
    (_from to _to)
      .foldLeft(Map[String, Int]())((elements, number) => {
        number match {
          case num if num.toString.toCharArray.contains(51 /*Ascii code*/) =>
            elements.updated("lucky", elements.getOrElse("lucky", 0) + 1)
          case num if num % 15 == 0 =>
            elements.updated("fizzbuzz", elements.getOrElse("fizzbuzz", 0) + 1)
          case num if num % 5 == 0 =>
            elements.updated("buzz", elements.getOrElse("buzz", 0) + 1)
          case num if num % 3 == 0 =>
            elements.updated("fizz", elements.getOrElse("fizz", 0) + 1)
          case _ =>
            elements.updated("integer", elements.getOrElse("integer", 0) + 1)
        }
      })
  }

}
