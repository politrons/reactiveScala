package app.impl.algorithms

import org.junit.Test

import scala.collection.immutable.{List, Map}

class LogicAlgorithm {

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
    val matrix: Array[Array[Int]] = new Array(3)
    matrix.update(0, Array(11, 2, 4))
    matrix.update(1, Array(4, 5, 6))
    matrix.update(2, Array(10, 8, -12))
    print(diagonalDifference(matrix))
  }

  /**
    * Here having two index from left to right and right to left and index from up to down we finally manage to have
    * the diagonals values of the matrix which we sum.
    */
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
      } else {
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

  @Test
  def maximumToys: Unit = {
    assert(maximumToys(Array(1, 12, 5, 111, 200, 1000, 10), 50) == 4)

  }

  def maximumToys(prices: Array[Int], k: Int): Int = {
    var money = k
    val sorted = prices.sorted
    var presents = 0
    sorted.foreach(price => {
      if (money - price > 0) {
        money -= price
        presents += 1
      }
    })
    presents
  }

  @Test
  def getMinimumCost: Unit = {
    println(getMinimumCost(2, 3, Array(2, 5, 6)))

  }


  def getMinimumCost(people: Int, numberOfFlowers: Int, flowers: Array[Int]): Int = {
    val sorted = flowers.sorted.reverse
    val iterations = numberOfFlowers / people
    val mod = numberOfFlowers % people
    var purchases = 1
    var count = 0
    var total = 0
    0 until iterations foreach (_ => {
      0 until people foreach (_ => {
        total += sorted(count) * purchases
        count += 1
      })
      purchases += 1
    })
    0 until mod foreach (_ => {
      total += sorted(count) * purchases
      count += 1
    })
    total
  }

  @Test
  def angryChildren: Unit = {
    print(angryChildren(4, Array(10, 4, 1, 2, 3, 4, 10, 20, 30, 40, 100, 200)))
  }


  def angryChildren(k: Int, arr: Array[Int]): Int = {
    var total = 0
    val sorted = arr.sorted
    0 to sorted.length - k foreach (i => {
      val tmp = sorted(i + k - 1) - sorted(i)
      if (total == 0) {
        total = tmp
      } else if (tmp <= total) {
        total = tmp
      }
    })
    total
  }

  @Test
  def jimOrders: Unit = {
    val orders: Array[Array[Int]] = Array(Array(), Array(), Array(), Array(), Array())
    orders.update(0, Array(8, 1))
    orders.update(1, Array(4, 2))
    orders.update(2, Array(5, 6))
    orders.update(3, Array(3, 1))
    orders.update(4, Array(4, 3))

    val ints = jimOrders(orders)
    print(ints.mkString(" "))
  }

  def jimOrders(orders: Array[Array[Int]]): Array[Int] = {
    var orderOrders: Map[Int, List[Int]] = Map()
    var fasterOrders: Array[Int] = Array()
    orders.indices foreach (customer => {
      val order = orders(customer)(0)
      val preparation = orders(customer)(1)
      val serveTime: Int = order + preparation

      val customers = orderOrders.find(f => f._1 == serveTime)
      if (customers.isDefined) {
        orderOrders = orderOrders ++ Map(serveTime -> (customers.get._2 ++ List(customer)))
      } else {
        orderOrders = orderOrders ++ Map(serveTime -> List(customer))
      }
      fasterOrders = fasterOrders ++ Array(serveTime)
    })
    var outPutArray: Array[Int] = Array()
    fasterOrders.sorted foreach (time => {
      val customers = orderOrders(time).sorted
      customers foreach (customer => {
        outPutArray = outPutArray ++ Array(customer + 1)
      })
    })
    outPutArray
  }

  @Test
  def twoArrays: Unit = {
    val A = Array(4, 4, 3, 2, 1, 4, 4, 3, 2, 4)
    val B = Array(2, 3, 0, 1, 1, 3, 1, 0, 0, 2)
    println(twoArrays(4, A, B))
  }

  def twoArrays(k: Int, A: Array[Int], B: Array[Int]): String = {
    val Ar = A.sorted
    val Br = B.sorted.reverse
    var higher = true
    A.indices foreach (index => {
      if (Ar(index) + Br(index) < k) {
        higher = false
      }
    })
    if (higher) {
      "YES"
    } else {
      "NO"
    }
  }

  @Test
  def gradingStudents: Unit = {
    val ints = gradingStudents(Array(73, 67, 38, 33))
    println(ints.mkString(" "))
  }

  def gradingStudents(grades: Array[Int]): Array[Int] = {
    var outputArray: Array[Int] = Array()
    grades foreach (grade => {
      var output = grade
      if (grade >= 38) {
        val mod = grade % 10
        val total = Math.abs(mod - 10)
        if (total < 3) {
          output = grade + total
        } else if (total >= 5) {
          val newTotal = total - 5
          if (newTotal < 3) {
            output = grade + newTotal
          }
        }
      }
      outputArray = outputArray ++ Array(output)
    })
    outputArray
  }


  @Test
  def countApplesAndOranges: Unit = {
    countApplesAndOranges(7, 11, 5, 15, Array(-2, 2, 1), Array(5, -6))
  }

  def countApplesAndOranges(s: Int, t: Int, a: Int, b: Int, apples: Array[Int], oranges: Array[Int]) {
    var totalApple = 0
    apples.map(distance => {
      if (distance < 0) {
        a - Math.abs(distance)
      } else {
        a + distance
      }
    }).filter(position => position >= s && position <= t)
      .foreach(_ => totalApple += 1)

    var totalOrange = 0
    oranges foreach (distance => {
      val position = if (distance < 0) {
        b - Math.abs(distance)
      } else {
        b + distance
      }
      if (position >= s && position <= t) {
        totalOrange += 1
      }
    })
    println(totalApple)
    println(totalOrange)
  }

  @Test
  def kangaroo: Unit = {
    //    print(kangaroo(0, 3, 4, 2))
    print(kangaroo(0, 2, 5, 3))
  }

  def kangaroo(x1: Int, v1: Int, x2: Int, v2: Int): String = {
    if (x2 < x1 && v1 > v2) {
      return "NO"
    }
    if (x2 > x1 && v1 < v2) {
      return "NO"
    }
    if (x2 > x1 && v2 == v1) {
      return "NO"
    }
    if (x2 < x1 && v2 == v1) {
      return "NO"
    }
    val jumps = Math.abs(x2 - x1) % Math.abs(v1 - v2)
    if (jumps == 0) {
      "YES"
    } else {
      "NO"
    }
  }

  @Test
  def getTotalX: Unit = {
    print(getTotalX(Array(2, 4), Array(16, 32, 96)))
  }

  def getTotalX(a: Array[Int], b: Array[Int]): Int = {
    var output = 0
    a.max to b.min foreach (number => {
      var condition = true
      a.foreach(value => {
        if (number % value > 0) {
          condition = false
        }
      })
      b.foreach(value => {
        if (value % number > 0) {
          condition = false
        }
      })
      if (condition) {
        output += 1
      }
    })
    output
  }

  @Test
  def breakingRecords: Unit = {
    print(breakingRecords(Array(0, 9, 3, 10, 2, 20)).mkString(" "))
  }

  def breakingRecords(score: Array[Int]): Array[Int] = {
    var best = 0
    var worst = 0
    var bestCount = 0
    var worstCount = 0
    var first = true
    score.foreach(score => {
      if (first) {
        best = score
        worst = score
        first = false
      } else {
        if (best < score) {
          best = score
          bestCount += 1
        }
        if (worst > score) {
          worst = score
          worstCount += 1
        }
      }
    })
    Array(bestCount, worstCount)
  }

  @Test
  def solve: Unit = {
    print(solve(5, Array(1, 2, 1, 3, 2), 3, 2))

  }


  def solve(n: Int, s: Array[Int], d: Int, m: Int): Int = {
    (0 to n).flatMap(position => {
      var index = 0
      var total = 0
      (position until s.length)
        .map(i => {
          total += s(i)
          index += 1
          (total, index)
        })
        .filter(tuple => tuple._1 == d && tuple._2 == m)
    }).toList
      .length
  }

  @Test
  def divisibleSumPairs: Unit = {
    print(divisibleSumPairs(6, 3, Array(1, 3, 2, 6, 1, 2)))
  }

  def divisibleSumPairs(n: Int, k: Int, ar: Array[Int]): Int = {
    ar.indices.flatMap(i => {
      ar.indices.filter(j => i < j && ((ar(i) + ar(j)) % k == 0))
    }).toList.length
  }

  @Test
  def migratoryBirds: Unit = {
    print(migratoryBirds(Array(1, 4, 4, 4, 5, 3)))
  }

  def migratoryBirds(ar: Array[Int]): Int = {
    var previousBird = 0
    var countBird = 1
    var birds: Map[Int, Int] = Map()
    ar.sorted foreach (bird => {
      if (bird == previousBird) {
        countBird += 1
      } else {
        countBird = 1
      }
      previousBird = bird
      birds = birds ++ Map(bird -> countBird)
    })
    var maxCount = 0
    var birdType = birds.keys.head
    birds foreach (entry => {
      if (entry._2 == maxCount && entry._1 < birdType) {
        maxCount = entry._2
        birdType = entry._1
      }
      if (entry._2 > maxCount) {
        maxCount = entry._2
        birdType = entry._1
      }
    })
    birdType
  }

  @Test
  def notRepeatedWords: Unit = {
    val sentence = "This is a for is repeated many many times this it should be repeated removed duplicity"

    var output: List[String] = List()
    sentence.split(" ").foreach(word => {
      if (!output.contains(word)) {
        output = output ++ Array(word)
      }
    })

    print(output.mkString(" "))
  }

  @Test
  def findElementInMatrix(): Unit = {
    val element = 16
    val matrix: Array[Array[Int]] = new Array(4)
    matrix.update(0, Array(1, 5, 13, 29))
    matrix.update(1, Array(11, 16, 25, 38))
    matrix.update(2, Array(45, 49, 52, 57))
    matrix.update(3, Array(51, 54, 59, 66))

    var founded = false
    0 until matrix.length - 1 foreach (i => {
      0 until matrix.length - 1 foreach (j => {
        if (matrix(i)(j) == element) {
          founded = true
        }
      })
    })
    print(founded)
  }

  @Test
  def increaseAndDecrease(): Unit = {
    increaseAndDecrease(Array(6, 5, 4, 3, 2, 8, 9, 10, 11, 1, 7))
  }

  def increaseAndDecrease(array: Array[Int]): Unit = {
    val middle = array.length - 1 / 2
    if (array.length >= 3) {
      val left = array(middle - 1)
      val right = array(middle + 1)
      val center = array(middle)
      if (center > left && center < right) {
        println(center)
      } else {
        increaseAndDecrease()
      }

    }

  }

  @Test
  def secondLarger(): Unit = {
    secondLarger(Array(6, 5, 4, 3, 2, 8, 9, 10, 11, 1, 7))
  }

  var first = 0
  var second = 0

  def secondLarger(array: Array[Int]): Unit = {
    array.foreach(_ => {
      array.foreach(value => {
        if (value > first) first = value
        if (value > second && value < first) second = value
      })
    })
    print(second)
  }

  @Test
  def binarySearchFirstOcurence(): Unit = {
    print(binarySearchFirstOrLastOcurence(Array(1, 2, 3, 3, 4, 5, 6, 6, 7, 8), 6))
  }

  var result: Integer = -1

  def binarySearchFirstOrLastOcurence(array: Array[Int], node: Int): Integer = {
    if (array.length > 1) {
      val middle = array.length / 2
      val center = array(middle)
      val (left, right) = array.splitAt(middle)
      if (node == center) {
        result = middle
        binarySearchFirstOrLastOcurence(left, node)//If we want last ocurrence just modify left by right
      } else if (node < center) {
        binarySearchFirstOrLastOcurence(left, node)
      } else {
        binarySearchFirstOrLastOcurence(right, node)
      }
    }
    result
  }
}

