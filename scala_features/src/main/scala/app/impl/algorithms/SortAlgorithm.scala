package app.impl.algorithms

import org.junit.Test

import scala.collection.immutable.{List, Map}

class SortAlgorithm {

  //##########################
  //        SORTING
  //##########################

  def introTutorial: Unit = {
    print(introTutorial(4, Array(1, 4, 5, 7, 9, 12)))
  }

  def introTutorial(V: Int, arr: Array[Int]): Int = {
    var output: Int = 0
    arr.indices.filter(i => output == 0 && arr(i) == V)
      .foreach(i => output = i)
    output
  }

  //####### Insertion sort

  @Test
  def insertionSort1: Unit = {
    insertionSort1(5, Array(2, 4, 6, 8, 3))
  }

  def insertionSort1(n: Int, arr: Array[Int]) {
    arr.length - 1 to 0 by -1 foreach (_ => {
      arr.length - 1 to 0 by -1 foreach (j => {
        val valueJ = if (j == 0) arr(0) else arr(j - 1)
        if (arr(j) < valueJ) {
          val tmp = arr(j)
          arr(j) = arr(j - 1)
          println(arr.mkString(" "))
          arr(j - 1) = tmp
        }
      })
    })
    println(arr.mkString(" "))
  }

  @Test
  def insertionSort2: Unit = {
    insertionSort2(5, Array(1, 4, 3, 6, 5, 2))

  }

  def insertionSort2(n: Int, arr: Array[Int]) {
    arr.indices foreach (i => {
      val count = i
      if (count > 0) println(arr.mkString(" "))
      arr.indices foreach (_ => {
        0 to count foreach (i => {
          val nextIndex = if (i == arr.length - 1) arr.length - 1 else i + 1
          val valL = arr(i)
          val valR = arr(nextIndex)
          if (valL > valR) {
            val tmp = arr(nextIndex)
            arr(nextIndex) = arr(i)
            arr(i) = tmp
          }
        })
      })
    })

  }

  @Test
  def bigSorting: Unit = {
    print(bigSorting(Array("31415926535897932384626433832795", "1", "3", "10", "3", "5")).mkString(" "))
  }

  def bigSorting(unsorted: Array[String]): Array[String] = {
    var lastIndexFound = unsorted.length - 1
    0 until lastIndexFound foreach (x => {
      var isSorted = false
      0 until lastIndexFound foreach (j => {
        val rightIndex = if (j == unsorted.length - 1) unsorted.length - 1 else j + 1
        if (BigDecimal(unsorted(j)) > BigDecimal(unsorted(rightIndex))) {
          val tmp = unsorted(j)
          unsorted(j) = unsorted(rightIndex)
          unsorted(rightIndex) = tmp
          isSorted = true
        }
      })
      if (!isSorted) {
        return unsorted
      }
      lastIndexFound -= x
    })
    unsorted
    //    var last: = ""
    //    unsorted.sortBy(a => a)
    //#########
    //Efficient
    //#########
    //    unsorted.sortWith((a, b) => BigDecimal(a) < BigDecimal(b))
    //    unsorted.sortWith((a, b) => {
    //      if (a.length > Integer.MAX_VALUE && b.length > Integer.MAX_VALUE) {
    //        BigDecimal(a) < BigDecimal(b)
    //      }
    //      else if (a.length < Integer.MAX_VALUE && b.length > Integer.MAX_VALUE) {
    //        a.toInt < BigDecimal(b)
    //      } else if (a.length > Integer.MAX_VALUE && b.length < Integer.MAX_VALUE) {
    //        BigDecimal(a) < b.toInt
    //      } else {
    //        a.toInt < b.toInt
    //      }
    //    })
  }

  @Test
  def runningTime: Unit = {
    println(runningTime(Array(2, 1, 3, 1, 2)))
  }

  /**
    * It's important to know that every iteration of the first array it means we move
    * the higher element to the right of the array. So there's no point to compare again and iterate
    * again over  in the second array. That's the reason why we use the variable sortedIndex
    *
    * @param arr
    * @return
    */
  def runningTime(arr: Array[Int]): Int = {
    var shifted = 0
    var sortedIndex: Int = arr.length - 1
    var isSorted = false
    0 to sortedIndex foreach (_ => {
      0 until sortedIndex foreach (i => {
        if (arr(i) > arr(i + 1)) {
          val tmp = arr(i)
          arr(i) = arr(i + 1)
          arr(i + 1) = tmp
          shifted += 1
          isSorted = true
        }
      })
      if (!isSorted) {
        return shifted
      }
      sortedIndex -= 1
    })
    shifted
  }

  //####### Divide and conquer
  /**
    * Divide the array into three left eq and right
    * eq only contain the first element of the array.
    * iterate the array and if the value is higher than the first one we move to the right
    * and otherwise it goes to the left
    */
  @Test
  def quickSort() {
    val array = Array(8, 5, 3, 7, 2, 4, 10, 1)
    print(quickSort(array).mkString(" "))
  }

  def quickSort(array: Array[Int]): Array[Int] =
    if (array.length < 2) array
    else {
      val pivot = array(array.length / 2)
      val leftArray = quickSort(array.filter(e => pivot > e))
      val rightArray = quickSort(array.filter(e => pivot < e))
      leftArray ++ Array(pivot) ++ rightArray
    }


  //Merge sort

  @Test
  def mergeSort(): Unit = {
    val array = Array(45, 23, 11, 89, 77, 98, 4, 28, 65, 43)
    print(divideParts(array).mkString(" "))
  }

  /**
    * We first divide all elements of the array in left and right from the middle.
    * Until we have the undivide since we have simple numbers. And from there we go from
    * inside out.
    */
  def divideParts(array: Array[Int]): Array[Int] = {
    val middle = array.length / 2
    if (middle == 0) array // no more elements to split.
    else {
      val (left, right) = array.splitAt(middle)
      val arrayOne = divideParts(left)
      val arrayTwo = divideParts(right)
      mergeParts(arrayOne, arrayTwo)
    }
  }

  def mergeParts(arrayOne: Array[Int], arrayTwo: Array[Int]): Array[Int] = {
    var orderArray: Array[Int] = Array()
    var a = arrayOne
    var b = arrayTwo
    while (!a.isEmpty && !b.isEmpty) {
      if (a(0) > b(0)) {
        orderArray = orderArray ++ Array(b(0))
        b = b.filter(e => e != b(0))
      } else {
        orderArray = orderArray ++ Array(a(0))
        a = a.filter(e => e != a(0))
      }
    }
    //Now a or b should empty. But we put the rest not compared at the end of the order array
    while (!a.isEmpty) {
      orderArray = orderArray ++ Array(a(0))
      a = a.filter(e => e != a(0))
    }
    while (!b.isEmpty) {
      orderArray = orderArray ++ Array(b(0))
      b = b.filter(e => e != b(0))
    }
    orderArray
  }


  //####### Counting sort
  @Test
  def countingSort: Unit = {
    print(countingSort(Array(1, 4, 1, 3, 5, 6, 2, 7, 1)).mkString(" "))
  }

  /**
    * In counter sort algorithm we create a initial array a range as the max value from the original array just with zeros.
    * Then in the original array we just use the value in every iteration to be used as the index of the counter array
    * and in every index we increase the counter.
    *
    * At the end, run through your counter array, printing the value of each non-zero valued index that number of times in case the number was repeated.
    */
  def countingSort(arr: Array[Int]): Array[Int] = {
    var counter: Array[Int] = Array()
    0 to arr.max foreach (_ => {
      counter = counter ++ Array[Int](0)
    })
    arr foreach (value => {
      counter(value) = counter(value) + 1
    })
    var sortedArray: Array[Int] = Array()
    counter.indices foreach (i => {
      if (counter(i) > 0)
        0 until counter(i) foreach (_ => {
          sortedArray = sortedArray ++ Array(i)
        })
    })
    sortedArray
  }

  @Test
  def findMedian: Unit = {
    print(findMedian(Array(0, 1, 2, 4, 6, 5, 3)))
  }

  /**
    * For find median we use counting sort algorithm
    */
  def findMedian(arr: Array[Int]): Int = {
    val counter = (0 to arr.max).map(_ => 0).toArray
    arr foreach (value => {
      counter(value) = counter(value) + 1
    })
    var output: Array[Int] = Array()
    counter.indices foreach (i => {
      if (counter(i) > 0) {
        0 until counter(i) foreach (_ => {
          output = output ++ Array(i)
        })
      }
    })
    println(output.mkString(" "))
    arr((output.length - 1) / 2)
    //    Efficient
    //    val x = arr.sorted
    //    x((arr.length - 1) / 2)
  }

  @Test
  def activityNotifications: Unit = {
    println(activityNotifications(Array(2, 3, 4, 2, 3, 6, 8, 4, 5), 5))
    println(activityNotifications(Array(1, 2, 3, 4, 4), 4))
  }

  def activityNotifications(expenditure: Array[Int], d: Int): Int = {
    var alarm: Int = 0
    var from = 0
    var lastDay = d
    val average = new Array[Int](d)
    lastDay until expenditure.length foreach (expense => {
      Array.copy(expenditure, from, average, 0, d)
      val sorted = average.sorted
      if (expense >= sorted(d / 2) * 2) {
        alarm += 1
      }
      from += 1
      lastDay += 1
    })
    alarm
  }

  /**
    * In this example we use counter sorting technique.
    * We sum all arrays and we use this efficient algorithm.
    */
  @Test
  def sortMultipleArraysByCounter(): Unit = {
    val a1 = Array(1, 5, 8, 9, 11)
    val a2 = Array(2, 12, 24, 44)
    val a3 = Array(1, 10, 20, 40, 60)

    val total = a1 ++ a2 ++ a3

    val counter: Array[Int] = new Array(total.max + 1)
    total foreach (e => {
      counter(e) = counter(e) + 1
    })

    var output: Array[Int] = Array()
    counter.indices foreach (i => {
      if (counter(i) > 0) {
        0 until counter(i) foreach (_ => {
          output = output ++ Array(i)
        })
      }
    })
    print(output.mkString(" "))
  }

  @Test
  def sortMultipleArraysByQuickSort(): Unit = {
    val a1 = Array(1, 5, 8, 9, 11)
    val a2 = Array(2, 12, 24, 44)
    val a3 = Array(1, 10, 20, 40, 60)

    var total = a1 ++ a2 ++ a3

    0 until total.length - 1 foreach (i => {
      val eq = total(i)
      var left: Array[Int] = Array()
      var right: Array[Int] = Array()
      total foreach (value => {
        if (eq > value) {
          left = left ++ Array(value)
        } else if (eq < value) {
          right = right ++ Array(value)
        }
      })
      total = left ++ Array(eq) ++ right
    })
    print(total.mkString(" "))
  }

  @Test
  def bubbleSort(): Unit = {
    val array = Array(4, 2, 6, 1, 3, 5, 9, 7, 8)
    0 until array.length - 1 foreach (_ => {
      0 until array.length - 1 foreach (i => {
        if (array(i) > array(i + 1)) {
          val tmp = array(i + 1)
          array(i + 1) = array(i)
          array(i) = tmp
        }
      })
    })
    print(array.mkString(" "))
  }

  @Test
  def quickSortFast(): Unit = {
    var array = Array(8, 5, 3, 7, 2, 4, 10, 1)
    array.indices foreach (i => {
      val eq = array(i)
      var left: Array[Int] = Array()
      var right: Array[Int] = Array()
      array.indices foreach (j => {
        if (array(j) < eq) {
          left = left ++ Array(array(j))
        } else if (array(j) > eq) {
          right = right ++ Array(array(j))
        }
      })
      array = left ++ Array(eq) ++ right
    })
    print(array.mkString(" "))
  }

  @Test
  def quickSortFast2(): Unit = {
    val array = Array(8, 5, 3, 7, 2, 4, 10, 1)
    print(quickSortFast2(array).mkString(" "))
  }

  def quickSortFast2(array: Array[Int]): Array[Int] = {
    if (array.length == 0) {
      return array
    }
    val pivot = array(array.length / 2)
    val left = quickSortFast2(array.filter(e => pivot > e))
    val right = quickSortFast2(array.filter(e => pivot < e))
    left ++ Array(pivot) ++ right
  }

  /**
    * Divide and conquer since it chop the half of the data in every iteration is consider one of the most efficients
    * algorthims and containsRE: Personal tax payment the Big O grasde O(log n)
    */
  @Test
  def findNumberWithDivideAndCoquer(): Unit = {
    print(divideAndConquer(Array(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15), 13))
  }

  def divideAndConquer(array: Array[Int], number: Int): Unit = {
    val middle = array((array.length - 1) / 2)
    if (middle != number) {
      println("Chopping")
      val (left, right) = array.splitAt((array.length - 1) / 2)
      if (middle > number) {
        divideAndConquer(left, number)
      } else {
        divideAndConquer(right, number)
      }
    } else {
      println(s"Founded $middle")
    }
  }

  @Test
  def closestNumbers: Unit = {
    println(closestNumbers(Array(-20, -3916237, -357920, -3620601, 7374819, -7330761, 30, 6246457, -6461594, 266854)).mkString(" "))
    //    println(closestNumbers(Array(-20, -3916237, -357920, -3620601, 7374819, -7330761, 30, 6246457, -6461594, 266854, -520, -470)).mkString(" "))
  }

  def closestNumbers(arr: Array[Int]): Array[Int] = {

    val totalMap: Map[(Int, Int), Int] =
      arr.flatMap(valueA => {
        arr.filter(valueB => (valueA - valueB) > 0)
          .map(valueB => Map[(Int, Int), Int]((valueA, valueB) -> (valueA - valueB)))
      }).reduce((m, m1) => m ++ m1)

    val entriesArray = totalMap.iterator.toArray
    totalMap foreach (_ => {
      entriesArray.indices foreach (i => {
        val rightIndex = if (i == entriesArray.length - 1) entriesArray.length - 1 else i + 1
        if (entriesArray(i)._2 > entriesArray(rightIndex)._2) {
          val tmp = entriesArray(rightIndex)
          entriesArray(rightIndex) = entriesArray(i)
          entriesArray(i) = tmp
        }
      })
    })

    var minDiffer = entriesArray.head._2
    val output: Array[Int] =
      entriesArray.filter(entry => entry._2 <= minDiffer)
        .flatMap(entry => {
          minDiffer = entry._2
          Array(entry._1._2, entry._1._1)
        })
    output
  }

}

