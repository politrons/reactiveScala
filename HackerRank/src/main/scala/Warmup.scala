import scala.collection.mutable

object Warmup extends App {

  arrayRotation()

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
}
