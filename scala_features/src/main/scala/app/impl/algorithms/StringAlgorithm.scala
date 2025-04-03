package app.impl.algorithms

import org.junit.Test

class StringAlgorithm {


  @Test
  def countNumberOfWordsWithCamelCase(): Unit = {
    val sentence = "saveChangesInTheEditor"
    var count = 1
    sentence.toCharArray.foreach(c => {
      if (c.isUpper) {
        count += 1
      }
    })
    print(count)

  }

  //    numbers = "0123456789"
  //    lower_case = "abcdefghijklmnopqrstuvwxyz"
  //    upper_case = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
  //


  @Test
  def minimumNumber: Unit = {
    print(minimumNumber(11, "9"))
  }

  def minimumNumber(n: Int, password: String): Int = {
    val special_characters = "!@#$%^&*()-+"
    var passwordLenght = 6
    var digit = 1
    var lowerCase = 1
    var upperCase = 1
    var specialCharacter = 1
    var strongPassword = 0
    password.toCharArray.foreach(c => {
      if (c isUpper) {
        upperCase -= 1
        passwordLenght -= 1
      }
      if (c isLower) {
        lowerCase -= 1
        passwordLenght -= 1
      }
      if (c isDigit) {
        digit -= 1
        passwordLenght -= 1
      }
      if (special_characters.indexOf(c) >= 0) {
        passwordLenght -= 1
        specialCharacter -= 1
      }
      val expected = Math.max(0, upperCase) + Math.max(0, lowerCase) + Math.max(0, digit) + Math.max(0, specialCharacter)
      strongPassword = if (expected > passwordLenght) {
        expected
      } else {
        passwordLenght
      }
    })
    strongPassword
  }

  @Test
  def twoCharaters: Unit = {
    println(twoCharaters("uyetuppelecblwipdsqabzsvyfaezeqhpnalahnpkdbhzjglcuqfjnzpmbwprelbayyzovkhacgrglrdpmvaexkgertilnfoo"))
  }

  var processed: List[String] = List()

  def twoCharaters(s: String): Int = {
    var maxLength = 0
    s.toCharArray.foreach(c1 => {
      var word = ""
      s.toCharArray.foreach(c2 => {
        val str = String.copyValueOf(Array(c1, c2))
        val str1 = String.copyValueOf(Array(c2, c1))
        if (!processed.contains(str) && !processed.contains(str1)) {
          processed = processed ++ List(str)
          if (c1 != c2) {
            word = s.toCharArray.filter(c => c == c1 || c == c2).mkString
            var previousWord: Char = 0
            var total = if (word.isEmpty) 2 else 0
            var repeated = false
            word.toCharArray.foreach(c => {
              if (previousWord != c) {
                previousWord = c
                total += 1
              } else {
                repeated = true
              }
            })
            if (total > maxLength && !repeated) {
              maxLength = total
            }
          }
        }
      })
    })
    maxLength
  }

  /**
    * Check if a sentence can be consider as a circle.
    */
  @Test
  def stringCircle(): Unit = {
    println(stringCircle("circle is when start and finish with circle"))
    println(stringCircle("Otherwise is not consider as a circle circle"))
  }

  def stringCircle(sentence: String): Boolean = {
    val strings = sentence.split(" ")
    strings.head == strings.last
  }

  /**
    * Palindrome algorithm is quite easy, we just need to get the word, create an array of chars and compare
    * from the beginning to the last in the iteration where in every iteration we move to compare from the beginning and end
    */
  @Test
  def palindromeString(): Unit = {
    println(isPalindrome("AbsbA"))
    println(isPalindrome("AbsbE"))
  }

  def isPalindrome(str: String): Boolean = {
    var palindrome = true
    val word = str.toCharArray
    word.indices foreach (i => {
      val lastIndex = word.length - 1 - i
      if (word(i) != word(lastIndex)) {
        palindrome = false
      }
    })
    palindrome
  }

  /**
    * We apply the very same principle but with a sentnece. We split the array of Strings instead of Chars.
    */
  @Test
  def palindromeSentence(): Unit = {
    println(palindromeSentence("This is a palindrome a is This"))
    println(palindromeSentence("This is not a palindrome a is This"))
  }

  def palindromeSentence(sentence: String): Boolean = {
    var palindrome = true
    val words = sentence.split(" ")
    words.indices foreach (i => {
      val lastIndex = words.length - 1 - i
      if (words(i) != words(lastIndex)) {
        palindrome = false
      }
    })
    palindrome
  }

  @Test
  def palindromeWithRecursion(): Unit = {
    isPalindrome = true
    println(palindromeWithRecursion("basab"))
    isPalindrome = true
    println(palindromeWithRecursion("basax"))
    isPalindrome = true
    println(palindromeWithRecursion("abcba"))
  }

  var isPalindrome = true

  def palindromeWithRecursion(str: String): Boolean = {
    val array:Array[Char] = str.toCharArray
    if (str.length - 1 > 1) {
      array.indices foreach (i => {
        if (array(i) != array(array.length - 1 - i)) {
          isPalindrome = false
        } else {
          palindromeWithRecursion(str.substring(1, str.length - 1))
        }
      })
    }
    isPalindrome
  }
}