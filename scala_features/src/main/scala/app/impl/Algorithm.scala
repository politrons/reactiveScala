package app.impl

import org.junit.Test

class Algorithm {

  @Test
  def simpleArraySum: Unit = {
    val sum = simpleArraySum(Array(1, 2, 3, 4, 10, 11))
    println(sum)
  }

  def simpleArraySum(ar: Array[Int]): Int = {
    ar.toList.sum
  }

  @Test
  def compareArray(): Unit = {
    val a = Array(5, 6, 7)
    val b = Array(3, 6, 10)

    var alice = 0
    var bob = 0
    a.zip(b)
      .foreach(tuple => if (tuple._1 > tuple._2) {
        alice += 1
      } else if (tuple._1 < tuple._2) {
        bob += 1
      })

    println(Array(alice, bob))

  }

  @Test
  def sumInteger(): Unit = {
    val total = 5
    val array = Array[Long](1000000001, 1000000002, 1000000003, 1000000004, 1000000005)
    println(aVeryBigSum(total, array))
  }

  def aVeryBigSum(n: Int, ar: Array[Long]): Long = {
    ar.sum
  }

  @Test
  def diagonalMatrix: Unit = {
    val matrix: Array[Array[Int]] = Array(Array(), Array(), Array())
    matrix.update(0, Array(11, 2, 4))
    matrix.update(1, Array(4, 5, 6))
    matrix.update(2, Array(10, 8, -12))
    diagonalDifference(matrix)
  }

  def diagonalDifference(matrix: Array[Array[Int]]): Int = {
    val dimension = matrix.length - 1
    var dimensionX = 0
    var dimensionY = matrix.length - 1

    var diagonalX = 0
    0 to dimension foreach (index => {
      diagonalX += matrix(index)(dimensionX)
      dimensionX += 1
    })

    var diagonalY = 0
    0 to dimension foreach (index => {
      diagonalY += matrix(index)(dimensionY)
      dimensionY -= 1
    })

    Math.abs(diagonalX - diagonalY)

  }

  @Test
  def plusMinusTest: Unit = {
    plusMinus(Array(-4, 3, -9, 0, 4, 1))
  }

  def plusMinus(arr: Array[Int]) {
    val size: Float = arr.length
    var positive: Float = 0
    var negative: Float = 0
    var zero: Float = 0
    arr.foreach {
      case x if x > 0 => positive += 1
      case x if x < 0 => negative += 1
      case x if x == 0 => zero += 1
    }
    val positiveFraction: Float = positive / size
    println(positiveFraction)
    val negativeFraction: Float = negative / size
    println(negativeFraction)
    val zeroFraction: Float = zero / size
    println(zeroFraction)

  }

  @Test
  def staircase: Unit = {
    staircase(6)
  }

  def staircase(n: Int) {
    1 to n foreach (index => {
      var totalCounter = n - index
      1 to n foreach (_ => {
        if (totalCounter > 0) {
          print(" ")
        } else {
          print("#")
        }
        totalCounter -= 1
      })
      println("")
    })
  }

  @Test
  def miniMaxSum: Unit = {
    miniMaxSum(Array(426980153, 354802167, 142980735, 968217435, 734892650))
  }

  def miniMaxSum(arr: Array[Int]) {
    var min: Long = 0
    var max: Long = 0
    val sorted = arr.sorted
    val count = sorted.length - 1
    sorted.indices foreach (index => {
      if (count != index) {
        max += sorted(index)
      }
    })
    sorted.indices foreach (index => {
      if (index > 0) {
        min += sorted(index)
      }
    })
    println(s"$max $min")
  }

  @Test
  def birthdayCakeCandles: Unit = {
    print(birthdayCakeCandles(4, Array(3, 2, 1, 3)))

  }

  def birthdayCakeCandles(n: Int, ar: Array[Int]): Int = {
    var totalBlow = 0
    val sorted = ar.sorted
    val max = sorted.max
    sorted.indices.foreach(index => {
      val i = sorted(index)
      if (i >= max) {
        totalBlow += 1
      }
    })
    totalBlow
  }

  @Test
  def timeConversion: Unit = {
    val time = timeConversion("12:45:54PM")
    assert(time == "12:45:54")

  }

  def timeConversion(s: String): String = {
    val meridian = s.substring(s.length - 2, s.length)
    if (meridian == "PM") {
      if (s.substring(0, 2) == "12") {
        s.replace("PM", "")
      }else{
        val new_hour = s.substring(0, 2).toLong + 12
        new_hour + s.substring(2, s.length).replace("PM", "")
      }
    } else {
      if (s.substring(0, 2) == "12") {
        "00" + s.substring(2, s.length).replace("AM", "")
      } else {
        s.replace("AM", "")
      }
    }
  }

}
