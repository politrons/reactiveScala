/**
  * Created by pabloperezgarcia on 6/7/16.
  */
package object implicitUtils {

  implicit class StringImprovements(s: String) {
    def increment = s.map(c => (c + 1).toChar)
  }

  implicit class IntegerImprovements(i: Int) {
    def exponential = i * i

    def increment(n: Int) = i + n

    def decrement(n: Int) = i - n

    def multiply(n: Int) = i * n

  }

}
