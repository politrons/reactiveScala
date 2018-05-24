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
        val valueX = arr(j)
        val valueJ = if (j == 0) arr(0) else arr(j - 1)
        if (valueX < valueJ) {
          arr(j) = arr(j - 1)
          println(arr.mkString(" "))
          arr(j - 1) = valueX
        }
      })
    })
    println(arr.mkString(" "))
  }

  @Test
  def insertionSort2: Unit = {
    insertionSort2(5, Array(1, 4, 3, 5, 6, 2))

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
    * again over  in the second array. That's the reason why we ise the variable sortedIndex
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
  def quickSort: Unit = {
    println(quickSort(Array(4, 5, 3, 7, 2)).mkString(" "))
  }

  def quickSort(arr: Array[Int]): Array[Int] = {
    var left: Array[Int] = Array()
    val eq = Array(arr.head)
    var right: Array[Int] = Array()
    arr.foreach(value => {
      if (value > arr.head) {
        right = right ++ Array(value)
      } else if (value < arr.head) {
        left = left ++ Array(value)
      }
    })
    left ++ eq ++ right
  }

  //####### Counting sort
  @Test
  def countingSort: Unit = {
    print(countingSort(Array(1, 1, 3, 2, 1)).mkString(" "))
  }

  /**
    * In counter sort algorithm we create a initial array a range as the max value from the original array just with zeros.
    * Then in the original array we just use the value in every iteration to be used as the index of the counter array
    * and in every index we increase the counter.
    *
    * At the end, run through your counter array, printing the value of each non-zero valued index that number of times.
    */
  def countingSort(arr: Array[Int]): Array[Int] = {
    var counter: Array[Int] = Array()
    0 to arr.max foreach (_ => {
      counter = counter ++ Array[Int](0)
    })
    arr foreach (value => {
      counter(value) = counter(value) + 1
    })
    var sortedArray:Array[Int] = Array()
    counter.indices foreach(i => {
      if (counter(i) > 0)
        0 until counter(i) foreach(_ => {
          sortedArray = sortedArray ++ Array(i)
        })
    })
    sortedArray
  }
}

