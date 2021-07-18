import scala.::
import scala.util.Sorting

object HackerRank extends App {

  palindromeIndex()

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
          cloudSteps = cloudSteps :+ i
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
    var previousLetter: String = ""
    val output: Array[String] = new Array(queries.length)
    output.indices.foreach(i => {
      output(i) = "No"
    })
    sArray.foreach(letter => {
      val length: Int = previousLetter.toArray.count(c => c == letter)
      val currentValue = if (length > 0) {
        previousLetter = letter.toString + previousLetter
        (letter.toInt - 96) * (length + 1)
      } else {
        previousLetter = letter.toString
        letter.toInt - 96
      }
      val queryIndex = queries.indexOf(currentValue)
      if (queryIndex >= 0) {
        output(queryIndex) = "Yes"
      }
    })
    println(output.mkString("Array(", ", ", ")"))
  }

  /**
    * https://www.hackerrank.com/challenges/separate-the-numbers/problem
    * TODO:Unsolved
    */
  def separateNumbers(): Unit = {
    val s = "101112"
    var fromCurrent = 0
    var toCurrent = 1
    var fromNext = 1
    var toNext = 2

    var finish = false
    while (!finish) {
      val current = s.substring(fromCurrent, toCurrent).toInt
      val right = s.substring(fromNext, toNext).toInt
      if (current > right) {
        toNext += 1
      } else if (right - current != 1) {
        toCurrent += 1
        fromNext += 1
        toNext += 1
      } else {
        println(s"Current:$current")
        println(s"Right:$right")
        fromCurrent = toCurrent
        toCurrent = toNext
        fromNext = fromNext + 1
        toNext = toNext + 1
        if (toNext > s.length) {
          finish = true
        }
      }
    }
  }

  /**
    * https://www.hackerrank.com/challenges/funny-string/problem
    */
  def funnyString(): Unit = {
    val s = "bcxz" //97 99 120 122
    val array = s.toArray
    val diffArray: Array[Int] = new Array(array.length)
    val revArray: Array[Int] = new Array(array.length)
    array.indices.foreach(i => {
      if (i + 1 < array.length) {
        val diff = Math.abs(array(i).toInt - array(i + 1).toInt)
        val revDiff = Math.abs(array(array.length - (i + 1)).toInt - array(array.length - (i + 2)).toInt)
        diffArray(i) = diff
        revArray(i) = revDiff
      }
    })
    var output = "Funny"
    for (i <- array.indices) {
      if (diffArray(i) != revArray(i)) {
        output = "Not Funny"
      }
    }
    println(output)
  }

  /**
    * https://www.hackerrank.com/challenges/gem-stones/problem?h_r=next-challenge&h_v=zen
    */
  def gemStorm(): Unit = {
    val arr = Array("abcdde", "baccd", "eeabg")
    var gemStones: Map[Char, Boolean] = Map()
    arr.foreach(rock => {
      val minerals: Array[Char] = rock.toArray.distinct
      minerals.foreach(mineral => {
        val mineralInRocks = arr.count(rock => rock.contains(mineral.toString))
        if (mineralInRocks == arr.length) {
          gemStones = gemStones ++ Map(mineral -> true)
        }
      })
    })
    println(gemStones.size)
  }

  /**
    * https://www.hackerrank.com/challenges/alternating-characters/problem?h_r=next-challenge&h_v=zen&h_r=next-challenge&h_v=zen
    */
  def alteringCharacters(): Unit = {
    val s = "ABABABAA"
    val array = s.toArray
    var deleteNumber = 0
    array.indices.foreach(index => {
      if (index < array.length - 1) {
        val current = array(index)
        if (current.toInt > 0 && index <= array.length - 2 && current == array(index + 1)) {
          array(index) = 0
          deleteNumber += 1
        }
      }
    })
    println(deleteNumber)
  }

  /**
    * https://www.hackerrank.com/challenges/beautiful-binary-string/problem?h_r=next-challenge&h_v=zen&h_r=next-challenge&h_v=zen&h_r=next-challenge&h_v=zen
    */
  def beautifulBinaryString(): Unit = {
    val b = "0101010"
    val nonBeautiful = "010"
    val array = b.toArray
    var deleteTimes = 0
    array.indices.foreach(i => {
      if (i >= 2) {
        if (s"${array(i - 2).toString}${array(i - 1).toString}${array(i).toString}" == nonBeautiful) {
          array(i) = if (array(i) == 49) 48 else 49
          deleteTimes += 1
        }
      }
    })
    println(deleteTimes)
  }

  /**
    * https://www.hackerrank.com/challenges/the-love-letter-mystery/problem?h_r=next-challenge&h_v=zen&h_r=next-challenge&h_v=zen&h_r=next-challenge&h_v=zen&h_r=next-challenge&h_v=zen
    */
  def theLoveLetterMystery(): Unit = {
    val s = "cba"
    val array = s.toArray
    var operation = 0

    def reduceValue(i: Int): Unit = {
      val reverseIndex = s.length - 1 - i
      if (array(i) > array(reverseIndex) && array(i).toString != "a") {
        array(i) = (array(i).toInt - 1).toChar
        operation += 1
        reduceValue(i)
      } else if (array(i) < array(reverseIndex) && array(reverseIndex).toString != "a") {
        array(reverseIndex) = (array(reverseIndex).toInt - 1).toChar
        operation += 1
        reduceValue(i)
      }
    }

    array.indices.foreach(i => {
      reduceValue(i)
    })
    println(operation)
  }

  /**
    * https://www.hackerrank.com/challenges/ctci-fibonacci-numbers/problem
    */
  def fibonacciNumbers(): Unit = {
    val x = 6
    val fibonacciSequence: Array[Int] = new Array(x + 1)
    for (i <- 0 to x) {
      if (i < 2) {
        fibonacciSequence(i) = i
      } else {
        fibonacciSequence(i) = fibonacciSequence(i - 2) + fibonacciSequence(i - 1)
      }
    }
    println(fibonacciSequence(x))
  }

  /**
    * https://www.hackerrank.com/challenges/fibonacci-finding-easy/problem
    */
  def fibonacciFinder(): Unit = {
    val a = 509618737
    val b = 460201239
    val n = 229176339

    val fibonacciSequence = new Array[Long](n + 1)

    def calcNextNumber(index: Int): Unit = {
      if (index <= n) {
        if (index < 2) {
          fibonacciSequence(index) = a
          fibonacciSequence(index + 1) = b
          calcNextNumber(index + 2)
        } else {
          fibonacciSequence(index) = fibonacciSequence(index - 2) + fibonacciSequence(index - 1)
          calcNextNumber(index + 1)
        }
      }
    }

    calcNextNumber(0)

    println(fibonacciSequence(n))
  }

  /**
    * https://www.hackerrank.com/challenges/palindrome-index/problem
    */
  def palindromeIndex(): Unit = {
    val s = "hgygsvlfwcwnswtuhmyaljkqlqjjqlqkjlaymhutwsnwcflvsgygh"
    val array: Array[Char] = s.toArray
    var indexToMove: Int = -1

    def findThePalindrome(array: Array[Char]): Unit = {
      array.indices.foreach(i => {
        val left = array(i)
        val right = array(array.length - 1 - i)
        if (left != right) {
          val leftArray = array
          leftArray(i) = 0
          val filterArray = leftArray.filter(c => c != 0)
          findThePalindrome(filterArray)
          if (indexToMove == -1) {
            indexToMove = array.length - 1 - i
          } else {
            indexToMove = i
          }
        }
      })
    }

    findThePalindrome(array)
    println(indexToMove)
  }

  /**
    * https://www.hackerrank.com/challenges/electronics-shop/problem
    */
  def findBestProduct(): Unit = {

    val keyboards: Array[Int] = Array(3, 1)
    val drives: Array[Int] = Array(5, 2, 8)
    val b: Int = 10
    val prices: Array[Int] = new Array[Int](keyboards.length * drives.length)

    var index = 0
    keyboards.foreach(keyboard => {
      drives.foreach(driver => {
        prices(index) = keyboard + driver
        index += 1
      })
    })
    Sorting.quickSort(prices)

    val finalPrice = prices.reverse.find(price => price <= b).getOrElse(-1)
    println(finalPrice)

  }

  /**
    * https://www.hackerrank.com/challenges/cats-and-a-mouse/problem?h_r=next-challenge&h_v=zen
    */
  def catsAndMouse(): Unit = {

    val x: Int = 1
    val y: Int = 3
    val z: Int = 2

    val movesCatA = Math.abs(x - z)
    val movesCatB = Math.abs(y - z)
    if (movesCatA == movesCatB) {
      println("Mouse C")
    } else if (movesCatA < movesCatB) {
      println("Cat A")
    } else {
      println("Cat B")
    }
  }

  /**
    * https://www.hackerrank.com/challenges/picking-numbers/problem
    */
  def pickingNumbers(): Unit = {

    val a: Array[Int] = Array(4, 6, 5, 3, 3, 1)

    var bestSubArray = 0
    a.foreach(x => {
      var lenSubArray = 0
      a.foreach(y => {
        val diff = x - y
        if (diff <= 1 && diff >= 0) {
          lenSubArray += 1
        }
      })
      if (lenSubArray > bestSubArray) bestSubArray = lenSubArray
    })
    println(bestSubArray)
  }

  /**
    * https://www.hackerrank.com/challenges/the-hurdle-race/problem
    */
  def hurdlerRace(): Unit = {
    val k: Int = 47
    val height: Array[Int] = Array(52, 99, 93, 84, 50, 64, 61, 87, 89, 97, 64, 69, 61, 90, 82, 53, 50, 63, 82, 87, 76, 78, 75, 55, 80, 68, 75, 83, 69, 81, 95, 89, 60, 59, 90, 100, 90, 64, 53, 60, 88, 93, 62, 50, 75, 77, 60, 93, 55, 79, 52, 47, 65, 74, 62, 60, 96, 49, 73, 92, 79, 54, 100, 81, 63, 58, 75, 80, 89, 94, 52, 85, 57, 72, 97, 81, 97, 66, 84, 77, 83, 94, 85, 68, 99, 54, 64, 83, 67, 84, 81, 65, 59, 89, 68, 91, 60, 79, 74, 57)

    Sorting.quickSort(height)
    val numbOfDoses = height.reverse.head - k
    val i = if (numbOfDoses > 0) {
      numbOfDoses
    } else {
      0
    }
    println(i)
  }

  /**
    * https://www.hackerrank.com/challenges/designer-pdf-viewer/problem?h_r=next-challenge&h_v=zen
    */
  def designerPdfViewer(): Unit = {
    val h: Array[Int] = Array(1, 3, 1, 3, 1, 4, 1, 3, 2, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5)
    val word: String = "abc"
    val highlight: Array[Int] = new Array(word.length)
    word.toArray.indices.foreach(i => {
      val index = word(i).toInt - 97
      highlight(i) = h(index)
    })
    Sorting.quickSort(highlight)
    val mm3 = highlight.last * word.length
    println(mm3)
  }

  /**
    * https://www.hackerrank.com/challenges/utopian-tree/problem?h_r=next-challenge&h_v=zen&h_r=next-challenge&h_v=zen
    */
  def utopianTree() {
    val n: Int = 4

    var height = 1
    val spring: Int => Int = a => a * 2
    val summer: Int => Int = a => a + 1

    val yearCycle: Int = n / 2

    for (_ <- 0 until yearCycle) {
      height = summer(spring(height))
    }
    if (n % 2 != 0) {
      height = spring(height)
    }
    println(height)
  }

  /**
    * https://www.hackerrank.com/challenges/angry-professor/problem?h_r=next-challenge&h_v=zen&h_r=next-challenge&h_v=zen&h_r=next-challenge&h_v=zen
    */
  def angryProfessor() {
    val k: Int = 3
    val a: Array[Int] = Array(-1, -3, 4, 2)
    val totalStudentOnTime = a.count(t => t <= 0)
    val result = if (totalStudentOnTime >= k) {
      "NO"
    } else {
      "YES"
    }
    println(result)
  }


  /**
    * https://www.hackerrank.com/challenges/beautiful-days-at-the-movies/problem?h_r=next-challenge&h_v=zen&h_r=next-challenge&h_v=zen&h_r=next-challenge&h_v=zen&h_r=next-challenge&h_v=zen
    */
  def beautifullyDays() {
    val i: Int = 20
    val j: Int = 23
    val k: Int = 6
    var beautifulDays = 0
    for (x <- i to j) {
      val day = x.toString.toArray
      val reverse = day.reverse
      val reverseDay = reverse.mkString("").toInt
      if ((x - reverseDay) % k == 0) {
        beautifulDays += 1
      }
    }
    println(beautifulDays)
  }


  /**
    * https://www.hackerrank.com/challenges/strange-advertising/problem?h_r=next-challenge&h_v=zen&h_r=next-challenge&h_v=zen&h_r=next-challenge&h_v=zen&h_r=next-challenge&h_v=zen&h_r=next-challenge&h_v=zen
    */
  def viralAdvertising() {
    val n: Int = 3
    var total = 0
    var init = 5
    for (_ <- 0 until n) {
      val i = init / 2
      total += i
      init = i * 3
    }
    println(total)
  }


}
