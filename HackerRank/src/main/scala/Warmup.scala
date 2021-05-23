import scala.util.Sorting

object Warmup extends App {

  weighOfString()

  /**
    * https://www.hackerrank.com/challenges/sock-merchant/problem?h_l=interview&playlist_slugs%5B%5D%5B%5D=interview-preparation-kit&playlist_slugs%5B%5D%5B%5D=warmup&isFullScreen=true
    */
  def searchPairOfSocks(args: Array[String]): Unit = {
    val socks = Array(10, 20, 20, 10, 10, 30, 50, 10, 20)
    val pairSocks = socks.foldLeft(Map[Int, Int]())((acc, next) => {
      acc ++ Map(next -> (acc.getOrElse(next, 0) + 1))
    }).filter(entry => entry._2 / 2 > 0)
      .map(entry => entry._2 / 2)
      .sum
    println(pairSocks)
  }

  /**
    * https://www.hackerrank.com/challenges/counting-valleys/problem?h_l=interview&h_r=next-challenge&h_v=zen&isFullScreen=false&playlist_slugs%5B%5D%5B%5D%5B%5D=interview-preparation-kit&playlist_slugs%5B%5D%5B%5D%5B%5D=warmup
    */
  def findNumberOfValleys(): Unit = {
    val stepsInfo = "DDUUDDUDUUUD"
    var totalValley = 0
    stepsInfo.toArray.foldLeft(0)((seaLevel, step) => {
      if (step.toString == "D") {
        seaLevel - 1
      } else {
        val upLevel = seaLevel + 1
        if (upLevel == 0) {
          totalValley += 1
        }
        upLevel
      }
    })
    println(totalValley)
  }

  /**
    * Cloud jump
    * ----------
    * https://www.hackerrank.com/challenges/jumping-on-the-clouds/problem?h_l=interview&h_r=next-challenge&h_v=zen&isFullScreen=false&playlist_slugs%5B%5D%5B%5D%5B%5D=interview-preparation-kit&playlist_slugs%5B%5D%5B%5D%5B%5D=warmup&h_r=next-challenge&h_v=zen
    */
  def cloudJump(): Unit = {
    var cloudSteps: Array[Int] = Array()
    val c = Array(0, 0, 0, 1, 0, 0)
    c.indices foreach (i => {
      if (c(i) == 0) {
        if (cloudSteps.isEmpty) {
          cloudSteps = cloudSteps ++ Array(i)
        } else {
          if (cloudSteps.length >= 2 && (cloudSteps(cloudSteps.length - 2) + 2) >= i) {
            cloudSteps(cloudSteps.length - 1) = i
          }
          if (cloudSteps.last != i) {
            cloudSteps = cloudSteps ++ Array(i)
          }
          println(cloudSteps.mkString("Array(", ", ", ")"))
        }
      }
    })
    println(cloudSteps.length - 1)
  }

  /**
    * https://www.hackerrank.com/challenges/repeated-string/problem?h_l=interview&h_r=next-challenge&h_v=zen&isFullScreen=false&playlist_slugs%5B%5D%5B%5D%5B%5D=interview-preparation-kit&playlist_slugs%5B%5D%5B%5D%5B%5D=warmup&h_r=next-challenge&h_v=zen&h_r=next-challenge&h_v=zen
    */
  def repeatedString(): Unit = {
    val s = "ababa"
    val n: Long = 3
    val numberOfa = s.count(c => c.toString == "a")
    val l: Long = n / s.length
    var count = numberOfa * l

    val mod = n % s.length
    val letters: Array[Char] = s.toArray
    for (i <- 0 until mod.toInt) {
      if (letters(i).toString == "a") {
        count += 1
      }
    }
    println(count)
  }

  /**
    * https://www.hackerrank.com/challenges/ctci-array-left-rotation/problem?h_l=interview&playlist_slugs%5B%5D=interview-preparation-kit&playlist_slugs%5B%5D=arrays
    */
  def arrayRotation(): Unit = {
    val d = 4
    val a = Array(1, 2, 3, 4, 5)
    val arrayLen = a.length
    val result = a.indices.foldLeft(new Array[Int](arrayLen))((leftRotateArray, i) => {
      val leftRotate = i - d
      val newIndex = if (leftRotate < 0) {
        leftRotate + arrayLen
      } else {
        leftRotate
      }
      leftRotateArray(newIndex) = a(i)
      leftRotateArray
    })
    println(result.foldLeft("")((acc, next) => acc + " " + next))
  }

  /**
    * https://www.hackerrank.com/challenges/new-year-chaos/problem?h_l=interview&playlist_slugs%5B%5D=interview-preparation-kit&playlist_slugs%5B%5D=arrays&h_r=next-challenge&h_v=zen
    */
  def bribeArrayPos(): Unit = {
    val bribeArray = Array(1, 2, 5, 3, 7, 8, 6, 4)
    var totalMovements = 0
    bribeArray.indices.foreach(i => {
      var movement = 0
      for (x <- i until bribeArray.length) {
        val bribePos = bribeArray(i)
        val comparedPos = bribeArray(x)
        if (bribePos > comparedPos) {
          movement += 1
        }
        if (movement > 2) {
          println("Too chaotic")
          return
        }
      }
      totalMovements += movement
    })
    println(totalMovements)
  }

  /**
    * https://www.hackerrank.com/challenges/day-of-the-programmer/problem
    *
    * @return
    */
  def dayOfTheProgrammer(): String = {
    val year = 1918
    val halfYearDays = if (year % 4 == 0) {
      29
    } else {
      28
    }
    val halfYear = halfYearDays + 215
    if (year == 1918) {
      return "26.09.1918"
    }
    s"${256 - halfYear}.09.$year"
  }

  def billDinner(): Unit = {
    val bill = Array(3, 10, 2, 9)
    val k = 1
    val b = 12

    val myShare = bill.indices.foldLeft(0)((acc, i) => {
      val total = if (i != k) {
        acc + bill(i)
      } else {
        acc
      }
      total
    }) / 2
    if (myShare == b) {
      println("Bon Appetit")
    } else {
      val totalBill = bill.sum / 2
      val fairBill = totalBill - myShare
      println(fairBill)
    }
  }

  /**
    * https://www.hackerrank.com/challenges/drawing-book/problem
    */
  def flipPages(): Unit = {
    val n = 6
    val p = 2
    val x = p / 2
    val y = n / 2 - p / 2
    val best = Math.min(x, y)
    println(best)
  }

  /**
    * https://www.hackerrank.com/challenges/reduced-string/problem
    */
  def superReduceString(): Unit = {
    val s = "aaabccddd"
    var letters = s.toArray
    var matchFound = true
    while (matchFound) {
      matchFound = false
      letters.indices.foreach(i => {
        if (i < letters.length - 1 && letters(i) == letters(i + 1)) {
          letters(i) = 0
          letters(i + 1) = 0
          matchFound = true
        }
      })
      letters = letters.filter(c => c != 0)
    }
    val result = if (letters.isEmpty) {
      "Empty String"
    } else {
      letters.foldLeft("")(_ + _)
    }
    println(result)
  }

  /**
    * https://www.hackerrank.com/challenges/caesar-cipher-1/problem?h_r=next-challenge&h_v=zen
    */
  def cypherCaesar(): Unit = {
    val s = "www.abc.xy"
    val k = 87
    var cipherS = ""
    val rest = k % 26
    s.toCharArray.foreach(c => {
      val newChar = if (c.isLetter) {
        var newChar = if (rest != 0) {
          c.toInt + rest
        } else {
          c.toInt + k
        }
        if (c >= 65 && c <= 90) {
          //Upper
          if (newChar > 90) {
            newChar = newChar - 26
          }
        }
        if (c >= 97 && c <= 122) {
          //Lower
          if (newChar > 122) {
            newChar = newChar - 26
          }
        }
        newChar
      } else {
        c
      }
      cipherS += newChar.toChar.toString
    })
    println(cipherS)
    //fff.jkl.gh
  }

  /**
    * https://www.hackerrank.com/challenges/mars-exploration/problem?h_r=next-challenge&h_v=zen&h_r=next-challenge&h_v=zen
    */
  def marsExplorer(): Unit = {
    val s = "SOSOSOOSOSOSOSSOSOSOSOSOSOS"
    val x = s.length / 3
    var messages = List[String]()
    var index = 0
    for (_ <- 0 until x) {
      messages = messages ++ List(s.substring(index, index + 3))
      index += 3
    }
    val wrongLetters = messages.foldLeft(0)((count, msg) => {
      var newCount = count
      newCount = if (msg.toCharArray.apply(0).toString != "S") newCount + 1 else newCount
      newCount = if (msg.toCharArray.apply(1).toString != "O") newCount + 1 else newCount
      newCount = if (msg.toCharArray.apply(2).toString != "S") newCount + 1 else newCount
      newCount
    })
    println(wrongLetters)
  }

  /**
    * https://www.hackerrank.com/challenges/hackerrank-in-a-string/problem?h_r=next-challenge&h_v=zen&h_r=next-challenge&h_v=zen&h_r=next-challenge&h_v=zen
    */
  def hackerRankInString(): Unit = {
    val s = "rhbaasdndfsdskgbfefdbrsdfhuyatrjtcrtyytktjjt" //NO
    //    val s = "hhssaacckfkesskraraahhnnk" // YES
    val hackerRank = "hackerrank".toCharArray
    val response = if (s.length >= hackerRank.length) {
      val array: Array[Char] = s.toArray
      var rankIndex = 0
      array.indices.foreach(i => {
        val value = hackerRank(rankIndex).toString
        if (rankIndex < hackerRank.length && value == array(i).toString) {
          rankIndex += 1
        }
      })
      if (rankIndex == hackerRank.length) "YES" else "NO"
    } else {
      "NO"
    }
    println(response)
  }

  /**
    * https://www.hackerrank.com/challenges/pangrams/problem
    */
  def pangramsString(): Unit = {
    val s = "We promptly judged antique ivory buckles for the prize"
    val alpha = "abcdefghijklmnopqrstuvwxyz".toArray
    var totalAlpha = 0
    val response = if (s.length < alpha.length) {
      "not pangram"
    } else {

      val str = s.toLowerCase.replace(" ", "")
      alpha.foreach(c => {
        if (str.contains(c)) {
          totalAlpha += 1
        }
      })
      if (totalAlpha == alpha.length) "pangram" else "not pangram"
    }
    println(response)
  }

  /**
    * https://www.hackerrank.com/challenges/weighted-uniform-string/problem?h_r=next-challenge&h_v=zen
    */
  def weighOfString(): Unit = {
    val s: String = "abccddde" //1,2,6,4,8,12,5
    val queries: Array[Int] = Array(1, 3, 12, 5, 9, 10)
    val sArray = s.toArray
    val alpha = Map(
      "a" -> 1,
      "b" -> 2,
      "c" -> 3,
      "d" -> 4,
      "e" -> 5,
      "f" -> 6,
      "g" -> 7,
      "h" -> 8,
      "i" -> 9,
      "j" -> 10,
      "k" -> 11,
      "l" -> 12,
      "m" -> 13,
      "n" -> 14,
      "o" -> 15,
      "p" -> 16,
      "q" -> 17,
      "r" -> 18,
      "s" -> 19,
      "t" -> 20,
      "u" -> 21,
      "v" -> 22,
      "w" -> 23,
      "x" -> 24,
      "y" -> 25,
      "z" -> 26)

    var totalValues: List[Int] = List()
    var previousLetter: String = ""
    sArray.foreach(letter => {
      val length: Int = previousLetter.toArray.count(c => c.toString == letter.toString)
      if (length > 0) {
        previousLetter = letter.toString + previousLetter
        totalValues = totalValues ++ List(alpha(letter.toString) * (length + 1))
      } else {
        previousLetter = letter.toString
        totalValues = totalValues ++ List(alpha(letter.toString))
      }
    })
    println(totalValues.toString())
    val output: Array[String] = new Array(queries.length)
    queries.indices.foreach(i => {
      val value = queries(i)
      output(i) = "No"
      if (totalValues.contains(value)) {
        output(i) = "Yes"
      }
    })
    println(output.mkString("Array(", ", ", ")"))
  }
}
