package step2

object FizzBuzzStep2 {

  def apply(_from: Int, _to: Int): IndexedSeq[String] = {
    (_from to _to)
      .map {
        case number if number.toString.toCharArray.contains(51/*Ascii code*/) => "lucky"
        case number if number % 15 == 0 => "fizzbuzz"
        case number if number % 5 == 0 => "buzz"
        case number if number % 3 == 0 => "fizz"
        case number => number.toString
      }
  }

}
